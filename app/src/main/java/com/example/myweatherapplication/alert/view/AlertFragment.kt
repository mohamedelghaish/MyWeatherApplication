package com.example.myweatherapplication.alert.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myweatherapplication.database.AlertState
import com.example.myweatherapplication.R
import com.example.myweatherapplication.alert.viewmodel.AlertViewModel
import com.example.myweatherapplication.alert.viewmodel.AlertViewModelFactory
import com.example.myweatherapplication.database.LocalDataSourceImp
import com.example.myweatherapplication.databinding.FragmentAlertBinding
import com.example.myweatherapplication.databinding.SelectTimeBinding
import com.example.myweatherapplication.favorite.view.AlertAdapter
import com.example.myweatherapplication.model.Repository
import com.example.myweatherapplication.model.SavedAlerts
import com.example.myweatherapplication.network.RemoteDataSourceImp
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


class AlertFragment : Fragment(), OnAlertDeleteClickListener {
    private  val TAG = "AlertFragment"

    private lateinit var binding: FragmentAlertBinding
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var viewModel: AlertViewModel
    private lateinit var alert: SavedAlerts
    private var alarmStartYear: Int = 0
    private var alarmStartMonth: Int = 0
    private var alarmStartDay: Int = 0
    private var alarmStartHour: Int = 0
    private var alarmStartMinute: Int = 0
    private var Delay: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initViewModel()
        updateUI()
        
    }

    private fun initUI() {
        alertAdapter = AlertAdapter(this)
        binding.recyclerViewAlerts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewAlerts.adapter = alertAdapter
        binding.addAlertsFloatingActionButton.setOnClickListener {
            showTimeChooser()
        }
    }

    private fun initViewModel() {
        val viewModelFactory = AlertViewModelFactory(
            Repository.getInstance(
                RemoteDataSourceImp.getInstance(),
                LocalDataSourceImp(requireContext())
            )
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[AlertViewModel::class.java]

        viewModel.getStoredAlerts()
    }

    private fun updateUI() {
        lifecycleScope.launch{
           viewModel._alertResponse.collectLatest {
               when(it){
                   is AlertState.Loading->{
                       binding.recyclerViewAlerts.visibility = View.GONE
                       binding.noAlertsImageView.visibility = View.VISIBLE
                       binding.noAlertsTextView.visibility = View.VISIBLE
                   }
                   is AlertState.Success->{
                       if (it.data.isEmpty()) {
                           binding.recyclerViewAlerts.visibility = View.GONE
                           binding.noAlertsImageView.visibility = View.VISIBLE
                           binding.noAlertsTextView.visibility = View.VISIBLE
                       }else {
                           binding.noAlertsImageView.visibility = View.GONE
                           binding.noAlertsTextView.visibility = View.GONE
                           binding.recyclerViewAlerts.visibility = View.VISIBLE
                           alertAdapter.setList(it.data)
                       }
                   }
                   else->{}
               }
           }
        }
    }

    private fun showTimeChooser() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext()).create()
        val bindingAlert = SelectTimeBinding.inflate(LayoutInflater.from(requireContext()))
        alertDialogBuilder.setView(bindingAlert.root)

        bindingAlert.startDateLinerLayout.setOnClickListener {
            showDatePicker(bindingAlert.alertStartDate, bindingAlert.alertStartHour, true)
        }

        bindingAlert.endDateLinerLayout.setOnClickListener {
            showDatePicker(bindingAlert.alertEndDate, bindingAlert.alertEndHour, false)
        }

        bindingAlert.alertSaveButton.setOnClickListener {
            Delay = checkDates()
            val repetitions =
                calculateDifferenceBetweenDates(
                    bindingAlert.alertStartDate.text.toString(),
                    bindingAlert.alertEndDate.text.toString()
                )
            if (repetitions > -1) {
                if (Delay > 0) {
                    insertToRoom(
                        bindingAlert.alertStartDate.text.toString(),
                        bindingAlert.alertEndDate.text.toString(),
                        bindingAlert.alertStartHour.text.toString(),
                        bindingAlert.alertEndHour.text.toString(), repetitions
                    )
                    setupAlertRequest()
                    alertDialogBuilder.dismiss()
                    viewModel.getStoredAlerts()
                } else {
                    showSankBar(bindingAlert.root, getString(R.string.alert_start_time_error))
                }
            } else {
                showSankBar(bindingAlert.root, getString(R.string.alert_end_time_error))
            }
        }
        alertDialogBuilder.setCanceledOnTouchOutside(false)
        alertDialogBuilder.show()
    }

    private fun showDatePicker(date: TextView, time: TextView, isStart: Boolean) {
        val calender = Calendar.getInstance()
        val day: Int = calender.get(Calendar.DAY_OF_MONTH)
        val month: Int = calender.get(Calendar.MONTH)
        val year: Int = calender.get(Calendar.YEAR)

        DatePickerDialog(
            requireContext(),
            { _, yearIndex, monthIndex, dayIndex ->
                if (isStart) {
                    alarmStartYear = yearIndex
                    alarmStartMonth = monthIndex + 1
                    alarmStartDay = dayIndex
                }
                date.text = formatDate("$dayIndex/${monthIndex + 1}/$yearIndex")
                showTimePicker(time, isStart)
            }, year, month, day
        ).show()
    }

    private fun formatDate(stringDate: String): String {
        val stringFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
        return dateFormat.format(stringFormat.parse(stringDate)!!)
    }

    private fun showTimePicker(time: TextView, isStart: Boolean) {
        val calender = Calendar.getInstance()
        val hour: Int = calender.get(Calendar.HOUR)
        val minute: Int = calender.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _: TimePicker, hourIndex: Int, minuteIndex: Int ->
            if (isStart) {
                alarmStartHour = hourIndex
                alarmStartMinute = minuteIndex
            }
            if (hourIndex >= 12)
                time.text =
                    DecimalFormat("#").format(hourIndex).plus(
                        ":" + DecimalFormat("#").format(
                            minuteIndex
                        ) + requireContext().getString(
                            R.string.PM
                        )
                    )
            else
                time.text =
                    DecimalFormat("#").format(hourIndex).plus(
                        ":" + DecimalFormat("#").format(
                            minuteIndex
                        ) + requireContext().getString(
                            R.string.AM
                        )
                    )
        }, hour, minute, false).show()
    }

    private fun checkDates(): Long {
        val dates = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        val currentTime = Calendar.getInstance().timeInMillis
        val startTime =
            dates.parse("$alarmStartYear/$alarmStartMonth/$alarmStartDay $alarmStartHour:$alarmStartMinute")!!
                .time
        val initialDelay: Long = startTime - currentTime
        return initialDelay
    }

    private fun insertToRoom(
        startDate: String,
        endDate: String,
        startHour: String,
        endHour: String,
        repetitions: Long
    ) {
        alert = SavedAlerts(
            startDate,
            endDate,
            startHour,
            endHour,
            repetitions,
            System.currentTimeMillis()
        )
        
        viewModel.insertAlert(alert)

    }

    private fun calculateDifferenceBetweenDates(startDate: String, endDate: String): Long {
        val dates = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
        val difference: Long =
            dates.parse(endDate)!!.time - dates.parse(startDate)!!.time
        val differenceDates = difference / (24 * 60 * 60 * 1000)
        return differenceDates
    }

    private fun setupAlertRequest() {

        val reminderRequest = OneTimeWorkRequestBuilder<AlarmNotify>().setInitialDelay(
            Delay,
            TimeUnit.MILLISECONDS
        )
            .addTag(alert.tag.toString())
            .build()
        Log.i(TAG, "setupAlertRequest: tag-----------> ${alert.tag}")
        WorkManager.getInstance(requireContext()).enqueue(reminderRequest)
    }

    private fun showSankBar(rootView: View, message: String) {
        val snackBar = Snackbar.make(
            rootView,
            message,
            Snackbar.LENGTH_SHORT
        ).setActionTextColor(Color.WHITE)
        snackBar.view.setBackgroundColor(Color.RED)
        snackBar.show()
    }

    override fun delete(id: Int, tag: Long) {
        showDialog(id, tag)
    }

    private fun showDialog(id: Int, tag: Long) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.item_dialog, null)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        val dialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.btn_yes).setOnClickListener {
            viewModel.deleteAlertFromRoom(id)

            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_no).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}
package com.example.myweatherapplication.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myweatherapplication.ApiState
import com.example.myweatherapplication.Const
import com.example.myweatherapplication.R
import com.example.myweatherapplication.databinding.FragmentHomeBinding
import com.example.myweatherapplication.home.viewmodel.HomeViewModel
import com.example.myweatherapplication.model.WeatherResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var  viewModel:HomeViewModel
    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    lateinit var hourAdapter: HourAdapter
    lateinit var hourManager: LinearLayoutManager
    lateinit var dayAdapter: DayAdapter
    lateinit var dayManager: LinearLayoutManager
    private val REQUEST_CODE_LOCATION_PERMISSION = 5
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var long:String
    lateinit var lat :String
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
       // viewModel =ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        //return inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hourAdapter = HourAdapter()
        hourManager = LinearLayoutManager(requireContext())

        dayAdapter= DayAdapter()
        dayManager = LinearLayoutManager(requireContext())



        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences("location",Context.MODE_PRIVATE)
        lat = sharedPreferences.getString("latitude","0")!!.toString()
        long = sharedPreferences.getString("longitude","0")!!.toString()

        //viewModel.getWeatherFromNetwork(lat,long)
        lifecycleScope.launch(Dispatchers.Main) {viewModel.getWeatherFromNetwork(lat,long) }
//        viewModel.weatherResponse.observe(viewLifecycleOwner){
//            setData(it)
//        }

        lifecycleScope.launch {
            viewModel._weatherResponse.collectLatest {
                when(it){
                    is ApiState.Success->{
                        setData(it.data)
                    }
                    else->{
                        Toast.makeText(
                            requireContext(),
                            "there is a problem",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    private fun getTemperature(temp: Double): String {
        val temperature: String
        when (Const.tempUnit) {
            "celsius" -> {
                temperature = DecimalFormat("#").format(temp - 273.15)
                binding.tvTempUnit.text =
                    resources.getString(R.string.temperature_celsius_unit)
                binding.tvMaxTempUnit.text =
                    resources.getString(R.string.temperature_celsius_unit)
            }
            "fahrenheit" -> {
                temperature = DecimalFormat("#").format(((temp - 273.15) * 1.8) + 32)
                binding.tvTempUnit.text =
                    resources.getString(R.string.temperature_fahrenheit_unit)
                binding.tvMaxTempUnit.text =
                    resources.getString(R.string.temperature_fahrenheit_unit)
            }
            else -> {
                temperature = DecimalFormat("#").format(temp)
                binding.tvTempUnit.text =
                    resources.getString(R.string.temperature_kelvin_unit)
                binding.tvMaxTempUnit.text =
                    resources.getString(R.string.temperature_kelvin_unit)
            }
        }
        return temperature
    }

    private fun getWindSpeed(weatherResponse: WeatherResponse): String {
        val windSpeed = when (Const.windSpeedUnit) {
            "M/H" -> DecimalFormat("#.##").format(weatherResponse.list.get(0).wind.speed * 3.6) + " " + getString(
                R.string.wind_speed_unit_MH
            )
            else -> DecimalFormat("#.##").format(weatherResponse.list.get(0).wind.speed) + " " + getString(
                R.string.wind_speed_unit_MS)

        }
        return windSpeed
    }

    private fun setData(it:WeatherResponse){
        binding.tvDescription.text = it.list.get(0).weather.get(0).description
        binding.ivIcon
        Glide.with(requireContext())
            .load(
                "https://openweathermap.org/img/wn/" +
                        it.list.get(0).weather.get(0).icon + "@2x.png"
            )
            .into(binding.ivIcon)

        binding.tvDate.text= convertDate(it.list.get(0).dt_txt)

        binding.tvTempDegree.text = getTemperature(it.list.get(0).main.temp)
        binding.tvCity.text = it.city.name

        binding.tvMaxTemp.text = getTemperature(it.list.get(0).main.temp_max)

        binding.tvWindSpeed.text = getWindSpeed(it)
        binding.tvHumidity.text = it.list.get(0).main.humidity.toString() + "%"
        binding.tvCloudInfo.text = it.list.get(0).clouds.all.toString() + "%"
        binding.tvPressure.text = it.list.get(0).main.pressure.toString() + "hPa"
        binding.tvVisibilityInfo.text = it.list.get(0).visibility.toString() + "m"


        binding.RVHourlyInfo.adapter = hourAdapter
        hourAdapter.submitList(it.list.subList(0, 8))
        hourManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.RVHourlyInfo.layoutManager = hourManager

        binding.RVWeekInfo.adapter = dayAdapter
        dayAdapter.submitList(it.list.chunked(8))
        dayManager.orientation = LinearLayoutManager.VERTICAL
        binding.RVWeekInfo.layoutManager = dayManager
    }
    fun formatDate(dateInSeconds: Long): String {
        val time = dateInSeconds * 1000.toLong()
        val date = Date(time)
        val dateFormat = SimpleDateFormat("d MMM yyyy", Locale(Const.language))
        return dateFormat.format(date)
    }

    fun convertDate(inputDateString:String):String{
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE MMM dd | hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(inputDateString)
        val outputDateString = outputFormat.format(date)
        return outputDateString
    }


}
package com.example.myweatherapplication.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myweatherapplication.R
import com.example.myweatherapplication.databinding.FragmentHomeBinding
import com.example.myweatherapplication.home.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class HomeFragment : Fragment() {

    private lateinit var  viewModel:HomeViewModel
    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    lateinit var hourAdapter: HourAdapter
    lateinit var hourManager: LinearLayoutManager
    lateinit var dayAdapter: DayAdapter
    lateinit var dayManager: LinearLayoutManager


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
         viewModel.getWeatherFromNetwork(31.2596451.toString(),30.0210898.toString())
        viewModel.weatherResponse.observe(viewLifecycleOwner){


            binding.tvDescription.text= it.list.get(0).weather.get(0).description
            binding.ivIcon
            Glide.with(this)
                .load("https://openweathermap.org/img/wn/" +
                        it.list.get(0).weather.get(0).icon + "@2x.png")
                .into(binding.ivIcon)
            binding.tvDate.text= it.list.get(0).dt_txt
            val temperature: String
            temperature = DecimalFormat("#").format(it.list.get(0).main.temp - 273.15)
            binding.tvTempDegree.text = temperature +
                resources.getString(R.string.temperature_celsius_unit)
            binding.tvCity.text = it.city.name
            val maxTemp :String
            maxTemp = DecimalFormat("#").format(it.list.get(0).main.temp_max -273.15 )
            binding.tvMaxTemp.text = maxTemp +
             resources.getString(R.string.temperature_celsius_unit)
            binding.tvWindSpeed.text = it.list.get(0).wind.speed.toString() + "m/s"
            binding.tvHumidity.text = it.list.get(0).main.humidity.toString() + "%"
            binding.tvCloudInfo.text = it.list.get(0).clouds.all.toString() + "%"
            binding.tvPressure.text= it.list.get(0).main.pressure.toString()
            binding.tvVisibilityInfo.text = it.list.get(0).visibility.toString() + "m"


            binding.RVHourlyInfo.adapter = hourAdapter
            hourAdapter.submitList(it.list.subList(0,8))
            hourManager.orientation = LinearLayoutManager.HORIZONTAL
            binding.RVHourlyInfo.layoutManager = hourManager

            binding.RVWeekInfo.adapter= dayAdapter
            dayAdapter.submitList(it.list.chunked(8).flatten())
            dayManager.orientation=LinearLayoutManager.VERTICAL
            binding.RVWeekInfo.layoutManager = dayManager



        }

    }

}
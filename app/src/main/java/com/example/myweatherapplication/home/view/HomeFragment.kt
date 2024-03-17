package com.example.myweatherapplication.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
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
    var long:Double = 0.0
    var lat :Double = 0.0


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





         //viewModel.getWeatherFromNetwork(31.2596451.toString(),30.0210898.toString())
        /*viewModel.getWeatherFromNetwork(lat.toString(),long.toString())
        viewModel.weatherResponse.observe(viewLifecycleOwner){


            binding.tvDescription.text= it.list.get(0).weather.get(0).description
            binding.ivIcon
            Glide.with(this)
                .load("https://openweathermap.org/img/wn/" +
                        it.list.get(0).weather.get(0).icon + "@2x.png")
                .into(binding.ivIcon)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            binding.tvDate.text= inputFormat.parse(it.list.get(0).dt_txt).toString()
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



        }*/

    }
    override fun onStart() {
        super.onStart()
        if (checkPermissions()){
            if (isLocationEnabled()){
                getFreshLocation()
            } else {
                enableLocationServices()
            }
        } else{
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_CODE_LOCATION_PERMISSION
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun getFreshLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location = locationResult.lastLocation
                     // location?.longitude!!
                      //location?.latitude!!
//                    location?.latitude?.let { location?.longitude?.let { it1 ->
//                        getAddressFromLocation(it,
//                            it1
//                        )
//                    } }
                    viewModel.getWeatherFromNetwork(location?.latitude!!.toString(),location?.longitude!!.toString())
                    viewModel.weatherResponse.observe(viewLifecycleOwner){it->viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {

                       setData(it)
                      }
                    }
                    fusedLocationProviderClient.removeLocationUpdates(this)
                }
            },
            Looper.myLooper()
        )
    }

    /*@SuppressLint("MissingPermission")
    fun getFreshLocation(onLocationReceived: (latitude: Double, longitude: Double) -> Unit) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location = locationResult.lastLocation
                    val latitude = location?.latitude ?: 0.0
                    val longitude = location?.longitude ?: 0.0

                    fusedLocationProviderClient.removeLocationUpdates(this)
                    // Invoke the callback with latitude and longitude
                    onLocationReceived(latitude, longitude)
                }
            },
            Looper.myLooper()
        )
    }*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==REQUEST_CODE_LOCATION_PERMISSION){
            if (grantResults.size>1&& grantResults.get(0)== PackageManager.PERMISSION_GRANTED)
                getFreshLocation()
        }
    }

    fun checkPermissions(): Boolean {
        return (requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    fun enableLocationServices(){
        Toast.makeText(requireContext(),"Turn on location", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
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
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        binding.tvDate.text = inputFormat.parse(it.list.get(0).dt_txt).toString()

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


}
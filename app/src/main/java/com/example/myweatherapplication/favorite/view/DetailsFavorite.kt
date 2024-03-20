package com.example.myweatherapplication.favorite.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myweatherapplication.ApiState
import com.example.myweatherapplication.Const
import com.example.myweatherapplication.R
import com.example.myweatherapplication.database.LocalDataSourceImp
import com.example.myweatherapplication.databinding.ActivityDetailsFavoriteBinding
import com.example.myweatherapplication.favorite.viewmodel.FavoriteViewModel
import com.example.myweatherapplication.favorite.viewmodel.FavoriteViewModelFactory
import com.example.myweatherapplication.home.view.DayAdapter
import com.example.myweatherapplication.home.view.HourAdapter
import com.example.myweatherapplication.model.Repository
import com.example.myweatherapplication.model.WeatherResponse
import com.example.myweatherapplication.network.RemoteDataSourceImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailsFavorite : AppCompatActivity() {
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var binding: ActivityDetailsFavoriteBinding
    private lateinit var hourAdapter: HourAdapter
    private lateinit var dayAdapter: DayAdapter
    lateinit var hourManager: LinearLayoutManager
    lateinit var dayManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_details_favorite)
        binding = ActivityDetailsFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        getData()
    }

    fun getData(){
        val latitude = intent.getStringExtra("LAT").toString()
        val longitude = intent.getStringExtra("LONG").toString()

        viewModel.getDataToFavoriteLocation(latitude,longitude,Const.language)

        lifecycleScope.launch {
            viewModel._favoriteLocationDetails.collectLatest {
                when(it){
                    is ApiState.Loading->{
                        binding.progressBarHome.visibility = View.VISIBLE
                        binding.homeLayout.visibility = View.GONE
                        binding.linearLayoutHome.visibility = View.GONE
                        binding.RVWeekInfo.visibility = View.GONE
                        binding.RVHourlyInfo.visibility= View.GONE
                    }
                    is ApiState.Success->{
                        binding.progressBarHome.visibility = View.GONE
                        binding.homeLayout.visibility = View.VISIBLE
                        binding.linearLayoutHome.visibility = View.VISIBLE
                        binding.RVWeekInfo.visibility = View.VISIBLE
                        binding.RVHourlyInfo.visibility=View.VISIBLE
                        setData(it.data)
                    }
                    else->{
                        Toast.makeText(
                            this@DetailsFavorite,
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

    private fun setData(it: WeatherResponse){
        binding.tvDescription.text = it.list.get(0).weather.get(0).description
        binding.ivIcon
        Glide.with(this)
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

    fun init(){
        hourAdapter = HourAdapter()
        hourManager = LinearLayoutManager(this)

        dayAdapter= DayAdapter()
        dayManager = LinearLayoutManager(this)

        val factory = FavoriteViewModelFactory(
            Repository.getInstance(
                this,
                RemoteDataSourceImp.getInstance(),
                LocalDataSourceImp.getInstance(this)

            ))
        viewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)
    }

}

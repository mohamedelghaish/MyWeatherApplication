package com.example.myweatherapplication.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweatherapplication.Const
import com.example.myweatherapplication.R

import com.example.myweatherapplication.model.WeatherEntry
import java.text.DecimalFormat
import androidx.recyclerview.widget.ListAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DayAdapter : ListAdapter<WeatherEntry, DayAdapter.ViewHolder>(WeatherEntryDiffCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObj = getItem(position)
        holder.day.text = getNameOfDay(currentObj.dt)
        holder.statusTxt.text = currentObj.weather.get(0).description
        val temperature: String
        temperature = DecimalFormat("#").format(currentObj.main.temp - 273.15)
        holder.tempreatureDegree.text = temperature + "°C"
        Glide.with(context)
            .load("https://openweathermap.org/img/wn/" +
                    currentObj.weather.firstOrNull()?.icon + "@2x.png")
            .into(holder.statusImg)


    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var day : TextView = view.findViewById(R.id.name_of_day_textView)
        var statusImg:ImageView = view.findViewById(R.id.status_of_day_imageView)
        var tempreatureDegree:TextView = view.findViewById(R.id.temperature_of_day_textView)
        var statusTxt:TextView = view.findViewById(R.id.status_of_day_textView)
    }

    private fun getNameOfDay(milliSeconds: Long): String {
        return SimpleDateFormat("EE").format(milliSeconds * 1000)
    }

    private fun convertTime(milliSeconds: Long): String {
        val time = milliSeconds * 1000.toLong()
        val format = SimpleDateFormat("h a", Locale(Const.language))
        return format.format(Date(time))
    }




    private class WeatherEntryDiffCallback : DiffUtil.ItemCallback<WeatherEntry>() {
        override fun areItemsTheSame(oldItem: WeatherEntry, newItem: WeatherEntry): Boolean {
            return oldItem.dt == newItem.dt
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: WeatherEntry, newItem: WeatherEntry): Boolean {
            return oldItem == newItem
        }
    }

    private fun convertTemperature(temp: Double): String {
        var temperature: String
        when (Const.tempUnit) {
            "celsius" -> {
                temperature = DecimalFormat("#").format(temp - 273.15)
                temperature += " " + context.getString(R.string.temperature_celsius_unit)
            }
            "fahrenheit" -> {
                temperature = DecimalFormat("#").format(((temp - 273.15) * 1.8) + 32)
                temperature += " " + context.getString(R.string.temperature_fahrenheit_unit)
            }

            else -> {
                temperature = DecimalFormat("#").format(temp)
                temperature += " " + context.getString(R.string.temperature_kelvin_unit)
            }
        }
        return temperature
    }
}


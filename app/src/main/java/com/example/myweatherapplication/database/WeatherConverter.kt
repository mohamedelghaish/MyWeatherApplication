package com.example.myweatherapplication.database

import androidx.room.TypeConverter
import com.example.myweatherapplication.model.City
import com.example.myweatherapplication.model.WeatherEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherConverter {

    @TypeConverter
    fun weatherEntry_fromJson(json: String): List<WeatherEntry> {
        val type = object : TypeToken<List<WeatherEntry>>() {}.type
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun weatherEntry_toJson(list: List<WeatherEntry>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun city_fromJson(json: String): City {
        return Gson().fromJson(json, City::class.java)
    }

    @TypeConverter
    fun city_toJson(city: City): String {
        return Gson().toJson(city)
    }

}
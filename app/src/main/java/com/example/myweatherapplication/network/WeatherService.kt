package com.example.myweatherapplication.network

import com.example.myweatherapplication.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("forecast")
    suspend fun getWeatherData(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
       // @Query("lang") lang: String,
        @Query("APPID") app_id: String = "3d91b6692ca2f8900c21f4c7cd8099a9"
    ): Response<WeatherResponse>
}


//api.openweathermap.org/data/2.5/forecast?lat=31.2596451&lon=30.0210898&appid=3d91b6692ca2f8900c21f4c7cd8099a9
//https://api.openweathermap.org/data/3.0/onecall?lat=31.2596451&lon=30.0210898&appid=3d91b6692ca2f8900c21f4c7cd8099a9
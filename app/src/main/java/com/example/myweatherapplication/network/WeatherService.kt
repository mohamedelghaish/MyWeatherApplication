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
        @Query("lang") lang: String,
        @Query("APPID") app_id: String = "3d91b6692ca2f8900c21f4c7cd8099a9"
    ): Response<WeatherResponse>
}

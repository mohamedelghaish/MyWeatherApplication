package com.example.myweatherapplication.network

import com.example.myweatherapplication.model.WeatherResponse

interface RemoteDataSource {
    suspend fun getData(
        latitude: String,
        longitude: String,
        language: String
    ): WeatherResponse
}
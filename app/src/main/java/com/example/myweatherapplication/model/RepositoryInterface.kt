package com.example.myweatherapplication.model

interface RepositoryInterface {
    suspend fun getDataFromNetwork(
        latitude: String,
        longitude: String
    ): WeatherResponse
}
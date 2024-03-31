package com.example.myweatherapplication.network

import com.example.myweatherapplication.model.City
import com.example.myweatherapplication.model.Clouds
import com.example.myweatherapplication.model.Coord
import com.example.myweatherapplication.model.Sys
import com.example.myweatherapplication.model.WeatherResponse
import com.example.myweatherapplication.model.Wind
import kotlin.random.Random

class FakeRemoteDataSource:RemoteDataSource {
    override suspend fun getData(
        latitude: String,
        longitude: String,
        language: String
    ): WeatherResponse {

        return WeatherResponse(
            cod = "",
            message = 0,
            cnt = 0,
            list = emptyList(),
            city = City(
                id = 0,
                name = "",
                coord = Coord(lat = 0.0, lon = 0.0),
                country = "",
                population = 0,
                timezone = 0,
                sunrise = 0L,
                sunset = 0L
            )
        )
    }
}
package com.example.myweatherapplication.database

import com.example.myweatherapplication.model.FavoriteLocation
import com.example.myweatherapplication.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource:LocalDataSource {
    // Fake data
    private val fakeWeatherData = mutableListOf<WeatherResponse>()
    private val fakeFavoriteData = mutableListOf<FavoriteLocation>()

    override suspend fun getWeatherFromDataBase(): Flow<List<WeatherResponse>> {
        // Return a flow of fake weather data
        return flowOf(fakeWeatherData)
    }

    override suspend fun insertCurrentDataToRoom(weatherResponse: WeatherResponse) {
        // Add the weather response to fake data list
        fakeWeatherData.add(weatherResponse)
    }

    override suspend fun getFavoriteFromDataBase(): Flow<List<FavoriteLocation>> {
        // Return a flow of fake favorite data
        return flowOf(fakeFavoriteData)
    }

    override suspend fun insertToFavorite(favoriteLocation: FavoriteLocation) {
        // Add the favorite location to fake data list
        fakeFavoriteData.add(favoriteLocation)
    }

    override suspend fun removeFromFavorite(favoriteLocation: FavoriteLocation): Int {
        // Remove the favorite location from fake data list and return the result
        return if (fakeFavoriteData.remove(favoriteLocation)) 1 else 0
    }
}


package com.example.myweatherapplication.database

import com.example.myweatherapplication.model.FavoriteLocation
import com.example.myweatherapplication.model.SavedAlerts
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

        fakeWeatherData.add(weatherResponse)
    }

    override suspend fun getFavoriteFromDataBase(): Flow<List<FavoriteLocation>> {

        return flowOf(fakeFavoriteData)
    }

    override suspend fun insertToFavorite(favoriteLocation: FavoriteLocation) {

        fakeFavoriteData.add(favoriteLocation)
    }

    override suspend fun removeFromFavorite(favoriteLocation: FavoriteLocation): Int {

        return if (fakeFavoriteData.remove(favoriteLocation)) 1 else 0
    }

    override suspend fun getStoredAlerts(): Flow<List<SavedAlerts>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlertToRoom(alerts: SavedAlerts): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlertFromRoom(id: Int): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getAlertFromRoom(id: Int): SavedAlerts {
        TODO("Not yet implemented")
    }
}


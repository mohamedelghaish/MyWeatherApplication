package com.example.myweatherapplication.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getDataFromNetwork(
        latitude: String,
        longitude: String,
        language: String
    ): Flow<WeatherResponse>


    //RoomCurrent
    suspend fun getWeatherFromDataBase(): Flow<List<WeatherResponse>>
    suspend fun insertCurrentDataToRoom(weatherResponse: WeatherResponse)

    //Favorites
    suspend fun getFavoriteFromDataBase(): Flow<List<FavoriteLocation>>
    suspend fun insertToFavorite(favoritePlaces: FavoriteLocation)
    suspend fun removeFromFavorite(favoritePlaces: FavoriteLocation) : Int

    //Alerts
    suspend fun insertAlertToRoom(savedAlerts: SavedAlerts): Long
    suspend fun getStoredAlerts(): Flow<List<SavedAlerts>>
    suspend fun deleteAlertFromRoom(id: Int): Int
    suspend fun getAlertFromRoom(id: Int): SavedAlerts
}
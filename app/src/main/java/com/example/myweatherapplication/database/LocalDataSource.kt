package com.example.myweatherapplication.database

import android.content.Context
import com.example.myweatherapplication.model.FavoriteLocation
import com.example.myweatherapplication.model.SavedAlerts
import com.example.myweatherapplication.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    //Current
    suspend fun getWeatherFromDataBase(): Flow <List<WeatherResponse>>
    suspend fun insertCurrentDataToRoom(weatherResponse: WeatherResponse)

    //Favorites
    suspend fun getFavoriteFromDataBase(): Flow <List<FavoriteLocation>>
    suspend fun insertToFavorite(favoriteLocation: FavoriteLocation)
    suspend fun removeFromFavorite(favoriteLocation: FavoriteLocation) : Int

    //Alert
    suspend fun getStoredAlerts(): Flow<List<SavedAlerts>>
    suspend fun insertAlertToRoom(alerts: SavedAlerts) : Long
    suspend fun deleteAlertFromRoom(id: Int) : Int
    suspend fun getAlertFromRoom(id: Int): SavedAlerts



}
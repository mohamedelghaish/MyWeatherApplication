package com.example.myweatherapplication.database

import android.content.Context
import com.example.myweatherapplication.model.FavoriteLocation
import com.example.myweatherapplication.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    //Current
    suspend fun getWeatherFromDataBase(): Flow <List<WeatherResponse>>
    suspend fun insertCurrentDataToRoom(weatherResponse: WeatherResponse)

    //Favorites
    suspend fun getFavoriteFromDataBase(): Flow <List<FavoriteLocation>>
    suspend fun insertToFavorite(favoritePlaces: FavoriteLocation)
    suspend fun removeFromFavorite(favoritePlaces: FavoriteLocation) : Int

}
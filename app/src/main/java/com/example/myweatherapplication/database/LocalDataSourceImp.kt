package com.example.myweatherapplication.database

import android.content.Context
import com.example.myweatherapplication.model.FavoriteLocation
import com.example.myweatherapplication.model.SavedAlerts
import com.example.myweatherapplication.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImp(context: Context): LocalDataSource {
    private var weatherDao: WeatherDao?

    init {
        val dataBase = AppDataBase.getInstance(context.applicationContext)
        weatherDao = dataBase.getWeatherDao()
    }
    companion object {
        @Volatile
        private var instance: LocalDataSourceImp? = null
        fun getInstance(context: Context): LocalDataSourceImp {
            if(instance == null)
                instance = LocalDataSourceImp(context)
            return instance as LocalDataSourceImp
        }
    }
    //Current
    override suspend fun getWeatherFromDataBase(): Flow<List<WeatherResponse>> {
       return weatherDao!!.getStoredWeather()
    }

    override suspend fun insertCurrentDataToRoom(weatherResponse: WeatherResponse) {
        weatherDao!!.insertCurrentToRoom(weatherResponse)
    }

    //Favorite
    override suspend fun getFavoriteFromDataBase(): Flow<List<FavoriteLocation>> {
        return weatherDao!!.getStoredFavorite()
    }

    override suspend fun insertToFavorite(favoriteLocation: FavoriteLocation) {
         weatherDao!!.insertToFavorite(favoriteLocation)
    }

    override suspend fun removeFromFavorite(favoriteLocation: FavoriteLocation): Int {
       return weatherDao!!.deleteFromFavorite(favoriteLocation)
    }

    override suspend fun getStoredAlerts(): Flow<List<SavedAlerts>> {
        return weatherDao!!.getStoredAlerts()
    }

    override suspend fun insertAlertToRoom(alerts: SavedAlerts): Long {
        return weatherDao!!.insertAlertToRoom(alerts)
    }

    override suspend fun deleteAlertFromRoom(id: Int): Int {
        return weatherDao!!.deleteAlertFromRoom(id)
    }

    override suspend fun getAlertFromRoom(id: Int): SavedAlerts {
        val response = weatherDao?.getAlertFromRoom(id)
        return response!!
    }
}
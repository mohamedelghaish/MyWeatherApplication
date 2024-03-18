package com.example.myweatherapplication.database

import android.content.Context
import com.example.myweatherapplication.model.FavoriteLocation
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

    //Favorite
    override suspend fun getFavoriteFromDataBase(): Flow<List<FavoriteLocation>> {
        return weatherDao!!.getStoredFavorite()
    }

    override suspend fun insertToFavorite(favoriteLocation: FavoriteLocation) {
         weatherDao!!.insertToFavorite(favoriteLocation)
    }

    override suspend fun removeFromFavorite(favoritePlaces: FavoriteLocation): Int {
       return weatherDao!!.deleteFromFavorite(favoritePlaces)
    }
}
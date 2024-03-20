package com.example.myweatherapplication.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myweatherapplication.model.FavoriteLocation
import com.example.myweatherapplication.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
@Dao
interface WeatherDao {


    //Stored Current Place
    @Query("SELECT * From current_weather")
     fun getStoredWeather():  Flow<List<WeatherResponse>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentToRoom(weatherResponse: WeatherResponse): Long

    //-----------------------------------------------------------------------------------------------------//

    //Stored from Favorite
    @Query("SELECT * From favorite")
     fun getStoredFavorite(): Flow<List<FavoriteLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToFavorite(favoritePlaces: FavoriteLocation): Long

    @Delete
    suspend fun deleteFromFavorite(favoritePlaces: FavoriteLocation): Int
}
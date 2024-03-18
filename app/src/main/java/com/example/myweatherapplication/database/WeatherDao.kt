package com.example.myweatherapplication.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myweatherapplication.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow
@Dao
interface WeatherDao {
    //Stored from Favorite
    @Query("SELECT * From favorite")
     fun getStoredFavorite(): Flow<List<FavoriteLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToFavorite(favoritePlaces: FavoriteLocation): Long

    @Delete
    suspend fun deleteFromFavorite(favoritePlaces: FavoriteLocation): Int
}
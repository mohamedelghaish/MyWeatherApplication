package com.example.myweatherapplication.database

import android.content.Context
import com.example.myweatherapplication.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    //Favorites
    suspend fun getFavoriteFromDataBase(): Flow <List<FavoriteLocation>>
    suspend fun insertToFavorite(favoritePlaces: FavoriteLocation)
    suspend fun removeFromFavorite(favoritePlaces: FavoriteLocation) : Int

}
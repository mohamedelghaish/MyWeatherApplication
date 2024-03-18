package com.example.myweatherapplication.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getDataFromNetwork(
        latitude: String,
        longitude: String
    ): Flow<WeatherResponse>

    //Favorites
    suspend fun getFavoriteFromDataBase(): Flow<List<FavoriteLocation>>
    suspend fun insertToFavorite(favoritePlaces: FavoriteLocation)
    suspend fun removeFromFavorite(favoritePlaces: FavoriteLocation) : Int
}
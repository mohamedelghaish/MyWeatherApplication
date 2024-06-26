package com.example.myweatherapplication.model

import android.content.Context
import com.example.myweatherapplication.database.LocalDataSource
import com.example.myweatherapplication.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class Repository (
    var remoteSource: RemoteDataSource,
    var localSource: LocalDataSource
) : RepositoryInterface {

    companion object {
        private var weatherRepo: Repository? = null
        fun getInstance(
            remoteDataSource: RemoteDataSource,
            localDataSource: LocalDataSource
        ): Repository {

            return weatherRepo ?: Repository( remoteDataSource,localDataSource)
        }
    }

    //Current Retrofit
    override suspend fun getDataFromNetwork(
        latitude: String,
        longitude: String,
        language: String
    ): Flow<WeatherResponse> {
        val response = remoteSource.getData(latitude, longitude,language)

        return flowOf(response)
    }

    override suspend fun getWeatherFromDataBase(): Flow<List<WeatherResponse>> {
        return localSource.getWeatherFromDataBase()
    }

    override suspend fun insertCurrentDataToRoom(weatherResponse: WeatherResponse) {
        localSource.insertCurrentDataToRoom(weatherResponse)
    }

    //Favorite
    override suspend fun getFavoriteFromDataBase(): Flow<List<FavoriteLocation>> {
        return (localSource.getFavoriteFromDataBase())
    }

    override suspend fun insertToFavorite(favoritePlaces: FavoriteLocation) {
        localSource.insertToFavorite(favoritePlaces)
    }

    override suspend fun removeFromFavorite(favoritePlaces: FavoriteLocation): Int {
        return localSource.removeFromFavorite(favoritePlaces)
    }

    override suspend fun insertAlertToRoom(savedAlerts: SavedAlerts): Long {
        return localSource.insertAlertToRoom(savedAlerts)
    }

    override suspend fun getStoredAlerts(): Flow<List<SavedAlerts>> {
        return localSource.getStoredAlerts()
    }

    override suspend fun deleteAlertFromRoom(id: Int): Int {
        return localSource.deleteAlertFromRoom(id)
    }

    override suspend fun getAlertFromRoom(id: Int): SavedAlerts {
        return localSource.getAlertFromRoom(id)
    }

}
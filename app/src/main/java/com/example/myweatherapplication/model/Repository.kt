package com.example.myweatherapplication.model

import android.content.Context
import com.example.myweatherapplication.database.LocalDataSource
import com.example.myweatherapplication.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class Repository (
    var context: Context,
    var remoteSource: RemoteDataSource,
    var localSource: LocalDataSource

) : RepositoryInterface {

    companion object {
        private var weatherRepo: Repository? = null
        fun getInstance(
            context: Context,
            remoteDataSource: RemoteDataSource,
            localDataSource: LocalDataSource

        ): Repository {

            return weatherRepo ?: Repository(context, remoteDataSource,localDataSource)
        }
    }

    //Current Retrofit
    override suspend fun getDataFromNetwork(
        latitude: String,
        longitude: String
    ): Flow<WeatherResponse> {
        val response = remoteSource.getData(latitude, longitude)

        return flowOf(response)
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

}
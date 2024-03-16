package com.example.myweatherapplication.model

import android.content.Context
import com.example.myweatherapplication.network.RemoteDataSource

class Repository (
    var context: Context,
    private var remoteSource: RemoteDataSource

) : RepositoryInterface {

    companion object {
        private var weatherRepo: Repository? = null
        fun getInstance(
            context: Context,
            remoteDataSource: RemoteDataSource

        ): Repository {

            return weatherRepo ?: Repository(context, remoteDataSource)
        }
    }

    //Current Retrofit
    override suspend fun getDataFromNetwork(
        latitude: String,
        longitude: String
    ): WeatherResponse {
        val response = remoteSource.getData(latitude, longitude)

        return response
    }

}
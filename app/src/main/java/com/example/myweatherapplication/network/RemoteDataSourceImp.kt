package com.example.myweatherapplication.network

import com.example.myweatherapplication.model.WeatherResponse

class RemoteDataSourceImp :RemoteDataSource {
    companion object{
        private var instance:RemoteDataSourceImp? = null
        fun getInstance():RemoteDataSourceImp{
            return instance ?: RemoteDataSourceImp()
        }
    }

    private val retrofitHelper = RetrofitHelper.getClient().create(WeatherService::class.java)
    override suspend fun getData(
        latitude: String,
        longitude: String,
        language: String

    ): WeatherResponse {
        val response = retrofitHelper.getWeatherData(
            latitude,
            longitude,
            language

        )
        return response.body()!!
    }

}
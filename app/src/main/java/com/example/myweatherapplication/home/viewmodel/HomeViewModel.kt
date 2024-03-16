package com.example.myweatherapplication.home.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myweatherapplication.model.Repository
import com.example.myweatherapplication.model.RepositoryInterface
import com.example.myweatherapplication.model.WeatherResponse
import com.example.myweatherapplication.network.RemoteDataSourceImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private  val TAG = "HomeViewModel"
    private lateinit var _iRepo: RepositoryInterface
    private var _weatherResponse = MutableLiveData<WeatherResponse>()
    val weatherResponse: LiveData<WeatherResponse> = _weatherResponse

    init {
        _iRepo =
            Repository.getInstance(
                application.applicationContext,
                RemoteDataSourceImp.getInstance()
            )
    }

    fun getWeatherFromNetwork(latitude: String, longitude: String) {
        viewModelScope.launch {
            val response = _iRepo.getDataFromNetwork(latitude, longitude)
            Log.i(TAG, "getWeatherFromNetwork: "+ response.city)
            withContext(Dispatchers.Main) {
                _weatherResponse.postValue(response)
            }

        }

    }

     /*suspend fun getWeatherFromNetwork(latitude: String, longitude: String) {

            val response = _iRepo.getDataFromNetwork(latitude, longitude)
            Log.i(TAG, "getWeatherFromNetwork: "+ response.timezone)
                _weatherResponse.postValue(response)
    }*/
}
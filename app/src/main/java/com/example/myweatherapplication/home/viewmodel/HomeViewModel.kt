package com.example.myweatherapplication.home.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myweatherapplication.ApiState
import com.example.myweatherapplication.database.LocalDataSourceImp
import com.example.myweatherapplication.model.Repository
import com.example.myweatherapplication.model.RepositoryInterface
import com.example.myweatherapplication.model.WeatherResponse
import com.example.myweatherapplication.network.RemoteDataSourceImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private  val TAG = "HomeViewModel"
    private lateinit var _iRepo: RepositoryInterface
     var _weatherResponse:MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    //val weatherResponse: LiveData<WeatherResponse> = _weatherResponse


    init {
        _iRepo =
            Repository.getInstance(
                application.applicationContext,
                RemoteDataSourceImp.getInstance(),
                LocalDataSourceImp(application.applicationContext)

            )
    }

    fun getWeatherFromNetwork(latitude: String, longitude: String) {
        viewModelScope.launch(Dispatchers.IO) {

             _iRepo.getDataFromNetwork(latitude, longitude).collect{
                 _weatherResponse.value = ApiState.Success(it)
             }


        }

    }

     /*suspend fun getWeatherFromNetwork(latitude: String, longitude: String) {

            val response = _iRepo.getDataFromNetwork(latitude, longitude)
            Log.i(TAG, "getWeatherFromNetwork: "+ response.timezone)
                _weatherResponse.postValue(response)
    }*/
}
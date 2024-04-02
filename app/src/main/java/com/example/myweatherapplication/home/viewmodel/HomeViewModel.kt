package com.example.myweatherapplication.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweatherapplication.network.ApiState
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
                RemoteDataSourceImp.getInstance(),
                LocalDataSourceImp(application.applicationContext)

            )
    }

    fun getWeatherFromNetwork(latitude: String, longitude: String, language: String) {
        viewModelScope.launch(Dispatchers.IO) {

             _iRepo.getDataFromNetwork(latitude, longitude,language).collect{
                 _weatherResponse.value = ApiState.Success(it)
             }


        }

    }

    fun insertCurrentDataToRoom(weatherResponse: WeatherResponse){
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    _iRepo.insertCurrentDataToRoom(weatherResponse)
                }
            }

    }

    fun getCurrentWeatherFromRoom(){
        viewModelScope.launch(Dispatchers.IO) {

            _iRepo.getWeatherFromDataBase().collect{
                _weatherResponse.value = ApiState.Success(it.get(0))
            }


        }
    }


}
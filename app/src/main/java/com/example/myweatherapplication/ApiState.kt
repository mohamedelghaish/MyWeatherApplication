package com.example.myweatherapplication

import com.example.myweatherapplication.model.WeatherResponse

sealed class ApiState {
    data class Success(val data:WeatherResponse):ApiState()
    data class Failure(val msg:Throwable):ApiState()
    object Loading:ApiState()

}
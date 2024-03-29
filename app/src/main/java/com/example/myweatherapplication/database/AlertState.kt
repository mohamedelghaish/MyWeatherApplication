package com.example.myweatherapplication.database

import com.example.myweatherapplication.model.SavedAlerts

sealed class AlertState {
    data class Success(val data: List<SavedAlerts>): AlertState()
    data class Failure(val msg: Throwable) : AlertState()
    object Loading : AlertState()
}
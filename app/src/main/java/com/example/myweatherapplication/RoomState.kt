package com.example.myweatherapplication

import com.example.myweatherapplication.model.FavoriteLocation

sealed class RoomState {
    data class Success(val data: List<FavoriteLocation>):RoomState()
    data class Failure(val msg:Throwable):RoomState()
    object Loading:RoomState()
}
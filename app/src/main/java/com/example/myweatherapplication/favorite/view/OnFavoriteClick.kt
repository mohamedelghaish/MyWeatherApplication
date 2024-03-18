package com.example.myweatherapplication.favorite.view

import com.example.myweatherapplication.model.FavoriteLocation


interface OnFavoriteClick {
     fun deleteFromRoom(favoritePlaces: FavoriteLocation)
      fun showDetails(latitude: String, longitude: String, language: String)
}
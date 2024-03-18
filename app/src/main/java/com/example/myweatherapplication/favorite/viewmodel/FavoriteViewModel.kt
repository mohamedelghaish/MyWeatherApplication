package com.example.myweatherapplication.favorite.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweatherapplication.RoomState
import com.example.myweatherapplication.model.FavoriteLocation
import com.example.myweatherapplication.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteViewModel(private var _iRepo: RepositoryInterface) : ViewModel()  {

   // private var _favoriteLocation = MutableLiveData<List<FavoriteLocation>>()
    //val favoriteLocation: LiveData<List<FavoriteLocation>> = _favoriteLocation
    var _favoriteLocation:MutableStateFlow<RoomState> = MutableStateFlow(RoomState.Loading)

    fun getStoredFavoritePlaces() {
        viewModelScope.launch (Dispatchers.IO){
                _iRepo.getFavoriteFromDataBase().collect{
                    _favoriteLocation.value = RoomState.Success(it)
                }
            }
        }


    fun insertToFavorite(favoritePlaces: FavoriteLocation) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _iRepo.insertToFavorite(favoritePlaces)
            }
        }
    }

    fun deleteFromRoom(favoritePlaces: FavoriteLocation) :Int{
        var response:Int =0
        viewModelScope.launch {
             response = _iRepo.removeFromFavorite(favoritePlaces)
            withContext(Dispatchers.Main) {
                if (response > 0) {
                    getStoredFavoritePlaces()
                } else {
                    Log.i("Favorite", "deleteFromRoom: failed")
                }

            }
        }
        return response
    }



}

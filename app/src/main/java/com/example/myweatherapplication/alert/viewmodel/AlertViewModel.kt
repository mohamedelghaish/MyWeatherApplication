package com.example.myweatherapplication.alert.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweatherapplication.AlertState
import com.example.myweatherapplication.RoomState
import com.example.myweatherapplication.model.RepositoryInterface
import com.example.myweatherapplication.model.SavedAlerts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertViewModel (iRepo: RepositoryInterface) : ViewModel() {
    private val _iRepo: RepositoryInterface = iRepo
     var _alertResponse : MutableStateFlow<AlertState> = MutableStateFlow(AlertState.Loading)
    //val alertResponse: LiveData<List<SavedAlerts>> = _alertResponse


    fun getStoredAlerts() {
        viewModelScope.launch (Dispatchers.IO){
           _iRepo.getStoredAlerts().collect{
               _alertResponse.value = AlertState.Success(it)
           }



        }
    }

    fun insertAlert(savedAlerts: SavedAlerts) {
        viewModelScope.launch(Dispatchers.IO) {
                _iRepo.insertAlertToRoom(savedAlerts)
        }
    }

    fun deleteAlertFromRoom(id: Int) {
        viewModelScope.launch (Dispatchers.IO){
            val response = _iRepo.deleteAlertFromRoom(id)
                if (response > 0)
                    getStoredAlerts()
                else
                    Log.i("AlertFragment", "deleteAlertFromRoom: failed")

        }
    }

}
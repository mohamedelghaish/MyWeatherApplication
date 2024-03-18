package com.example.myweatherapplication.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myweatherapplication.model.RepositoryInterface
import java.lang.IllegalArgumentException

class FavoriteViewModelFactory(private val _repo: RepositoryInterface) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            FavoriteViewModel(_repo) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}
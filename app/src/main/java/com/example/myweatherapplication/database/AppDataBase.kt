package com.example.myweatherapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myweatherapplication.model.FavoriteLocation

@Database(entities = arrayOf(FavoriteLocation::class), version = 1)
abstract class AppDataBase: RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao

    companion object{
        private var INSTANCE: AppDataBase?= null

        fun getInstance(context: Context): AppDataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    AppDataBase::class.java,
                    "weather").build()
                INSTANCE = instance
                instance
            }
        }
    }
}
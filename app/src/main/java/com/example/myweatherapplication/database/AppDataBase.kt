package com.example.myweatherapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myweatherapplication.model.FavoriteLocation
import com.example.myweatherapplication.model.SavedAlerts
import com.example.myweatherapplication.model.WeatherResponse
@TypeConverters(WeatherConverter::class)
@Database(entities = arrayOf(FavoriteLocation::class,WeatherResponse::class, SavedAlerts::class), version = 3)
abstract class AppDataBase: RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao

    companion object{
        private var INSTANCE: AppDataBase?= null

        fun getInstance(context: Context): AppDataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    AppDataBase::class.java,
                    "weather").fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
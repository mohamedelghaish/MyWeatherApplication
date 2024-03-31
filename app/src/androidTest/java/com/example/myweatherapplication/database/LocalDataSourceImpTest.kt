package com.example.myweatherapplication.database

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.myweatherapplication.model.City
import com.example.myweatherapplication.model.Coord
import com.example.myweatherapplication.model.FavoriteLocation
import com.example.myweatherapplication.model.WeatherResponse
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
@MediumTest
@RunWith(AndroidJUnit4::class)
class LocalDataSourceImpTest{
    private lateinit var database: AppDataBase
    private lateinit var localDataSource: LocalDataSourceImp

    @get:Rule
    val rule = InstantTaskExecutorRule()


    @Before
    fun setup() {

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDataBase::class.java
        )

            .build()
        val app: Application = ApplicationProvider.getApplicationContext()
        localDataSource = LocalDataSourceImp(app)

    }
    @After
    fun teardown() {
        
        database.close()
    }


    @Test
    fun getWeatherFromDataBase_returns_data_from_database() {
        runBlockingTest {
            // Given
            val testData = listOf(
                WeatherResponse(
                    "1",
                    0,
                    0,
                    emptyList(),
                    City(1, "City1", Coord(0.0, 0.0), "Country1", 1000, 0, 0L, 0L)
                ),
                WeatherResponse(
                    "2",
                    0,
                    0,
                    emptyList(),
                    City(2, "City2", Coord(0.0, 0.0), "Country2", 2000, 0, 0L, 0L)
                )
            )
            testData.forEach { localDataSource.insertCurrentDataToRoom(it) }


            // When
            val storedWeatherFlow = localDataSource.getWeatherFromDataBase()
            val result = storedWeatherFlow.first()

            // Then
            assertEquals(testData, result)
        }
    }

    @Test
    fun insertCurrentDataToRoom_inserts_data_into_database() {
        runBlockingTest {
            // Given 
            val testData = WeatherResponse(
                "1",
                0,
                0,
                emptyList(),
                City(1, "City1", Coord(0.0, 0.0), "Country1", 1000, 0, 0L, 0L)
            )

            // When inserting the data into the database
            localDataSource.insertCurrentDataToRoom(testData)

            // Then fetch the stored data from the database
            val storedWeatherFlow = localDataSource.getWeatherFromDataBase()
            val result = storedWeatherFlow.first()

           //Then
            assertEquals(listOf(testData), result)
        }
    }

    @Test
    fun getFavoriteFromDataBase_returns_data_from_database() {
        runBlockingTest {
            // Given 
            val testData = listOf(
                FavoriteLocation(1.0, 2.0, "Location1", System.currentTimeMillis()),
                FavoriteLocation(3.0, 4.0, "Location2", System.currentTimeMillis())
            )
            testData.forEach { localDataSource.insertToFavorite(it) }

            // When
            val storedFavoriteFlow = localDataSource.getFavoriteFromDataBase()
            val result = storedFavoriteFlow.first()

            // Then 
            assertEquals(testData, result)
        }
    }

    @Test
    fun insertToFavorite_inserts_data_into_database() {
        runBlockingTest {
            // Given 
            val testData = FavoriteLocation(1.0, 2.0, "Location1", System.currentTimeMillis())

            // When 
            localDataSource.insertToFavorite(testData)

            // Then 
            val storedFavoriteFlow = localDataSource.getFavoriteFromDataBase()
            val result = storedFavoriteFlow.first()

            
            assertTrue(result.contains(testData))
            assertThat(
                result.get(0).selectedPlaces,
                `is`("Location1")
            )
        }
    }

    @Test
    fun removeFromFavorite_removes_data_from_database() {
        runBlockingTest {
            // Given 
            val testData = FavoriteLocation(1.0, 2.0, "Location1", System.currentTimeMillis())


            localDataSource.insertToFavorite(testData)

            val storedFavoriteFlowBefore = localDataSource.getFavoriteFromDataBase()
            val storedFavoritesBefore = storedFavoriteFlowBefore.first()
            assertTrue(storedFavoritesBefore.contains(testData))

            // When
            val sotredFavoriteDeleted = localDataSource.removeFromFavorite(testData)

            // Then
            val storedFavoriteFlowAfter = localDataSource.getFavoriteFromDataBase()
            val storedFavoritesAfter = storedFavoriteFlowAfter.first()


            assertFalse(storedFavoritesAfter.contains(testData))

            assertEquals(1, sotredFavoriteDeleted)
        }
    }







}
package com.example.myweatherapplication.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.myweatherapplication.model.City
import com.example.myweatherapplication.model.Coord
import com.example.myweatherapplication.model.FavoriteLocation
import com.example.myweatherapplication.model.WeatherResponse
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@ExperimentalCoroutinesApi
@SmallTest
@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {
    private lateinit var database: AppDataBase
    lateinit var dao: WeatherDao
    lateinit var weatherResponse:WeatherResponse
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDataBase::class.java
        ).build()
        dao =  database.getWeatherDao()
    }
    @After
    fun teardown() {
        // close the database
        database.close()
    }

    @Test
    fun insertCurrentToRoom_test() {
        runBlockingTest {

             weatherResponse = WeatherResponse(
                cod = "",
                message = 0,
                cnt = 0,
                list = emptyList(),
                city = City(
                    id = 0,
                    name = "",
                    coord = Coord(lat = 0.0, lon = 0.0),
                    country = "",
                    population = 0,
                    timezone = 0,
                    sunrise = 0L,
                    sunset = 0L
                )
            )


            val result = dao.insertCurrentToRoom(weatherResponse)


            assertThat(result, CoreMatchers.not(-1))
        }
    }

    @Test
    fun getStoredWeather() = runBlockingTest {

        val testData = listOf(
            WeatherResponse("1", 0, 0, emptyList(), City(1, "City1", Coord(0.0, 0.0), "Country1", 1000, 0, 0L, 0L)),
            WeatherResponse("2", 0, 0, emptyList(), City(2, "City2", Coord(0.0, 0.0), "Country2", 2000, 0, 0L, 0L))
        )
        testData.forEach { dao.insertCurrentToRoom(it) }


        val storedWeatherFlow = dao.getStoredWeather()


        val storedWeatherList = storedWeatherFlow.first()


        assertEquals(testData, storedWeatherList)
    }

    @Test
    fun insertToFavorite_and_getStoredFavorite() = runBlockingTest {
        val favoriteLocation = FavoriteLocation(26.8206, 30.8025, "Egypt", System.currentTimeMillis())

        dao.insertToFavorite(favoriteLocation)


        val storedFavorite = dao.getStoredFavorite().first()


        assertThat( storedFavorite.get(0).selectedPlaces,`is`("Egypt"))
        assertThat( storedFavorite.get(0).lat,`is`(26.8206))


    }

    @Test
    fun deleteFromFavorite() = runBlockingTest {
        val favoriteLocation = FavoriteLocation(26.8206, 30.8025, "Egypt", System.currentTimeMillis())


        dao.insertToFavorite(favoriteLocation)


        dao.deleteFromFavorite(favoriteLocation)


        val storedFavorite = dao.getStoredFavorite().first()


        assertFalse(storedFavorite.contains(favoriteLocation))
    }

    @Test
    fun getStoredFavorite_emptyList() = runBlockingTest {

        val storedFavorite = dao.getStoredFavorite().first()

        assertTrue(storedFavorite.isEmpty())
    }
}




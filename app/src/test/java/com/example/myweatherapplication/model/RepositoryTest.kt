package com.example.myweatherapplication.model

import com.example.myweatherapplication.database.FakeLocalDataSource
import com.example.myweatherapplication.database.LocalDataSource
import com.example.myweatherapplication.network.FakeRemoteDataSource
import com.example.myweatherapplication.network.RemoteDataSource
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test


class RepositoryTest{
        lateinit var fakeRemoteDataSource:  RemoteDataSource
        lateinit var fakeLocalDataSource: LocalDataSource
        lateinit var repository: Repository

    @Before
    fun setup(){
        fakeRemoteDataSource = FakeRemoteDataSource()
        fakeLocalDataSource = FakeLocalDataSource()
        repository = Repository(
            fakeRemoteDataSource,
            fakeLocalDataSource
        )
    }

    @Test
    fun getDataFromNetwork_returnsCorrectData() {
        val result = runBlocking {
            repository.getDataFromNetwork("10.0", "20.0", "en").toList()
        }

        assertEquals(listOf(createFakeWeatherResponse()), result)
    }

    @Test
    fun getWeatherFromDataBase_returnsCorrectData() {
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
            testData.forEach { fakeLocalDataSource.insertCurrentDataToRoom(it) }


            val result = repository.getWeatherFromDataBase().first()

            assertEquals(testData, result)
        }
    }

    @Test
    fun insertCurrentDataToRoom_inserts_data_into_database() {
        runBlockingTest {
            val testData = createFakeWeatherResponse()


            repository.insertCurrentDataToRoom(testData)

            val storedWeatherFlow = fakeLocalDataSource.getWeatherFromDataBase().first()
            val result = storedWeatherFlow.first()

            assertEquals(testData, result)
        }
    }

    @Test
    fun getFavoriteFromDataBase_returns_correct_data() {
        runBlockingTest {
            val testData = listOf(
                FavoriteLocation(1.0, 2.0, "Location1", 123456),
                FavoriteLocation(3.0, 4.0, "Location2", 789012)
            )


            testData.forEach { fakeLocalDataSource.insertToFavorite(it) }



            val storedFavoritesFlow = repository.getFavoriteFromDataBase()
            val result = runBlocking { storedFavoritesFlow.first() }

            assertEquals(testData, result)
        }
    }

    @Test
    fun insertToFavorite_inserts_data_into_database() {
        runBlockingTest {

            val testData = FavoriteLocation(1.0, 2.0, "Location1", System.currentTimeMillis())



            repository.insertToFavorite(testData)

            val storedFavoritesFlow = fakeLocalDataSource.getFavoriteFromDataBase()
            val result = storedFavoritesFlow.first()

            assertEquals(true, result.contains(testData))
        }

    }

    @Test
    fun removeFromFavorite_removes_data_from_database() {
        runBlockingTest {

            val testData = FavoriteLocation(1.0, 2.0, "Location1", System.currentTimeMillis())
            fakeLocalDataSource.insertToFavorite(testData)


            repository.removeFromFavorite(testData)

            val storedFavoritesFlow = fakeLocalDataSource.getFavoriteFromDataBase()
            val result = storedFavoritesFlow.first()

            assertEquals(false, result.contains(testData))
        }
    }


    private fun createFakeWeatherResponse(): WeatherResponse {
        return WeatherResponse(
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
    }
}
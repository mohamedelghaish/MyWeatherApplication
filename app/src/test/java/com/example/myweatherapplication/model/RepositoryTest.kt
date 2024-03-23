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
        // When
        val result = runBlocking {
            repository.getDataFromNetwork("10.0", "20.0", "en").toList()
        }

        // Then
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


            // When
            val result =
                repository.getWeatherFromDataBase().first()

            // Then
            assertEquals(testData, result)
        }
    }

    @Test
    fun insertCurrentDataToRoom_inserts_data_into_database() {
        runBlockingTest {
            // Given a WeatherResponse object to insert
            val testData = createFakeWeatherResponse()

            // When inserting the data into the repository

            repository.insertCurrentDataToRoom(testData)

            // Then fetch the stored data from the fake local data source
            val storedWeatherFlow = fakeLocalDataSource.getWeatherFromDataBase().first()
            val result = storedWeatherFlow.first()

            // Verify that the inserted data is present in the database
            assertEquals(testData, result)
        }
    }

    @Test
    fun getFavoriteFromDataBase_returns_correct_data() {
        runBlockingTest {
            // Given a list of fake favorite locations
            val testData = listOf(
                FavoriteLocation(1.0, 2.0, "Location1", 123456),
                FavoriteLocation(3.0, 4.0, "Location2", 789012)
            )

            // When inserting the fake favorite locations into the fake local data source

            testData.forEach { fakeLocalDataSource.insertToFavorite(it) }


            // Then fetch the stored favorite locations from the repository
            val storedFavoritesFlow = repository.getFavoriteFromDataBase()
            val result = runBlocking { storedFavoritesFlow.first() }

            // Verify that the retrieved favorite locations match the fake data
            assertEquals(testData, result)
        }
    }

    @Test
    fun insertToFavorite_inserts_data_into_database() {
        runBlockingTest {
            // Given a fake favorite location
            val testData = FavoriteLocation(1.0, 2.0, "Location1", System.currentTimeMillis())

            // When inserting the fake favorite location into the repository

            repository.insertToFavorite(testData)

            // Then fetch the stored favorite locations from the fake local data source
            val storedFavoritesFlow = fakeLocalDataSource.getFavoriteFromDataBase()
            val result = storedFavoritesFlow.first()

            // Verify that the inserted favorite location is present in the database
            assertEquals(true, result.contains(testData))
        }

    }

    @Test
    fun removeFromFavorite_removes_data_from_database() {
        runBlockingTest {
            // Given a fake favorite location stored in the database
            val testData = FavoriteLocation(1.0, 2.0, "Location1", System.currentTimeMillis())
            fakeLocalDataSource.insertToFavorite(testData)

            // When removing the fake favorite location from the repository

            repository.removeFromFavorite(testData)


            // Then fetch the stored favorite locations from the fake local data source
            val storedFavoritesFlow = fakeLocalDataSource.getFavoriteFromDataBase()
            val result = storedFavoritesFlow.first()

            // Verify that the removed favorite location is not present in the database
            assertEquals(false, result.contains(testData))
        }
    }


    private fun createFakeWeatherResponse(): WeatherResponse {
        // Create and return a fake WeatherResponse object with default or empty values
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
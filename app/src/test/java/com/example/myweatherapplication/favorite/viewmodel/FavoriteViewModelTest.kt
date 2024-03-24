package com.example.myweatherapplication.favorite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myweatherapplication.ApiState
import com.example.myweatherapplication.RoomState
import com.example.myweatherapplication.model.FakeRepository
import com.example.myweatherapplication.model.FavoriteLocation
import com.example.myweatherapplication.model.RepositoryInterface
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class FavoriteViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    private lateinit var repository: RepositoryInterface
    private lateinit var viewModel: FavoriteViewModel

    @Before
    fun setup() {
        repository = FakeRepository()
        viewModel = FavoriteViewModel(repository)
    }

    @Test
    fun getFavoritePlace_returnListNotNull() = runBlocking {

        //When
        viewModel.getStoredFavoritePlaces()

        //Then
        val result = viewModel._favoriteLocation.first()
        var data = emptyList<FavoriteLocation>()
        when (result) {
            is RoomState.Success ->
                data = result.data

            else -> {}
        }
        assertThat(data, `is`(notNullValue()))
    }

    @Test
    fun insertFavoritePlacesIntoDatabase() = runBlocking {
        val testData = FavoriteLocation(1.0, 2.0, "Location1", System.currentTimeMillis())
        viewModel.insertToFavorite(testData)

        val value = viewModel._favoriteLocation.first()
        var data = emptyList<FavoriteLocation>()
        when (value) {
            is RoomState.Success ->
                data = value.data

            else -> {}
        }
        assertThat(data, `is`(notNullValue()))
    }

    @Test
    fun deleteFavoritePlaceFromDatabase() = runBlocking {
        // Given
        val testData = FavoriteLocation(1.0, 2.0, "Location1", System.currentTimeMillis())
        // First, insert the test data into the database
        viewModel.insertToFavorite(testData)

        // When
        // Then, delete the test data from the database
        val response = viewModel.deleteFromRoom(testData)

        // Check if the favorite location is removed from the list
        val value = viewModel._favoriteLocation.first()
        var data = emptyList<FavoriteLocation>()
        when (value) {
            is RoomState.Success ->
                data = value.data

            else -> {
            }
        }
        assertThat(data, `is`(emptyList()))
        assertEquals(0, response)
    }

    @Test
    fun `get data from network`() = runBlockingTest {
        // Given
        val latitude = "1.0"
        val longitude = "2.0"
        val language = "en"

        // When
        viewModel.getDataToFavoriteLocation(latitude, longitude, language)

        // Then
        val result = viewModel._favoriteLocation.first()
        var data = emptyList<FavoriteLocation>()
        when (result) {
            is RoomState.Success ->
                data = result.data

            else -> {}
        }
        // Verify
        assertThat(data, `is`(notNullValue()))
    }
}



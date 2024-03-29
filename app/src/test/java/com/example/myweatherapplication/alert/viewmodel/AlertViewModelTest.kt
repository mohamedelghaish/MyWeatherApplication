package com.example.myweatherapplication.alert.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myweatherapplication.database.AlertState
import com.example.myweatherapplication.database.RoomState
import com.example.myweatherapplication.favorite.viewmodel.FavoriteViewModel
import com.example.myweatherapplication.model.FakeRepository
import com.example.myweatherapplication.model.FavoriteLocation
import com.example.myweatherapplication.model.RepositoryInterface
import com.example.myweatherapplication.model.SavedAlerts
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.`when`


class AlertViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: RepositoryInterface
    private lateinit var viewModel: AlertViewModel

    @Before
    fun setup() {
        repository = FakeRepository()
        viewModel = AlertViewModel(repository)
    }

    @Test
    fun getStoredAlerts() = runBlockingTest {

        viewModel.getStoredAlerts()

        //Then
        val result = viewModel._alertResponse.first()
        var data = emptyList<SavedAlerts>()
        when (result) {
            is AlertState.Success ->
                data = result.data

            else -> {}
        }
       assertThat(data, `is`(notNullValue()))
    }
    @Test
    fun insertAlert() = runBlockingTest {

        val testData = SavedAlerts(
            startDate = "2023-12-01",
            endDate = "2023-12-31",
            startTime = "08:00 AM",
            endTime = "09:00 AM",
            repetitions = 5,
            tag = 123456)

         viewModel.insertAlert(testData)

        val value = viewModel._alertResponse.first()
        var data = emptyList<SavedAlerts>()

        when (value) {
            is AlertState.Success ->
                data = value.data

            else -> {}
        }
        assertThat(data, `is`(notNullValue()))
    }
    @Test
    fun deleteAlertFromRoom() = runBlockingTest {
        val testData = SavedAlerts(
            startDate = "2023-12-01",
            endDate = "2023-12-31",
            startTime = "08:00 AM",
            endTime = "09:00 AM",
            repetitions = 5,
            tag = 123456, 1)
        viewModel.insertAlert(testData)

        viewModel.deleteAlertFromRoom(1)

        val value = viewModel._alertResponse.first()
        var data = emptyList<SavedAlerts>()
        when (value) {
            is AlertState.Success ->
                data = value.data

            else -> {
            }
        }
        assertThat(data, `is`(emptyList()))
    }
}




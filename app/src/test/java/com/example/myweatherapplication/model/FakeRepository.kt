package com.example.myweatherapplication.model


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository : RepositoryInterface {

    val fakeWeatherResponseList = mutableListOf<WeatherResponse>()
    val fakeFavoriteLocationList = mutableListOf<FavoriteLocation>()
    val fakeSavedAlertsList = mutableListOf<SavedAlerts>()



    override suspend fun getDataFromNetwork(
        latitude: String,
        longitude: String,
        language: String
    ): Flow<WeatherResponse> {

        return flow {

            val fakeWeatherResponse = createFakeWeatherResponse()
            emit(fakeWeatherResponse)
        }
    }

    override suspend fun getWeatherFromDataBase(): Flow<List<WeatherResponse>> {

        return flow { emit(fakeWeatherResponseList) }
    }

    override suspend fun insertCurrentDataToRoom(weatherResponse: WeatherResponse) {

        fakeWeatherResponseList.add(weatherResponse)
    }

    override suspend fun getFavoriteFromDataBase(): Flow<List<FavoriteLocation>> {

        return flow { emit(fakeFavoriteLocationList) }
    }

    override suspend fun insertToFavorite(favoritePlaces: FavoriteLocation) {

        fakeFavoriteLocationList.add(favoritePlaces)
    }

    override suspend fun removeFromFavorite(favoritePlaces: FavoriteLocation): Int {

        return fakeFavoriteLocationList.removeAll { it == favoritePlaces }.let { if (it) 1 else 0 }
    }

    override suspend fun insertAlertToRoom(savedAlerts: SavedAlerts): Long {
        return fakeSavedAlertsList.add(savedAlerts).let { if (true) 1L else 0L}
    }

    override suspend fun getStoredAlerts(): Flow<List<SavedAlerts>> {
        return flow { emit(fakeSavedAlertsList) }
    }

    override suspend fun deleteAlertFromRoom(id: Int): Int {
        val deletedAlert = fakeSavedAlertsList.removeAll { it.id == id }

        return if (deletedAlert) 1 else 0
    }

    override suspend fun getAlertFromRoom(id: Int): SavedAlerts {
        TODO("Not yet implemented")
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

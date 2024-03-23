package com.example.myweatherapplication.model


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository : RepositoryInterface {

    private val fakeWeatherResponseList = mutableListOf<WeatherResponse>()
    private val fakeFavoriteLocationList = mutableListOf<FavoriteLocation>()

    override suspend fun getDataFromNetwork(
        latitude: String,
        longitude: String,
        language: String
    ): Flow<WeatherResponse> {
        // For testing, you can return a fake WeatherResponse Flow
        return flow {
            // Create a fake WeatherResponse object
            val fakeWeatherResponse = createFakeWeatherResponse()
            emit(fakeWeatherResponse)
        }
    }

    override suspend fun getWeatherFromDataBase(): Flow<List<WeatherResponse>> {
        // Return a Flow emitting the fake weather response list
        return flow { emit(fakeWeatherResponseList) }
    }

    override suspend fun insertCurrentDataToRoom(weatherResponse: WeatherResponse) {
        // Insert the provided weather response into the fake list
        fakeWeatherResponseList.add(weatherResponse)
    }

    override suspend fun getFavoriteFromDataBase(): Flow<List<FavoriteLocation>> {
        // Return a Flow emitting the fake favorite location list
        return flow { emit(fakeFavoriteLocationList) }
    }

    override suspend fun insertToFavorite(favoritePlaces: FavoriteLocation) {
        // Insert the provided favorite location into the fake list
        fakeFavoriteLocationList.add(favoritePlaces)
    }

    override suspend fun removeFromFavorite(favoritePlaces: FavoriteLocation): Int {
        // Remove the provided favorite location from the fake list
        return fakeFavoriteLocationList.removeAll { it == favoritePlaces }.let { if (it) 1 else 0 }
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

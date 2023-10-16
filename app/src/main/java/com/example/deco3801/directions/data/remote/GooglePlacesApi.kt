package com.example.jetmap.feature_google_places.data.remote

import com.example.deco3801.directions.domain.model.GooglePlacesInfo
import retrofit2.http.GET
import retrofit2.http.Query

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose
interface GooglePlacesApi {
    @GET("/maps/api/directions/json")
    suspend fun getDirection(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String
    ): GooglePlacesInfo

    companion object {
        const val BASE_URL = "https://maps.googleapis.com/"
    }
}

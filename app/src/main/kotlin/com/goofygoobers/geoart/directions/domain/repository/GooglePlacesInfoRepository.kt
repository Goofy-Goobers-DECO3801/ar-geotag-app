package com.goofygoobers.geoart.directions.domain.repository

import com.goofygoobers.geoart.directions.domain.model.GooglePlacesInfo
import com.goofygoobers.geoart.directions.util.Resource
import kotlinx.coroutines.flow.Flow

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose

// Provides an interface for accessing directions
interface GooglePlacesInfoRepository {
    fun getDirection(
        origin: String,
        destination: String,
        key: String,
    ): Flow<Resource<GooglePlacesInfo>>
}

package com.goofygoobers.geoart.directions.domain.usecase

import com.goofygoobers.geoart.directions.domain.model.GooglePlacesInfo
import com.goofygoobers.geoart.directions.domain.repository.GooglePlacesInfoRepository
import com.goofygoobers.geoart.directions.util.Resource
import kotlinx.coroutines.flow.Flow

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose

// This class is responsible for providing a convenient way to retrieve direction information
// from the Google Places repository.
class GetDirectionInfo(private val repository: GooglePlacesInfoRepository) {
    operator fun invoke(
        origin: String,
        destination: String,
        key: String,
    ): Flow<Resource<GooglePlacesInfo>> =
        repository.getDirection(
            origin = origin,
            destination = destination,
            key = key,
        )
}

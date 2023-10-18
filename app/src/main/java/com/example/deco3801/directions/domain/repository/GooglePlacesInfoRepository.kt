package com.example.deco3801.directions.domain.repository

import com.example.deco3801.directions.domain.model.GooglePlacesInfo
import com.example.deco3801.directions.util.Resource
import kotlinx.coroutines.flow.Flow

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose
interface GooglePlacesInfoRepository {
    fun getDirection(origin: String, destination: String, key: String): Flow<Resource<GooglePlacesInfo>>
}

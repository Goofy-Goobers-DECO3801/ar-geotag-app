package com.example.deco3801.directions.use_case

import com.example.deco3801.directions.util.Resource
import com.example.deco3801.directions.domain.model.GooglePlacesInfo
import com.example.deco3801.directions.domain.repository.GooglePlacesInfoRepository
import kotlinx.coroutines.flow.Flow

class GetDirectionInfo(private val repository: GooglePlacesInfoRepository) {
    operator fun invoke(origin: String, destination: String, key: String): Flow<Resource<GooglePlacesInfo>> = repository.getDirection(origin = origin, destination = destination, key = key)
}
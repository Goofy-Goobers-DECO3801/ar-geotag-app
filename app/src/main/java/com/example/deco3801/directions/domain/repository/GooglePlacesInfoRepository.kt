package com.example.deco3801.directions.domain.repository

import com.example.deco3801.directions.util.Resource
import com.example.deco3801.directions.domain.model.GooglePlacesInfo
import kotlinx.coroutines.flow.Flow

interface GooglePlacesInfoRepository {
    fun getDirection(origin: String, destination: String, key: String): Flow<Resource<GooglePlacesInfo>>
}
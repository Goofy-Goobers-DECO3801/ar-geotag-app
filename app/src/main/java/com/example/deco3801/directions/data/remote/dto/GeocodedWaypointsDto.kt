package com.example.deco3801.directions.data.remote.dto

import com.example.deco3801.directions.domain.model.GeocodedWaypoints

data class GeocodedWaypointsDto(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
){
    fun toGeocodedWaypoints(): GeocodedWaypoints{
        return GeocodedWaypoints(
            geocoder_status = geocoder_status,
            place_id =place_id,
            types = types
        )
    }
}
package com.example.deco3801.directions.domain.model

data class GeocodedWaypoints(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
)
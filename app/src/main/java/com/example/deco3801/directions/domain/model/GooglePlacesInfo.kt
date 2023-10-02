package com.example.deco3801.directions.domain.model

data class GooglePlacesInfo(
    val geocoded_waypoints: List<GeocodedWaypoints>,
    val routes: List<Routes>,
    val status: String
)
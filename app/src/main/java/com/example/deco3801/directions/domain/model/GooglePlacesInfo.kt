package com.example.deco3801.directions.domain.model

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose
data class GooglePlacesInfo(
    val geocoded_waypoints: List<GeocodedWaypoints>,
    val routes: List<Routes>,
    val status: String,
)

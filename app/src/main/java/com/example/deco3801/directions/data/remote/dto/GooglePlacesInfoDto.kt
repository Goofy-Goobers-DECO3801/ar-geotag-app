package com.example.deco3801.directions.data.remote.dto

import com.example.deco3801.directions.domain.model.GooglePlacesInfo

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose

// Creates Waypoint transfer object
data class GooglePlacesInfoDto(
    val geocoded_waypoints: List<GeocodedWaypointsDto>,
    val routes: List<RoutesDto>,
    val status: String,
) {
    fun toGooglePlacesInfo(): GooglePlacesInfo  {
        return GooglePlacesInfo(
            geocoded_waypoints = geocoded_waypoints.map { it.toGeocodedWaypoints() },
            routes = routes.map { it.toRoutes() },
            status = status,
        )
    }
}

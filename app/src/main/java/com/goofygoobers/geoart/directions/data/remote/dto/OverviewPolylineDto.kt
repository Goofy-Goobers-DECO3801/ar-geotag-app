package com.goofygoobers.geoart.directions.data.remote.dto

import com.goofygoobers.geoart.directions.domain.model.OverviewPolyline

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose

// Creates String of points as transfer object
data class OverviewPolylineDto(
    val points: String,
) {
    fun toOverviewPolyline(): OverviewPolyline {
        return OverviewPolyline(
            points = points,
        )
    }
}

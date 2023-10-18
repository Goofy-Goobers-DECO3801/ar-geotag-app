package com.example.deco3801.directions.data.remote.dto

import com.example.deco3801.directions.domain.model.Routes

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose

// Creates final route transfer object combining overview and legs
data class RoutesDto(
    val summary: String,
    val overview_polyline: OverviewPolylineDto,
    val legs: List<LegsDto>
)
{
    fun toRoutes(): Routes{
        return Routes(
            summary = summary,
            overview_polyline = overview_polyline.toOverviewPolyline(),
            legs = legs.map { it.toLegs() }
        )
    }
}

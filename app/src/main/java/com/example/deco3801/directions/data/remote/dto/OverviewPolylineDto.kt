package com.example.deco3801.directions.data.remote.dto

import com.example.deco3801.directions.domain.model.OverviewPolyline

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose
data class OverviewPolylineDto(
    val points: String
){
    fun toOverviewPolyline(): OverviewPolyline{
        return OverviewPolyline(
            points = points
        )
    }
}

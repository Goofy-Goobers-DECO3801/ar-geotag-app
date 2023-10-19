package com.goofygoobers.geoart.directions.data.remote.dto

import com.goofygoobers.geoart.directions.domain.model.Distance

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose

// Creates Distance transfer object
data class DistanceDto(
    val text: String,
    val value: Int,
) {
    fun toDistance(): Distance {
        return Distance(
            text = text,
            value = value,
        )
    }
}

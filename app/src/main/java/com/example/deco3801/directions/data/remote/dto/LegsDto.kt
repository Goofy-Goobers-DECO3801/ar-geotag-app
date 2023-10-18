package com.example.deco3801.directions.data.remote.dto

import com.example.deco3801.directions.domain.model.Legs

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose

// Creates Legs transfer object
data class LegsDto(
    val distance: DistanceDto,
    val duration: DurationDto
){
    fun toLegs(): Legs {
        return Legs(
            distance = distance.toDistance(),
            duration = duration.toDuration()
        )
    }
}

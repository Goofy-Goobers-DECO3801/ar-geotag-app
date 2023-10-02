package com.example.deco3801.directions.data.remote.dto

import com.example.deco3801.directions.domain.model.Legs

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
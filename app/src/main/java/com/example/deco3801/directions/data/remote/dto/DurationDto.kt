package com.example.deco3801.directions.data.remote.dto

import com.example.deco3801.directions.domain.model.Duration

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose

// Creates Duration transfer object
data class DurationDto(
    val text: String,
    val value: Int,
) {
    fun toDuration(): Duration  {
        return Duration(
            text = text,
            value = value,
        )
    }
}

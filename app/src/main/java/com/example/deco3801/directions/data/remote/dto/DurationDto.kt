package com.example.deco3801.directions.data.remote.dto

import com.example.deco3801.directions.domain.model.Duration

data class DurationDto(
    val text: String,
    val value: Int
){
    fun toDuration(): Duration{
        return Duration(
            text = text,
            value = value
        )
    }
}
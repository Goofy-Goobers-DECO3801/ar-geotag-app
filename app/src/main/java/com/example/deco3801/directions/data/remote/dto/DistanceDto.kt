package com.example.deco3801.directions.data.remote.dto

import com.example.deco3801.directions.domain.model.Distance

data class DistanceDto(
    val text: String,
    val value: Int
){
    fun toDistance(): Distance{
        return  Distance(
            text = text,
            value = value
        )
    }
}
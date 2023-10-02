package com.example.deco3801.directions.domain.model

data class Routes(
    val summary: String,
    val overview_polyline: OverviewPolyline,
    val legs: List<Legs>
)
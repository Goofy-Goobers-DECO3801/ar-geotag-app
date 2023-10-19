package com.example.deco3801.directions.domain.model

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose
// These variable names need to match the JSON response from the Google Directions API,
// which is why they don't follow the usual naming conventions.
data class Routes(
    val summary: String,
    val overview_polyline: OverviewPolyline,
    val legs: List<Legs>,
)

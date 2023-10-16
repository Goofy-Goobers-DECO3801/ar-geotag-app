package com.example.jetmap.feature_google_places.presentation

import com.example.deco3801.directions.domain.model.GooglePlacesInfo

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose
data class GooglePlacesInfoState(val direction: GooglePlacesInfo? = null, val isLoading: Boolean = false)

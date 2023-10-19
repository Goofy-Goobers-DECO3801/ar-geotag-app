package com.goofygoobers.geoart.directions.presentation

import com.goofygoobers.geoart.directions.domain.model.GooglePlacesInfo

// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose

// This is a data class that represents the state of Google Places information within the app.

data class GooglePlacesInfoState(val direction: GooglePlacesInfo? = null, val isLoading: Boolean = false)

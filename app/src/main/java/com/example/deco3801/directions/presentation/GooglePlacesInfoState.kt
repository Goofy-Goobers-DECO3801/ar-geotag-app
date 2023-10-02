package com.example.jetmap.feature_google_places.presentation

import com.example.deco3801.directions.domain.model.GooglePlacesInfo

data class GooglePlacesInfoState(val direction: GooglePlacesInfo? = null, val isLoading: Boolean = false)
package com.example.deco3801.artdisplay.presentation

data class ArtDisplayViewState(
    var modelPlaced: Boolean = false,
    var readyToPlaceModel: Boolean = false,
    var downloadingAsset: Boolean = false,
    var modelAsset: String? = null
)

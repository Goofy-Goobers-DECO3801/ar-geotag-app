package com.example.deco3801.artdisplay.presentation

data class ArtDisplayViewState(
    var modelPlaced: Boolean = false,
    val readyToPlaceModel: Boolean = false,
    val downloadingAsset: Boolean = false,
    val modelAsset: String? = null
)

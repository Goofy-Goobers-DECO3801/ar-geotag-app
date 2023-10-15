package com.example.deco3801.artdisplay.presentation

// AR screen was modelled after below sample
// Blizl, “Blizl/sceneview-android,” 21 September 2023. [Online]. Available: https://github.com/Blizl/sceneview-android/tree/blizl/ecommerce-compose-mvvm-app.

data class ArtDisplayViewState(
    var modelPlaced: Boolean = false,
    var readyToPlaceModel: Boolean = false,
    var downloadingAsset: Boolean = false,
    var modelAsset: String? = null
)

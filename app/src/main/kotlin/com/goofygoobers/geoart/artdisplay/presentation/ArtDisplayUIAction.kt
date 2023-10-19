package com.goofygoobers.geoart.artdisplay.presentation

// AR screen was modelled after below sample
// Blizl, “Blizl/sceneview-android,” 21 September 2023. [Online]. Available: https://github.com/Blizl/sceneview-android/tree/blizl/ecommerce-compose-mvvm-app.

/*
 * Class representing UI actions
 */
sealed class ArtDisplayUIAction {
    object ShowModalPlaced : ArtDisplayUIAction()
}

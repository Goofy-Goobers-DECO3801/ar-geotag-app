package com.goofygoobers.geoart.artdisplay.presentation

import com.google.ar.core.Plane

// AR screen was modelled after below sample
// Blizl, “Blizl/sceneview-android,” 21 September 2023. [Online]. Available: https://github.com/Blizl/sceneview-android/tree/blizl/ecommerce-compose-mvvm-app.

/*
 * Class representing UI events
 */
sealed class ArtDisplayUIEvent {
    object ModelPlaced : ArtDisplayUIEvent()

    data class OnPlanesUpdated(val updatedPlanes: List<Plane>) : ArtDisplayUIEvent()

    data class FetchAsset(val artAddress: String) : ArtDisplayUIEvent()

    data class DownloadAsset(val downloading: Boolean) : ArtDisplayUIEvent()
}

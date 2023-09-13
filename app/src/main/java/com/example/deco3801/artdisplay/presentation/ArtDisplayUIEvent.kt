package com.example.deco3801.artdisplay.presentation

import com.google.ar.core.Plane

sealed class ArtDisplayUIEvent {
    object ModelPlaced : ArtDisplayUIEvent()
    data class OnPlanesUpdated(val updatedPlanes: List<Plane>) : ArtDisplayUIEvent()
    data class FetchAsset(val artID: Int) : ArtDisplayUIEvent()
}

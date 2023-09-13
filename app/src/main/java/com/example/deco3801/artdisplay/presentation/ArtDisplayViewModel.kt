package com.example.deco3801.artdisplay.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deco3801.artdisplay.data.ModelAssetRepoImpl
import com.example.deco3801.artdisplay.domain.ModelAssetRepo
import com.google.ar.core.Plane
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArtDisplayViewModel : ViewModel() {
    private val _state: MutableStateFlow<ArtDisplayViewState> =
        MutableStateFlow(ArtDisplayViewState())
    val state: StateFlow<ArtDisplayViewState> = _state

    private val _uiAction: MutableStateFlow<ArtDisplayUIAction?> = MutableStateFlow(
        null
    )
    val uiAction: StateFlow<ArtDisplayUIAction?> = _uiAction

    private var remoteAsset: String? = null

    // Best practice is to use dependency injection to inject the repository
    // TODO("look into dependency injection")
    private val repository: ModelAssetRepo = ModelAssetRepoImpl()


    fun dispatchEvent(event: ArtDisplayUIEvent) {
        when (event) {
            is ArtDisplayUIEvent.ModelPlaced -> onModelPlaced()
            is ArtDisplayUIEvent.OnPlanesUpdated -> onPlanesUpdated(event.updatedPlanes)
            is ArtDisplayUIEvent.FetchAsset -> onFetchAsset(event.artID)
        }
    }

    private fun onFetchAsset(artID: Int) {
        viewModelScope.launch {
            setState(state.value.copy(downloadingAsset = true))
            remoteAsset = repository.fetchAsset(artID)
            setState(state.value.copy(downloadingAsset = false, modelAsset = remoteAsset))
        }
    }

    private fun onPlanesUpdated(updatedPlanes: List<Plane>) {
        if (!state.value.readyToPlaceModel && updatedPlanes.isNotEmpty()) {
            // Only update once so that user sees that the model can be placed
            setState(
                state.value.copy(readyToPlaceModel = updatedPlanes.isNotEmpty())
            )
        }
    }

    private fun onModelPlaced() {
        setState(
            state.value.copy(
                modelPlaced = true,
            )
        )

        setUiAction(ArtDisplayUIAction.ShowModalPlaced)
    }


    private fun setState(newState: ArtDisplayViewState) {
        viewModelScope.launch {
            _state.emit(newState)
        }
    }

    private fun setUiAction(uiAction: ArtDisplayUIAction) {
        viewModelScope.launch {
            _uiAction.emit(uiAction)
        }
    }

    fun onConsumedUiAction() {
        viewModelScope.launch {
            _uiAction.emit(null)
        }
    }

}
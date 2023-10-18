package com.example.deco3801.artdisplay.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deco3801.artdisplay.data.ModelAssetRepoImpl
import com.example.deco3801.artdisplay.domain.ModelAssetRepo
import com.google.ar.core.Plane
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

// AR screen was modelled after below sample
// Blizl, “Blizl/sceneview-android,” 21 September 2023. [Online]. Available: https://github.com/Blizl/sceneview-android/tree/blizl/ecommerce-compose-mvvm-app.


/*
 * Class representing Art display view model
 */
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

    /*
     * Dispatches a UI event (e.g. calls appropriate handler)
     */
    fun dispatchEvent(event: ArtDisplayUIEvent) {
        when (event) {
            is ArtDisplayUIEvent.ModelPlaced -> onModelPlaced()
            is ArtDisplayUIEvent.OnPlanesUpdated -> onPlanesUpdated(event.updatedPlanes)
            is ArtDisplayUIEvent.FetchAsset -> onFetchAsset(event.artAddress)
            is ArtDisplayUIEvent.DownloadAsset -> onDownloadAsset(event.downloading)
        }
    }

    /*
     * Sets the downloadingAsset attribute of state to [downloading]
     */
    private fun onDownloadAsset(downloading: Boolean) {
        viewModelScope.launch {
            setState(state.value.copy(downloadingAsset = downloading))
        }
    }

    /*
     * Fetches art address and sets remoteAsset to it
     */
    private fun onFetchAsset(artAddress: String) {
        viewModelScope.launch {
            remoteAsset = repository.fetchAsset(artAddress)
            setState(state.value.copy(modelAsset = remoteAsset))
        }
    }

    /*
     * Set readToPlaceModel to updated planes
     */
    private fun onPlanesUpdated(updatedPlanes: List<Plane>) {
        if (!state.value.readyToPlaceModel && updatedPlanes.isNotEmpty()) {
            // Only update once so that user sees that the model can be placed
            setState(
                state.value.copy(readyToPlaceModel = updatedPlanes.isNotEmpty())
            )
        }
    }

    // Sets modelPlaced to be true
    private fun onModelPlaced() {
        setState(
            state.value.copy(
                modelPlaced = true,
            )
        )

        setUiAction(ArtDisplayUIAction.ShowModalPlaced)
    }


    /*
     * Set state attribute to [newState]
     */
    private fun setState(newState: ArtDisplayViewState) {
        viewModelScope.launch {
            _state.emit(newState)
        }
    }

    /*
     * Set state to default values
     */
    fun resetStates() {
        setState(
            state.value.copy(
                modelPlaced = false,
                readyToPlaceModel = false,
                downloadingAsset = false,
                modelAsset = null
            )
        )


    }

    /*
     * Emits [uiAction]
     */
    private fun setUiAction(uiAction: ArtDisplayUIAction) {
        viewModelScope.launch {
            _uiAction.emit(uiAction)
        }
    }

    /*
     * Emits a null UI action
     */
    fun onConsumedUiAction() {
        viewModelScope.launch {
            _uiAction.emit(null)
        }
    }

}

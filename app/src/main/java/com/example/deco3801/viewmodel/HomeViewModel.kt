package com.example.deco3801.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.deco3801.data.model.Art
import com.example.deco3801.data.repository.ArtRepository
import com.example.deco3801.util.toGeoPoint
import com.example.deco3801.util.toLatLng
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.GeoQuery
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener
import javax.inject.Inject

data class HomeUiState(
    val art: MutableMap<String, Art> = HashMap(),
    val currentLocation: LatLng? = null,
    val distanceInKm: Double = 10.0,
    val ready: Boolean = false,
)


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val artRepo: ArtRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState
    private var geoQuery: GeoQuery? = null

    fun onLocationChange(newValue: Location?) {
        if (newValue != null) {
            return onLocationChange(newValue.toLatLng())
        }
    }

    fun onLocationChange(newValue: LatLng) {
        _uiState.value = _uiState.value.copy(currentLocation = newValue)
        geoQuery?.center = newValue.toGeoPoint()
    }

    fun onDistanceChange(newValue: Double) {
        _uiState.value = _uiState.value.copy(distanceInKm = newValue)
        geoQuery?.radius = newValue
    }

    fun listenForArt() {
        if (_uiState.value.currentLocation == null) {
            Log.w("GEOQUERY", "No location")
            return
        }

        geoQuery = GeoFirestore(artRepo.getCollectionRef()).queryAtLocation(
            _uiState.value.currentLocation!!.toGeoPoint(),
            _uiState.value.distanceInKm
        )

        geoQuery?.addGeoQueryDataEventListener(object : GeoQueryDataEventListener {
            // private var offset = 0.001

            override fun onDocumentEntered(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
                val art = documentSnapshot.toObject<Art>() ?: return
                // DEBUG: Add offset since emulator is in a fixed location so all art is added at
                // the same spot and we only see one marker.
                // art.location = GeoPoint(art.location!!.latitude + offset, art.location!!.longitude)
                // offset += offset
                // TODO: Client-side filtering
                _uiState.update {
                    val updatedArtMap = it.art.toMutableMap().apply {
                        put(art.id, art)
                    }
                    it.copy(art = updatedArtMap)
                }
                Log.d("GEOQUERY", "ENTER $art")
            }

            override fun onDocumentExited(documentSnapshot: DocumentSnapshot) {
                val art = documentSnapshot.toObject<Art>() ?: return
                _uiState.update {
                    val updatedArtMap = it.art.toMutableMap().apply {
                        remove(art.id)
                    }
                    it.copy(art = updatedArtMap)
                }
                Log.d("GEOQUERY", "EXIT $art")
            }

            override fun onDocumentMoved(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
                val art = documentSnapshot.toObject<Art>() ?: return
                _uiState.update {
                    val updatedArtMap = it.art.toMutableMap().apply {
                        replace(art.id, art)
                    }
                    it.copy(art = updatedArtMap)
                }
                Log.d("GEOQUERY", "MOVED $art")
            }

            override fun onDocumentChanged(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
                // TODO: Client-side filtering
                val art = documentSnapshot.toObject<Art>() ?: return
                _uiState.update {
                    val updatedArtMap = it.art.toMutableMap().apply {
                        replace(art.id, art)
                    }
                    it.copy(art = updatedArtMap)
                }
                Log.d("GEOQUERY", "CHANGED $art")
            }

            override fun onGeoQueryReady() {
                _uiState.value = _uiState.value.copy(ready = true)
            }

            override fun onGeoQueryError(exception: Exception) {
                // TODO: Handle error
            }
        })
    }

    fun stopListen() {
        geoQuery?.removeAllListeners()
    }
}
package com.example.deco3801.directions.presentation

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deco3801.directions.domain.use_case.GetDirectionInfo
import com.example.deco3801.directions.util.Resource
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


// Directions was created using below
// Kadhi Chienja, "JetMapCompose", 16 October 2023. [Online]. Available: https://github.com/kahdichienja/JetMapCompose

// This is a ViewModel class for handling Google Places information and directions.
@HiltViewModel
class GooglePlacesInfoViewModel @Inject constructor(private val getDirectionInfo: GetDirectionInfo): ViewModel() {

    private val _googlePlacesInfoState = mutableStateOf(GooglePlacesInfoState())
    private val googlePlacesInfoState: State<GooglePlacesInfoState> = _googlePlacesInfoState


    private val _polyLinesPoints = MutableStateFlow<List<LatLng>>(emptyList())
    val polyLinesPoints: StateFlow<List<LatLng>>
        get() = _polyLinesPoints

    private val _eventFlow = MutableSharedFlow<UIEvent>()


    fun getDirection(origin: String, destination: String, key: String){
        viewModelScope.launch {
            getDirectionInfo(origin = origin, destination = destination, key = key).onEach { res ->
                when(res){
                    is Resource.Success ->{
                        _googlePlacesInfoState.value = googlePlacesInfoState.value.copy(
                            direction = res.data,
                            isLoading = false
                        )
                        _eventFlow.emit(UIEvent.ShowSnackBar(message = "Direction Loaded"))

                        val routes = googlePlacesInfoState.value.direction?.routes
                        if (!routes.isNullOrEmpty()) {
                            val overviewPolyline = routes[0].overview_polyline
                            val polylinePoints = overviewPolyline.points
                            if (polylinePoints.isNotEmpty()) {
                                decoPoints(points = polylinePoints)
                                Log.d(TAG, "POLYLINE: $polylinePoints")
                            } else {
                                // Pass
                            }
                        } else {
                            // Pass
                        }
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(
                            UIEvent.ShowSnackBar(
                                message = res.message ?: "Unknown Error"
                            )
                        )
                    }
                    is Resource.Loading -> {
                        _googlePlacesInfoState.value = googlePlacesInfoState.value.copy(
                            direction = null,
                            isLoading = false
                        )
                        _eventFlow.emit(UIEvent.ShowSnackBar(message = "Loading Direction"))
                    }
                }
            }.launchIn(this)
        }
    }

    // Sealed class for UI events.
    sealed class UIEvent{
        data class ShowSnackBar(val message: String): UIEvent()
    }

    // Function for decoding polyline points.
    private fun decoPoints(points: String): List<LatLng>{
        _polyLinesPoints.value = decodePoly(points)
        return decodePoly(points)
    }

    /**
     * Method to decode polyline points
     * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }
}

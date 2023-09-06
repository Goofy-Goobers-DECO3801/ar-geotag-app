package com.example.deco3801.ui

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.compose.foundation.layout.fillMaxSize

@Composable
fun HomeScreen() {
    AndroidView(
            factory = { context ->
                val mapView = MapView(context).apply {
                    // Configure the map settings here
                    onCreate(Bundle()) // Make sure to call onCreate
                    getMapAsync { googleMap ->
                        // Perform map-related operations here
                        // For example, add markers, listen for map events, etc.
                        val markerOptions = MarkerOptions()
                                .position(LatLng(52.8449, -0.5330))
                                .title("Marker Title")
                        googleMap.addMarker(markerOptions)
                    }
                }
                mapView
            },
            modifier = Modifier.fillMaxSize()
    )
}

// Replace YourLatitude and YourLongitude with actual values

@Composable
fun HomeScreenPreview() {
    // You can create a preview of your HomeScreen here
    HomeScreen()
}

package com.example.deco3801.ui

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.deco3801.ui.components.RequestPermissions
import com.example.deco3801.util.LocationUtil
import com.example.deco3801.util.toLatLng
import com.example.deco3801.viewmodel.HomeViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HomeScreen(
    context: Context = LocalContext.current,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    // Location Permissions
    RequestPermissions(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ),
        title = "Location Permissions",
        description = "This app functions best when we can use your precise location.\n" +
                "You can opt out of this at anytime."
    )

    val uiState by viewModel.uiState.collectAsState()

    /*
    TODO: Need a way to constantly get the users location.
     Should probably look into using a foreground service.
     This fails the first time since its run before the permissions are accepted.
     */
    LaunchedEffect(Unit) {
        viewModel.onLocationChange(LocationUtil.getCurrentLocation(context))
    }

    if (uiState.currentLocation == null) {
        return
    }

    //    val brisbane = LatLng(-27.4705, 153.0260)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.currentLocation!!.toLatLng(), 10f)
    }

    DisposableEffect(Unit) {
        viewModel.listenForArt()
        onDispose {
            viewModel.stopListen()
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        for (art in uiState.art) {
            if (art.location == null) {
                continue
            }
            Log.d("MARKER", art.toString())
            Marker(
                state = MarkerState(position = art.location!!.toLatLng()),
                title = art.title,
                snippet = art.description,
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}


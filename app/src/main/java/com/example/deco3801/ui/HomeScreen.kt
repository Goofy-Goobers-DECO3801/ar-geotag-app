package com.example.deco3801.ui

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.deco3801.R
import com.example.deco3801.ui.components.ProgressbarState
import com.example.deco3801.ui.components.RequestPermissions
import com.example.deco3801.ui.components.TopBar
import com.example.deco3801.util.LocationUtil.getCurrentLocation
import com.example.deco3801.util.toLatLng
import com.example.deco3801.util.toRadius
import com.example.deco3801.viewmodel.HomeViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = {
            TopBar(
                canNavigateBack = false,
                showSettings = false,
                navigateUp = {}
            )
        }
    ) { innerPadding ->

        val context = LocalContext.current

        DisposableEffect(Unit) {
            ProgressbarState.showIndeterminateProgressbar()
            onDispose {
                ProgressbarState.resetProgressbar()
            }
        }
        // Location Permissions
        RequestPermissions(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            title = "Location Permissions",
            description = "This app functions best when we can use your precise location.\n" +
                    "You can opt out of this at anytime."
        ) {
            LaunchedEffect(Unit) {
                viewModel.onLocationChange(getCurrentLocation(context))
            }

        }

        val uiState by viewModel.uiState.collectAsState()

        // TODO: What do we display if we cannot get the users location?
        if (uiState.currentLocation == null) {
            return@Scaffold
        }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(uiState.currentLocation!!, 10f)
        }

        val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }

        if (cameraPositionState.isMoving) {
            viewModel.onLocationChange(cameraPositionState.position.target)
            viewModel.onDistanceChange(cameraPositionState.position.toRadius())
        }

        DisposableEffect(Unit) {
            viewModel.listenForArt()
            onDispose {
                viewModel.stopListen()
            }
        }

        Column(modifier = Modifier.padding(innerPadding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                onMapLoaded = {
                    ProgressbarState.resetProgressbar()
                }
            ) {
                for (art in uiState.art.values) {
                    if (art.location == null) {
                        continue
                    }
                    Log.d("MARKER", art.toString())
                    Marker(
                        state = MarkerState(position = art.location!!.toLatLng()),
                        title = art.title,
                        snippet = art.description,
                        icon =BitmapDescriptorFactory.fromResource(R.drawable.map_marker)

                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}


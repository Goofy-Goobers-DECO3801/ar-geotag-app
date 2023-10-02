package com.example.deco3801.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
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
import com.example.deco3801.directions.presentation.GooglePlacesInfoViewModel
import com.example.deco3801.ui.components.ProgressbarState
import com.example.deco3801.ui.components.RequestPermissions
import com.example.deco3801.ui.components.TopBar
import com.example.deco3801.util.LocationUtil.getCurrentLocation
import com.example.deco3801.util.toLatLng
import com.example.deco3801.util.toRadius
import com.example.deco3801.viewmodel.HomeViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location
import android.os.Bundle;
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.ktx.storageMetadata
import com.google.maps.android.compose.Polyline

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    googlePlacesViewModel: GooglePlacesInfoViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = {
            TopBar(
                canNavigateBack = false,
                showSettings = false,
                navigateUp = {},
                showArtFilter = true
            )
        }
    ) { innerPadding ->

        val context = LocalContext.current
        val userLocation = remember { mutableStateOf<Location?>(null) }

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
                userLocation.value = getCurrentLocation(context)
            }

        }

        val uiState by viewModel.uiState.collectAsState()
        val art by viewModel.activeArt.collectAsState()


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

        var apiKey: String? = null

        context.packageManager
            .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            .apply {
                apiKey = metaData.getString("com.google.android.geo.API_KEY")
            }

        val markerClick: (Marker) -> Boolean = {marker ->
            // Call the ViewModel function in GooglePlacesInfoViewModel when a marker is clicked
            apiKey?.let {
                googlePlacesViewModel.getDirection(
                    origin = "${userLocation.value!!.latitude}, ${userLocation.value!!.longitude}", // Modify this to get the actual origin
                    destination = "${marker.position!!.latitude}, ${marker.position!!.longitude}", // Use the marker's location as the destination
                    key = it
                )
            }
            false
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
                art.values.forEach {
                    if (it.location == null) {
                        return@forEach
                    }
                    Log.d("MARKER", art.toString())
                    Marker(
                        state = MarkerState(position = it.location!!.toLatLng()),
                        title = it.title,
                        snippet = it.description,
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.map_marker),
                        onClick = markerClick

                    )
                }

                Polyline(points = googlePlacesViewModel.polyLinesPoints.value, onClick = {
                    Log.d(TAG, "${it.points} was clicked")
                }, color = Color.Blue)
            }



        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}


package com.example.deco3801.ui

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.R
import com.example.deco3801.data.model.Art
import com.example.deco3801.data.model.User
import com.example.deco3801.navigateArt
import com.example.deco3801.ui.components.ArtFilterMenu
import com.example.deco3801.ui.components.ProgressbarState
import com.example.deco3801.ui.components.RequestPermissions
import com.example.deco3801.ui.components.TopBar
import com.example.deco3801.util.formatDate
import com.example.deco3801.util.formatDistance
import com.example.deco3801.util.getCurrentLocation
import com.example.deco3801.util.toGeoLocation
import com.example.deco3801.util.toLatLng
import com.example.deco3801.viewmodel.HomeViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import org.imperiumlabs.geofirestore.util.GeoUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = {
            TopBar(
                navController = navController
            ) {
                ArtFilterMenu()
            }
        },
    ) { innerPadding ->

        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        val uiState by viewModel.uiState.collectAsState()
        val art by viewModel.activeArt.collectAsState()
        val cameraPositionState = rememberCameraPositionState()
        var mapProperties by remember { mutableStateOf(MapProperties()) }

        DisposableEffect(Unit) {
            ProgressbarState.showIndeterminateProgressbar()
            onDispose {
                ProgressbarState.resetProgressbar()
            }
        }
        // Location Permissions
        RequestPermissions(
            permissions =
                listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ),
            title = "Location Permissions",
            description =
                "This app functions best when we can use your precise location.\n" +
                    "You can opt out of this at anytime.",
            onRevoked = {
                // Set default location when location services are disabled to brisbane
                val currentLocation = LatLng(-27.4975, 153.0137)
                viewModel.onLocationChange(currentLocation)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation, 10f)
            }
        ) {
            LaunchedEffect(Unit) {
                val currentLocation = context.getCurrentLocation()
                viewModel.onLocationChange(currentLocation)
                if (currentLocation != null) {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation.toLatLng(), 10f)
                }
                mapProperties = mapProperties.copy(isMyLocationEnabled = true)
            }
        }

        /* XXX:
        This gets run way to much and lags the map since everytime it triggers it the
        onDocumentChanged callback gets triggered. (idk how to fix this so just hard coding our
        geoquery to 100,000km)
         */
//       if (cameraPositionState.isMoving) {
//           viewModel.onLocationChange(cameraPositionState.position.target)
//           viewModel.onDistanceChange(cameraPositionState.position.toRadius())
//       }

        DisposableEffect(Unit) {
            viewModel.listenForArt()
            onDispose {
                viewModel.stopListen()
            }
        }

        val markerClick: (Marker) -> Boolean = { marker ->
            marker.title?.let { viewModel.onArtSelect(it) }

            scope.launch {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLng(marker.position),
                    50,
                )
            }
            true // Disable default marker selection
        }

        Column(modifier = Modifier.padding(innerPadding)) {
            // Creates and displays the map interface
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                onMapLoaded = {
                    ProgressbarState.resetProgressbar()
                },
            ) {
                art.values.forEach {
                    if (it.location == null) {
                        return@forEach
                    }

                    // Retrieves and displays the custom marker
                    Marker(
                        state = MarkerState(position = it.location!!.toLatLng()),
                        title = it.id,
                        snippet = it.description,
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.map_marker),
                        onClick = markerClick,
                    )
                }
            }
        }

        if (uiState.selectedArt != null && uiState.selectArtUser != null) {
            ArtMarker(
                art = uiState.selectedArt!!,
                artist = uiState.selectArtUser!!,
                onDismissRequest = viewModel::onArtUnselect,
                onSelect = { navController.navigateArt(it) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtMarker(
    art: Art,
    artist: User,
    onDismissRequest: () -> Unit,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var distanceAway by remember { mutableStateOf<Double?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val currentLocation = context.getCurrentLocation()?.toGeoLocation()
        val artLocation = art.location?.toGeoLocation()
        if (artLocation != null && currentLocation != null) {
            distanceAway = GeoUtils.distance(artLocation, currentLocation)
        }
    }

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            onClick = {
                onDismissRequest()
                onSelect(art.id)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "distance",
                    )
                    Spacer(Modifier.width(5.dp))
                    Text("${distanceAway?.let { formatDistance(it) } ?: "??"} away")
                }
                Text(
                    art.title,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text("@${artist.username}")
                Text(formatDate(art.timestamp))
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}

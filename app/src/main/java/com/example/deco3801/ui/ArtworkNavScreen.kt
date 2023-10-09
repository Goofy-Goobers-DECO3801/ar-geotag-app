package com.example.deco3801.ui

import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.deco3801.R
import com.example.deco3801.ScreenNames
import com.example.deco3801.data.model.Art
import com.example.deco3801.data.model.User
import com.example.deco3801.directions.presentation.GooglePlacesInfoViewModel
import com.example.deco3801.navigateAR
import com.example.deco3801.ui.components.GetUserLocation
import com.example.deco3801.ui.components.ProgressbarState
import com.example.deco3801.ui.theme.UnchangingAppColors
import com.example.deco3801.util.LocationUtil
import com.example.deco3801.util.toGeoLocation
import com.example.deco3801.util.toLatLng
import com.example.deco3801.viewmodel.ArtworkNavViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import org.imperiumlabs.geofirestore.util.GeoUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkNavScreen(
    artId: String,
    navController: NavHostController,
    viewModel: ArtworkNavViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<Location?>(null) }
    val art by viewModel.art.collectAsState()
    val user by viewModel.user.collectAsState()
    val liked by viewModel.liked.collectAsState()

    GetUserLocation(onChange = { userLocation = it })
    LaunchedEffect(Unit) { // This is much quicker for the first time
        userLocation = LocationUtil.getCurrentLocation(context)
    }

    DisposableEffect(Unit) {
        viewModel.hasLiked(artId)
        viewModel.attachListener(artId)
        onDispose {
            viewModel.detachListener()
        }
    }

    Scaffold(
        topBar = {
            ArtworkTopBar(
                navController = navController,
                artworkTitle = art.title
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ArtworkTitle(art, user) {
                navController.navigate("${ScreenNames.Profile.name}/${user.id}")
            }
            ArtworkMap(art, userLocation)

            ArtworkInteract(
                distanceInM = if (art.location != null && userLocation != null) {
                    GeoUtils.distance(
                        art.location!!.toGeoLocation(),
                        userLocation!!.toGeoLocation()
                    )
                } else {
                    Double.MAX_VALUE
                }
            ) {
                navController.navigateAR(art.storageUri)
            }
            ArtworkDescription(art, liked) {
                viewModel.onLikeClicked()
            }
        }
    }
}

@Composable
fun ArtworkTitle(
    art: Art,
    user: User,
    onUserClicked: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        /*Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(UnchangingAppColors.main_theme)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = artworkTitle,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }*/
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(UnchangingAppColors.main_theme)
        ) {
            Column(
                modifier = Modifier.padding(
                    start = 15.dp,
                    top = 10.dp
                )
            ) {
                AsyncImage(
                    model = user.pictureUri.ifBlank { R.drawable.pfp },
                    placeholder = painterResource(id = R.drawable.pfp),
                    contentDescription = "profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(45.dp)
                        .clickable {
                            onUserClicked()
                        }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 10.dp,
                        top = 10.dp,
                        bottom = 15.dp
                    )
            ) {
                Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onUserClicked()
                    }
                )
                Text(
                    text = art.timestamp?.let { formatDate(it) } ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkTopBar(
    artworkTitle: String,
    navController: NavHostController
) {
    TopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, end = 48.dp)
            ) {
                Text(
                    text = artworkTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            titleContentColor = Color.White,
            containerColor = UnchangingAppColors.main_theme
        ),
        navigationIcon = {
            IconButton(onClick = navController::popBackStack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIos,
                    contentDescription = "ArrowBack",
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun ArtworkMap(
    art: Art,
    userLocation: Location?,
    googlePlacesViewModel: GooglePlacesInfoViewModel = hiltViewModel()
) {
    DisposableEffect(Unit) {
        ProgressbarState.showIndeterminateProgressbar()
        onDispose {
            ProgressbarState.resetProgressbar()
        }
    }

    if (userLocation == null) {
        return
    }

    val routePoints by googlePlacesViewModel.polyLinesPoints.collectAsState()
    val context = LocalContext.current
    var apiKey by remember { mutableStateOf<String?>(null) }
    val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation.toLatLng(), 15f)
    }

    LaunchedEffect(Unit) {
        context.packageManager
            .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            .apply {
                apiKey = metaData.getString("com.google.android.geo.API_KEY")
            }
    }

    LaunchedEffect(apiKey != null && art.location != null) {
        if (apiKey != null && art.location != null) {
            googlePlacesViewModel.getDirection(
                // Modify this to get the actual origin
                origin = "${userLocation.latitude}, ${userLocation.longitude}",
                // Use the marker's location as the destination
                destination = "${art.location!!.latitude}, ${art.location!!.longitude}",
                key = apiKey!!
            )
        }
    }

    Column(modifier = Modifier.height(400.dp)) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            onMapLoaded = {
                ProgressbarState.resetProgressbar()
            }
        ) {
            art.location?.let {
                Marker(
                    state = MarkerState(position = it.toLatLng()),
                    title = art.title,
                    snippet = art.description,
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.map_marker)
                )
            }

            Polyline(points = routePoints, onClick = {
                Log.d("ROUTE", "${it.points} was clicked")
            }, color = Color.Blue)
        }
    }
}

@Composable
fun ArtworkInteract(
    distanceInM: Double,
    onArClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(UnchangingAppColors.main_theme),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (distanceInM <= 10.0) {
            Text(
                text = "You have arrived",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 15.dp, bottom = 10.dp)
            )
            Button(
                onClick = onArClicked,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CenterFocusWeak,
                    contentDescription = "AR"
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "View in AR",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Text(
                text = formatDistance(distanceInM),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                modifier = Modifier.padding(15.dp)
            )
        }
    }
}

@Composable
fun ArtworkDescription(
    art: Art,
    liked: Boolean?,
    onLikeClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                enabled = liked != null,
                onClick = onLikeClicked
            ) {
                Icon(
                    imageVector = if (liked != null && liked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "heart",
                    tint = if (liked != null && liked) Color.Red else Color.Unspecified
                )
            }
            Spacer(Modifier.width(5.dp))
            Text("${art.likeCount} likes")
            Spacer(Modifier.width(10.dp))
            IconButton(
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Message,
                    contentDescription = "reviews"
                )
            }
            Spacer(Modifier.width(5.dp))
            Text("${art.reviewCount} reviews")
        }
        Spacer(Modifier.height(10.dp))
        Text(art.description)
    }
}

@Preview
@Composable
private fun PreviewArtworkNavScreen() {
    ArtworkNavScreen(
        "1",
        rememberNavController()
    )
}

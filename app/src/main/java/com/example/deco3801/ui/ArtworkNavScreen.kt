/**
 * Composable components for the Artwork Navigation screen.
 */
package com.example.deco3801.ui

import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.deco3801.R
import com.example.deco3801.data.model.Art
import com.example.deco3801.data.model.Comment
import com.example.deco3801.data.model.User
import com.example.deco3801.directions.presentation.GooglePlacesInfoViewModel
import com.example.deco3801.navigateAR
import com.example.deco3801.navigateProfile
import com.example.deco3801.ui.components.GetUserLocation
import com.example.deco3801.ui.components.ProgressbarState
import com.example.deco3801.ui.components.SnackbarManager
import com.example.deco3801.ui.components.TopBar
import com.example.deco3801.ui.theme.UnchangingAppColors
import com.example.deco3801.util.forEachOrElse
import com.example.deco3801.util.formatDate
import com.example.deco3801.util.formatDistance
import com.example.deco3801.util.getGoogleApiKey
import com.example.deco3801.util.toGeoLocation
import com.example.deco3801.util.toLatLng
import com.example.deco3801.viewmodel.ArtworkNavViewModel
import com.example.deco3801.viewmodel.CommentViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import org.imperiumlabs.geofirestore.util.GeoUtils

/**
 * Margin in meters to determine if the user is at the artwork location.
 */
private const val DISTANCE_AWAY_MARGIN = 50.0

/**
 * Displays the Artwork Navigation screen and all its components.
 * This screen includes a map, the artwork title, description, and the ability to like and comment.
 *
 * @param artId The id of the artwork to display.
 * @param navController The navigation controller.
 * @param markerIcon The icon to use for the marker on the map.
 * @param viewModel The Artwork Navigation view model injected by Hilt.
 * @param useDarkTheme Whether to use the dark theme.
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ArtworkNavScreen(
    artId: String,
    navController: NavHostController,
    markerIcon: Bitmap,
    viewModel: ArtworkNavViewModel = hiltViewModel(),
    useDarkTheme: Boolean = isSystemInDarkTheme(),
) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<Location?>(null) }
    val art by viewModel.art.collectAsState()
    val user by viewModel.user.collectAsState()
    val liked by viewModel.liked.collectAsState()
    var showComments by remember { mutableStateOf(false) }
    var showEditPost by remember { mutableStateOf(false) }
    var distanceInM by remember { mutableStateOf<Double?>(null) }
    var columnScrollingEnabled by remember { mutableStateOf(true) }
    val columnScroll: (Boolean) -> Unit = {
        columnScrollingEnabled = it
    }
    val cameraPositionState = rememberCameraPositionState()
    var mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapStyleOptions =
                    MapStyleOptions.loadRawResourceStyle(
                        context,
                        if (useDarkTheme) R.raw.map_style_dark else R.raw.map_style_light,
                    ),
            ),
        )
    }

    GetUserLocation(onChange = { userLocation = it })
    LaunchedEffect(userLocation != null) {
        if (userLocation != null) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation!!.toLatLng(), 15f)
            mapProperties = mapProperties.copy(isMyLocationEnabled = true)
        }
    }

    DisposableEffect(Unit) {
        viewModel.hasLiked(artId)
        viewModel.attachListener(artId)
        onDispose {
            viewModel.detachListener()
        }
    }

    distanceInM =
        if (art.location != null && userLocation != null) {
            GeoUtils.distance(
                art.location!!.toGeoLocation(),
                userLocation!!.toGeoLocation(),
            )
        } else {
            null
        }

    Scaffold(
        topBar = {
            TopBar(
                title = art.title,
                navController = navController,
                canNavigateBack = true,
            ) {
                IconButton(onClick = { showEditPost = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreHoriz,
                        contentDescription = "More",
                        tint = Color.White,
                    )
                }
            }
        },
    ) { innerPadding ->
        if (showComments) {
            CommentBottomSheet(
                artId = artId,
                modifier = Modifier.heightIn(min = 400.dp),
                onUserClicked = {
                    navController.navigateProfile(it.id)
                },
                onDismissRequest = {
                    showComments = false
                },
                distanceInM = distanceInM,
            )
        }
        if (showEditPost && art.id.isNotBlank()) {
            EditPostBottomSheet(
                isCurrentUser = viewModel.isCurrentUser(art.userId),
                modifier = Modifier.heightIn(min = 400.dp),
                onDismissRequest = {
                    showEditPost = false
                },
                onDelete = {
                    viewModel.onDeleteClicked()
                    navController.popBackStack()
                },
                onReport = viewModel::onReportClicked,
            )
        }
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(
                        rememberScrollState(),
                        columnScrollingEnabled,
                    ),
        ) {
            ArtworkTitle(
                art,
                user,
            ) { navController.navigateProfile(user.id) }
            ArtworkMap(
                art = art,
                userLocation = userLocation,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .pointerInteropFilter(
                            onTouchEvent = {
                                when (it.action) {
                                    MotionEvent.ACTION_DOWN -> {
                                        columnScroll(false)
                                        false
                                    }

                                    else -> {
                                        true
                                    }
                                }
                            },
                        ),
                columnScroll = columnScroll,
                cameraPositionState = cameraPositionState,
                mapProperties = mapProperties,
                markerIcon = markerIcon,
            )

            ArtworkInteract(
                distanceInM = distanceInM,
            ) {
                navController.navigateAR(art.storageUri)
            }
            ArtworkDescription(
                art = art,
                liked = liked,
                onLikeClicked = viewModel::onLikeClicked,
                onCommentClicked = { showComments = true },
                distanceInM = distanceInM,
            )
        }
    }
}

/**
 * Displays the comments for the artwork and allows the user to post a comment.
 *
 * @param artId The id of the artwork to display the comments for.
 * @param onUserClicked Callback to run when the user's profile is clicked.
 * @param onDismissRequest Callback to run when the bottom sheet is dismissed.
 * @param distanceInM The distance in meters between the user and the artwork.
 * @param modifier The modifier to apply to the bottom sheet.
 * @param viewModel The comment view model injected by Hilt.
 * @param sheetState The state of the bottom sheet.
 *
 * @see [ModalBottomSheet]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(
    artId: String,
    onUserClicked: (User) -> Unit,
    onDismissRequest: () -> Unit,
    distanceInM: Double?,
    modifier: Modifier = Modifier,
    viewModel: CommentViewModel = hiltViewModel(),
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    val commentState by viewModel.comments.collectAsState()
    var comment by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        viewModel.attachListener(artId)
        onDispose {
            viewModel.detachListener()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState(), true)
                    .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.Top,
            content = {
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comment") },
                    trailingIcon = {
                        IconButton(
                            enabled = atArtLocation(distanceInM) && comment.isNotBlank(),
                            onClick = {
                                viewModel.onCommentPosted(artId, comment)
                                comment = ""
                            },
                        ) {
                            Icon(imageVector = Icons.Filled.Send, contentDescription = null)
                        }
                    },
                    modifier =
                        Modifier
                            .padding(15.dp)
                            .fillMaxWidth(),
                    enabled = atArtLocation(distanceInM),
                    supportingText = {
                        Text("You must be at the location to comment.")
                    },
                )
                commentState.forEachOrElse(
                    orElse = {
                        Text(
                            "Be the first to comment!",
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 20.dp,
                                        end = 20.dp,
                                        top = 10.dp,
                                        bottom = 10.dp,
                                    ),
                        )
                    },
                ) {
                    UserComment(user = it.user, comment = it.comment) {
                        onUserClicked(it.user)
                    }
                }
            },
        )
    }
}

/**
 * Display the header information for an artwork.
 * This includes the user's profile picture, username, and the timestamp of the artwork.
 *
 * @param art The artwork to display the header for.
 * @param user The user who created the artwork.
 * @param onUserClicked Callback to run when the user's profile is clicked.
 */
@Composable
fun ArtworkTitle(
    art: Art,
    user: User,
    onUserClicked: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(UnchangingAppColors.main_theme),
        ) {
            Column(
                modifier =
                    Modifier.padding(
                        start = 15.dp,
                    ),
            ) {
                AsyncImage(
                    model = user.pictureUri.ifBlank { R.drawable.pfp },
                    placeholder = painterResource(id = R.drawable.pfp),
                    contentDescription = "profile",
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier
                            .clip(CircleShape)
                            .size(45.dp)
                            .clickable {
                                onUserClicked()
                            },
                )
            }
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 10.dp,
                            bottom = 15.dp,
                            end = 30.dp,
                        ),
            ) {
                Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier.clickable {
                            onUserClicked()
                        },
                )
                Text(
                    text = art.timestamp?.let { formatDate(it) } ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                )
            }
        }
    }
}

/**
 * Display the edit post bottom sheet.
 * This allows the user to delete or report their post.
 *
 * @param isCurrentUser Whether the current user is the owner of the post.
 * @param onDismissRequest Callback to run when the bottom sheet is dismissed.
 * @param onDelete Callback to run when the delete button is clicked.
 * @param onReport Callback to run when the report button is clicked.
 * @param modifier The modifier to apply to the bottom sheet.
 * @param sheetState The state of the bottom sheet.
 *
 * @see [ModalBottomSheet]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostBottomSheet(
    isCurrentUser: Boolean,
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    onReport: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    var showDeleteConfirmButton by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState(), true)
                    .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (isCurrentUser) "Deleting this post?" else "Reporting this post?",
                modifier = Modifier.padding(start = 10.dp, bottom = 16.dp),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height((0.5).dp)
                        .background(MaterialTheme.colorScheme.onError),
            )
            Text(
                text = if (isCurrentUser) "If you delete this post, it will be permanently deleted and other users will no longer be able to discover your artwork." else "We take the misuse of this app seriously and are committed to upholding our Terms and Conditions. Please help us maintain a positive community by reporting any posts that violate our guidelines.",
                modifier =
                    Modifier.padding(
                        top = 16.dp,
                        start = 25.dp,
                        end = 25.dp,
                        bottom = 16.dp,
                    ),
            )
            if (isCurrentUser) {
                if (showDeleteConfirmButton) {
                    Button(
                        onClick = {
                            onDismissRequest()
                            onDelete()
                        },
                    ) {
                        Text(text = "Are you sure?")
                    }
                } else {
                    Button(
                        onClick = {
                            showDeleteConfirmButton = true
                        },
                    ) {
                        Text(text = "Delete Post")
                    }
                }
            } else {
                Button(onClick = {
                    onDismissRequest()
                    onReport()
                }) {
                    Text(text = "Report Post")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

/**
 * Display the Google map with a marker on the artwork, and a route to it.
 *
 * @param art The artwork to display the map for.
 * @param userLocation The user's current location.
 * @param columnScroll Callback to run when the user scrolls the column.
 * @param markerIcon The icon to use for the marker on the map.
 * @param modifier The modifier to apply to the map.
 * @param cameraPositionState The camera position state of the map.
 * @param mapProperties The properties of the map.
 * @param googlePlacesViewModel The Google Places view model injected by Hilt.
 *
 * @see [GoogleMap]
 */
@Composable
fun ArtworkMap(
    art: Art,
    userLocation: Location?,
    columnScroll: (Boolean) -> Unit,
    markerIcon: Bitmap,
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    mapProperties: MapProperties = MapProperties(),
    googlePlacesViewModel: GooglePlacesInfoViewModel = hiltViewModel(),
) {
    DisposableEffect(Unit) {
        ProgressbarState.showIndeterminateProgressbar()
        onDispose {
            ProgressbarState.resetProgressbar()
        }
    }

    val routePoints by googlePlacesViewModel.polyLinesPoints.collectAsState()
    val context = LocalContext.current
    var apiKey by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        apiKey = context.getGoogleApiKey()
    }

    LaunchedEffect(apiKey != null && art.location != null && userLocation != null) {
        if (apiKey != null && art.location != null && userLocation != null) {
            googlePlacesViewModel.getDirection(
                // Modify this to get the actual origin
                origin = "${userLocation.latitude}, ${userLocation.longitude}",
                // Use the marker's location as the destination
                destination = "${art.location!!.latitude}, ${art.location!!.longitude}",
                key = apiKey!!,
            )
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            columnScroll(true)
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        onMapLoaded = {
            ProgressbarState.resetProgressbar()
        },
    ) {
        art.location?.let {
            Marker(
                state = MarkerState(position = it.toLatLng()),
                title = art.title,
                snippet = art.description,
                icon = BitmapDescriptorFactory.fromBitmap(markerIcon),
            )
        }

        Polyline(points = routePoints, onClick = {
            Log.d("ROUTE", "${it.points} was clicked")
        }, color = Color.Blue)
    }
}

/**
 * Display the interact section of the artwork navigation screen.
 * This shows the user how far away they are from the artwork and view it in AR when they arrive.
 *
 * @param distanceInM The distance in meters between the user and the artwork.
 * @param onArClicked Callback to run when the AR button is clicked.
 */
@Composable
fun ArtworkInteract(
    distanceInM: Double?,
    onArClicked: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(UnchangingAppColors.main_theme),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (distanceInM != null && distanceInM <= DISTANCE_AWAY_MARGIN) {
            Text(
                text = "You have arrived",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 15.dp, bottom = 10.dp),
            )
            Button(
                onClick = onArClicked,
                modifier = Modifier.padding(bottom = 20.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.CenterFocusWeak,
                    contentDescription = "AR",
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "View in AR",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        } else {
            Text(
                text = distanceInM?.let { formatDistance(it) } ?: "??",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                modifier = Modifier.padding(15.dp),
            )
        }
    }
}

/**
 * Display the description of the artwork.
 *
 * @param art The artwork to display the description for.
 * @param liked Whether the user has liked the artwork.
 * @param onLikeClicked Callback to run when the like button is clicked.
 * @param onCommentClicked Callback to run when the comment button is clicked.
 * @param distanceInM The distance in meters between the user and the artwork.
 */
@Composable
fun ArtworkDescription(
    art: Art,
    liked: Boolean?,
    onLikeClicked: () -> Unit,
    onCommentClicked: () -> Unit,
    distanceInM: Double?,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(20.dp, 5.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                enabled = liked != null && distanceInM != null,
                onClick = {
                    if (!atArtLocation(distanceInM)) {
                        SnackbarManager.showMessage(
                            "You must be at the location to like.",
                        )
                        return@IconButton
                    }
                    onLikeClicked()
                },
            ) {
                Icon(
                    imageVector = if (liked != null && liked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "heart",
                    tint = if (liked != null && liked) Color.Red else MaterialTheme.colorScheme.onBackground,
                )
            }
            Spacer(Modifier.width(5.dp))
            Text("${art.likeCount} likes")
            Spacer(Modifier.width(10.dp))
            IconButton(
                onClick = onCommentClicked,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Message,
                    contentDescription = "comments",
                )
            }
            Spacer(Modifier.width(5.dp))
            Text("${art.commentCount} comments")
        }
        Spacer(Modifier.height(10.dp))
        Text(art.description)
    }
}

/**
 * Display a user's comment.
 *
 * @param user The user who posted the comment.
 * @param comment The comment to display.
 * @param onUserClicked Callback to run when the user's profile is clicked.
 */
@Composable
fun UserComment(
    user: User,
    comment: Comment,
    onUserClicked: () -> Unit = {},
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
    ) {
        Column(Modifier.padding(top = 5.dp, end = 15.dp)) {
            AsyncImage(
                model = user.pictureUri.ifBlank { R.drawable.pfp },
                placeholder = painterResource(id = R.drawable.pfp),
                contentDescription = "profile",
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .size(45.dp)
                        .clickable {
                            onUserClicked()
                        },
            )
        }
        Column {
            Row {
                Text(
                    text = "@${user.username}",
                    fontWeight = FontWeight.W800,
                    modifier =
                        Modifier
                            .clickable {
                                onUserClicked()
                            },
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = formatDate(comment.timestamp),
                )
            }

            Text(
                text = comment.text,
            )
        }
    }
}

/**
 * Determines if the user is at the artwork location.
 */
private fun atArtLocation(distanceInM: Double?): Boolean {
    return distanceInM != null && distanceInM <= DISTANCE_AWAY_MARGIN
}

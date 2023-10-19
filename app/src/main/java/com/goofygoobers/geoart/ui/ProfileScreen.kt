/**
 * Composable components for the profile screen.
 */
package com.goofygoobers.geoart.ui

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.goofygoobers.geoart.R
import com.goofygoobers.geoart.ScreenNames
import com.goofygoobers.geoart.data.model.Art
import com.goofygoobers.geoart.data.model.User
import com.goofygoobers.geoart.navigateArt
import com.goofygoobers.geoart.navigateProfile
import com.goofygoobers.geoart.ui.components.ExpandableAsyncImage
import com.goofygoobers.geoart.ui.components.GetLocationName
import com.goofygoobers.geoart.ui.components.TopBar
import com.goofygoobers.geoart.util.formatDate
import com.goofygoobers.geoart.util.getGoogleApiKey
import com.goofygoobers.geoart.viewmodel.FollowSheetState
import com.goofygoobers.geoart.viewmodel.ProfileViewModel
import com.google.firebase.firestore.GeoPoint

/**
 * Displays the profile screen, allowing the user to view their profile and edit it and view their posts.
 * This screen is also used to view other users profiles.
 * The profile is updated in real time using event listeners on the database.
 *
 * @param userId The user id of the user to display the profile of.
 * @param navController The navigation controller to use.
 * @param viewModel The profile view model to use, injected by Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val spacerModifier: Modifier = Modifier.height(12.dp)
    val user by viewModel.user.collectAsState()
    val art by viewModel.art.collectAsState()
    val isFollowing by viewModel.isFollowing.collectAsState()
    val followSheetState by viewModel.followSheetState.collectAsState()
    val isCurrentUser = viewModel.isCurrentUser(userId)

    // Attach listeners and setup callback to detach listeners
    DisposableEffect(userId) {
        viewModel.attachListener(userId)
        viewModel.isFollowing(userId)
        onDispose {
            viewModel.detachListener()
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                canNavigateBack = !isCurrentUser,
            ) {
                if (isCurrentUser) {
                    IconButton(onClick = { navController.navigate(ScreenNames.Settings.name) }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        if (followSheetState != FollowSheetState.HIDDEN) {
            FollowersBottomSheet(
                onOpen = { navController.navigateProfile(it.id) },
            )
        }
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(start = 25.dp, end = 25.dp),
            ) {
                item(span = { GridItemSpan(2) }) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(top = 30.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            ExpandableAsyncImage(
                                model = user.pictureUri.ifBlank { R.drawable.pfp },
                                placeholder = painterResource(id = R.drawable.pfp),
                                contentDescription = "profile",
                                contentScale = ContentScale.Crop,
                                modifier =
                                    Modifier
                                        .clip(CircleShape)
                                        .size(92.dp),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                if (user.fullname.isNotEmpty()) {
                                    Text(
                                        text = user.fullname,
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                }

                                Text(
                                    text = "@${user.username}",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                if (isCurrentUser) {
                                    Button(
                                        onClick = {
                                            navController.navigate(
                                                ScreenNames.EditProfile.name,
                                            )
                                        },
                                        modifier = Modifier.size(130.dp, 40.dp),
                                    ) {
                                        Text(text = "Edit Profile")
                                    }
                                } else if (isFollowing != null) {
                                    Button(
                                        onClick = viewModel::follow,
                                        modifier = Modifier.size(130.dp, 40.dp),
                                    ) {
                                        Text(text = if (isFollowing!!) "Unfollow" else "Follow")
                                    }
                                }
                            }
                        }
                        Spacer(modifier = spacerModifier)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Text(text = user.bio)
                        }
                        Spacer(modifier = spacerModifier)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Text(
                                text = "${art.count()} posts",
                            )
                            ClickableText(
                                text = AnnotatedString("${user.followerCount} followers"),
                                onClick = {
                                    viewModel.onFollowersClick()
                                },
                                style =
                                    LocalTextStyle.current.copy(
                                        color = LocalContentColor.current,
                                    ),
                            )
                            ClickableText(
                                text = AnnotatedString("${user.followingCount} following"),
                                onClick = {
                                    viewModel.onFollowingClick()
                                },
                                style =
                                    LocalTextStyle.current.copy(
                                        color = LocalContentColor.current,
                                    ),
                            )
                        }
                    }
                }
                if (!user.isPrivate || isCurrentUser) {
                    items(art) {
                        ArtworkTile(it) {
                            navController.navigateArt(it.id)
                        }
                    }
                } else {
                    item {
                        Text(
                            "Private account.",
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Displays the list of followers or following in a bottom sheet.
 *
 * @param onOpen The callback to run when a user is clicked.
 * @param modifier The modifier to apply to the bottom sheet.
 * @param viewModel The profile view model to use, injected by Hilt.
 * @param sheetState The sheet state to use.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowersBottomSheet(
    onOpen: (User) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    val followSheetState by viewModel.followSheetState.collectAsState()
    val follows by viewModel.follows.collectAsState()

    ModalBottomSheet(
        onDismissRequest = viewModel::hideFollowSheet,
        sheetState = sheetState,
        modifier = modifier.heightIn(min = 400.dp),
    ) {
        Text(
            text = if (followSheetState == FollowSheetState.FOLLOWING) "Following" else "Followers",
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
        LazyColumn {
            if (follows.isEmpty()) {
                item {
                    Text(
                        text =
                            if (followSheetState == FollowSheetState.FOLLOWING) {
                                "You are not following anyone!"
                            } else {
                                "No one is following you!"
                            },
                        modifier = Modifier.padding(10.dp),
                    )
                }
            } else {
                items(follows) { user ->
                    FollowerBottomSheetSurface(user = user, onOpen = onOpen)
                }
            }
        }
    }
}

/**
 * The clickable surface that displays a follower or following in the bottom sheet.
 *
 * @param user The user to display.
 * @param onOpen The callback to run when the user is clicked.
 * @param modifier The modifier to apply to the surface.
 */
@Composable
fun FollowerBottomSheetSurface(
    user: User,
    onOpen: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = { onOpen(user) },
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(10.dp),
        ) {
            AsyncImage(
                model = user.pictureUri.ifBlank { R.drawable.pfp },
                placeholder = painterResource(id = R.drawable.pfp),
                contentDescription = "profile",
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .size(82.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (user.fullname.isNotEmpty()) {
                    Text(
                        text = user.fullname,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.titleMedium,
                )
                // TODO Maybe add unfollow/remove button for the current user
            }
        }
    }
}

/**
 * Displays a tile for an artwork.
 *
 * @param art The art to display.
 * @param onClick The callback to run when the tile is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkTile(
    art: Art,
    onClick: () -> Unit,
) {
    val spacerModifier: Modifier = Modifier.height(5.dp)
    var locationName by remember { mutableStateOf("") }

    GetLocationName(location = art.location, onLocationName = { locationName = it })

    Card(onClick = onClick) {
        Column {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp),
            ) {
                StaticMap(art.location)
            }
            Spacer(modifier = spacerModifier)
            Text(
                text = art.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 12.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = formatDate(art.timestamp),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 15.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = spacerModifier)
            Row {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "location",
                    modifier =
                        Modifier
                            .padding(start = 12.dp)
                            .size(16.dp),
                )
                Text(
                    text = locationName,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(end = 12.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = spacerModifier)
            Row {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "heart",
                    modifier =
                        Modifier
                            .padding(start = 12.dp)
                            .size(16.dp),
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "${art.likeCount}",
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Outlined.Message,
                    contentDescription = "review",
                    Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "${art.commentCount}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

/**
 * Displays a static Google Map of the location of the artwork.
 *
 * @param geoPoint The location of the artwork to put the marker.
 */
@Composable
fun StaticMap(geoPoint: GeoPoint?) {
    var model by remember { mutableStateOf<Any>(R.drawable.default_img) }

    // Construct the query
    ConstructGoogleStaticMapQuery(geoPoint) { model = it }

    Log.d("MODEL", model.toString())
    AsyncImage(
        model = model,
        placeholder = painterResource(id = R.drawable.default_img),
        contentDescription = "minimap",
        modifier = Modifier.size(146.dp),
    )
}

/**
 * Constructs the query for the Google Static Maps API.
 *
 * @param geoPoint The location of the artwork to put the marker.
 * @param onReady The callback to run when the query is ready.
 */
@Composable
fun ConstructGoogleStaticMapQuery(
    geoPoint: GeoPoint?,
    onReady: (String) -> Unit,
) {
    var apiKey by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val markerIconUrl = remember { context.getString(R.string.map_marker_url) }
    val isDark = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        apiKey = context.getGoogleApiKey()
    }

    LaunchedEffect(apiKey != null && geoPoint != null) {
        if (apiKey == null || geoPoint == null) {
            return@LaunchedEffect
        }

        val builder = StringBuilder()
        builder.append("https://maps.googleapis.com/maps/api/staticmap?key=$apiKey")
        builder.append("&size=146x146&scale=2&zoom=15")
        builder.append("&markers=icon:$markerIconUrl|${geoPoint.latitude},${geoPoint.longitude}")
        if (isDark) {
            builder.append(
                "&style=element:geometry%7Ccolor:0x242f3e&style=element:labels.text.fill%7Ccolor:0x746855&style=element:labels.text.stroke%7Ccolor:0x242f3e&style=feature:administrative.locality%7Celement:labels.text.fill%7Ccolor:0xd59563&style=feature:poi%7Celement:labels.text%7Cvisibility:off&style=feature:poi%7Celement:labels.text.fill%7Ccolor:0xd59563&style=feature:poi.business%7Cvisibility:off&style=feature:poi.park%7Celement:geometry%7Ccolor:0x263c3f&style=feature:poi.park%7Celement:labels.text.fill%7Ccolor:0x6b9a76&style=feature:road%7Celement:geometry%7Ccolor:0x38414e&style=feature:road%7Celement:geometry.stroke%7Ccolor:0x212a37&style=feature:road%7Celement:labels.icon%7Cvisibility:off&style=feature:road%7Celement:labels.text.fill%7Ccolor:0x9ca5b3&style=feature:road.highway%7Celement:geometry%7Ccolor:0x746855&style=feature:road.highway%7Celement:geometry.stroke%7Ccolor:0x1f2835&style=feature:road.highway%7Celement:labels.text.fill%7Ccolor:0xf3d19c&style=feature:transit%7Cvisibility:off&style=feature:transit%7Celement:geometry%7Ccolor:0x2f3948&style=feature:transit.station%7Celement:labels.text.fill%7Ccolor:0xd59563&style=feature:water%7Celement:geometry%7Ccolor:0x17263c&style=feature:water%7Celement:labels.text.fill%7Ccolor:0x515c6d&style=feature:water%7Celement:labels.text.stroke%7Ccolor:0x17263c",
            )
        } else {
            builder.append(
                "&style=feature:poi%7Celement:labels.text%7Cvisibility:off&style=feature:poi.business%7Cvisibility:off&style=feature:road%7Celement:labels.icon%7Cvisibility:off&style=feature:transit%7Cvisibility:off",
            )
        }
        onReady(builder.toString())
    }
}

@Composable
private fun ProfileScreenPreview() {
    ProfileScreen("", navController = rememberNavController())
}

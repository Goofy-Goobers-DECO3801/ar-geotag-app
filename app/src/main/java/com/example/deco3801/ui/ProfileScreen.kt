package com.example.deco3801.ui

import android.util.Log
import androidx.compose.foundation.background
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
import com.example.deco3801.R
import com.example.deco3801.ScreenNames
import com.example.deco3801.data.model.Art
import com.example.deco3801.data.model.User
import com.example.deco3801.navigateArt
import com.example.deco3801.navigateProfile
import com.example.deco3801.ui.components.ExpandableAsyncImage
import com.example.deco3801.ui.components.GetLocationName
import com.example.deco3801.ui.components.TopBar
import com.example.deco3801.util.formatDate
import com.example.deco3801.util.getGoogleApiKey
import com.example.deco3801.viewmodel.FollowSheetState
import com.example.deco3801.viewmodel.ProfileViewModel
import com.google.firebase.firestore.GeoPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


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
                    IconButton(onClick = {navController.navigate(ScreenNames.Settings.name)}) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
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
                        horizontalAlignment = Alignment.CenterHorizontally
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
                                        modifier = Modifier.size(120.dp,40.dp)
                                    ) {
                                        Text(text = "Edit Profile")
                                    }
                                } else if (isFollowing != null) {
                                    Button(
                                        onClick = viewModel::follow,
                                        modifier = Modifier.size(120.dp,40.dp)) {
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
        modifier = modifier.heightIn(min=400.dp),
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
                    .background(MaterialTheme.colorScheme.onError)
        )
        LazyColumn {
            if (follows.isEmpty()) {
                item {
                    Text(
                        text =
                        if (followSheetState == FollowSheetState.FOLLOWING)  {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkTile(
    art: Art,
    onClick: () -> Unit,
) {
    val spacerModifier: Modifier = Modifier.height(5.dp)
    var locationName by remember { mutableStateOf("") }

    GetLocationName(location = art.location, onLocationName = {locationName = it})

    Card(onClick = onClick) {
        Column {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                StaticMap(art.location)
            }
            Spacer(modifier = spacerModifier)
            Text(
                text = art.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 12.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatDate(art.timestamp),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 15.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
                    overflow = TextOverflow.Ellipsis
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

@Composable
fun StaticMap(
    geoPoint: GeoPoint?
) {
    var apiKey by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val markerIconUrl by remember {
        mutableStateOf(
            URLEncoder.encode(
                context.getString(R.string.map_marker_url),
                StandardCharsets.UTF_8.toString()
            )
        )
    }

    LaunchedEffect(Unit) {
        apiKey = context.getGoogleApiKey()
    }

    val model =
        if (apiKey != null && geoPoint != null) {
            "https://maps.googleapis.com/maps/api/staticmap?size=146x146" +
                "&markers=icon:${markerIconUrl}|${geoPoint.latitude},${geoPoint.longitude}" +
                "&key=${apiKey!!}"
        } else {
            R.drawable.default_img
        }

    Log.d("MODEL", model.toString())
    AsyncImage(
        model = model,
        contentDescription = "minimap",
        modifier = Modifier.size(146.dp),
    )
}

@Composable
private fun ProfileScreenPreview() {
    ProfileScreen("", navController = rememberNavController())
}

// @Composable
// fun ProfileGrid (modifier: Modifier = Modifier) {
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        verticalArrangement = Arrangement.spacedBy(12.dp),
//        horizontalArrangement = Arrangement.spacedBy(12.dp),
//        modifier = modifier
//    ) {
//        items(DataSource.profiles) { topic ->
//            ArtworkTile(topic)
//        }
//    }
// }

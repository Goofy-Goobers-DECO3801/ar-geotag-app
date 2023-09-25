package com.example.deco3801.ui

import android.icu.text.SimpleDateFormat
import android.location.Geocoder
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.deco3801.R
import com.example.deco3801.ScreenNames
import com.example.deco3801.data.model.Art
import com.example.deco3801.ui.components.TopBar
import com.example.deco3801.viewmodel.ProfileViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    navController: NavHostController,
    modifier: Modifier = Modifier.padding(8.dp),
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val spacerModifier: Modifier = Modifier.height(12.dp)
    val user by viewModel.user.collectAsState()
    val art by viewModel.art.collectAsState()
    val isCurrentUser = userId == Firebase.auth.uid!!


    DisposableEffect(userId) {
        viewModel.attachListener(userId)
        onDispose {
            viewModel.detachListener()
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                canNavigateBack = false,
                showSettings = isCurrentUser,
                navigateUp = { navController.navigate(ScreenNames.Settings.name) }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier
            ) {
                item( span = { GridItemSpan(2) } ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            AsyncImage(
                                model = user.pictureUri.ifBlank { R.drawable.pfp },
                                placeholder = painterResource(id = R.drawable.pfp),
                                contentDescription = "profile",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(92.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                if (user.fullname.isNotEmpty()) {
                                    Text(
                                        text = user.fullname,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }

                                Text(
                                    text = "@${user.username}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (isCurrentUser) {
                                    Button(onClick = { /*TODO*/ }) {
                                        Text(text = "Edit Profile")
                                    }
                                } else {
                                    Button(onClick = { /*TODO*/ }) {
                                        Text(text = "Follow")
                                    }
                                }

                            }
                        }
                        Spacer(modifier = spacerModifier)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "${art.count()} posts"
                            )
                            Text( // TODO FOLLOWERS
                                text = "12 followers"
                            )
                            Text(
                                text = "13 following"
                            )
                        }

                    }
                }
                if (!user.isPrivate || isCurrentUser) {
                    items(art) { topic ->
                        ArtworkTile(topic)
                    }
                } else {
                    item {
                        Text(
                            "Private account.",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreenPreview() {
    ProfileScreen("", navController = rememberNavController())
}


@Composable
fun ArtworkTile(
    art: Art
) {
    val spacerModifier: Modifier = Modifier.height(8.dp)
    val gcd = Geocoder(LocalContext.current, Locale.getDefault())
    var locationName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gcd.getFromLocation(art.location!!.latitude, art.location!!.longitude, 1) {
                locationName = "${it[0].locality}, ${it[0].adminArea}"
            }
        }
    }

    Card {
        Column {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // TODO: map preview
                AsyncImage(
                    model = R.drawable.default_img,
                    contentDescription = "post",
                    modifier = Modifier.size(146.dp)
                )
            }
            Spacer(modifier = spacerModifier)
            Text(
                text = art.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = formatDate(art.timestamp!!),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
            Row {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "location",
                    Modifier
                        .padding(start = 16.dp)
                        .size(16.dp)
                )
                Text(
                    text = locationName,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "heart",
                    Modifier
                        .padding(start = 16.dp)
                        .size(16.dp)
                )
                Text(
                    text = "${art.likeCount} likes",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer (modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "heart",
                    Modifier.size(16.dp)
                )

                Text(
                    text = "${art.reviewCount} reviews",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

fun formatDate(date: Date): String {
    val now = Date()
    val diff = now.time - date.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "just now"
        minutes < 60 -> "$minutes ${if (minutes.toInt() == 1) "minute" else "minutes"} ago"
        hours < 24 -> "$hours ${if (hours.toInt() == 1) "hour" else "hours"} ago"
        days < 7 -> "$days ${if (days.toInt() == 1) "day" else "days"} ago"
        else -> {
            val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(now)
            val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
            if (currentYear == year) {
                SimpleDateFormat("dd MMM", Locale.getDefault()).format(date)
            } else {
                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
            }
        }
    }
}


//@Composable
//fun ProfileGrid (modifier: Modifier = Modifier) {
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
//}




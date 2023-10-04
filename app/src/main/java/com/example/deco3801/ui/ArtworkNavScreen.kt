package com.example.deco3801.ui

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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.deco3801.navigateAR
import com.example.deco3801.ui.theme.UnchangingAppColors
import com.example.deco3801.viewmodel.ArtworkNavViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkNavScreen(
    artId: String,
    navController: NavHostController,
    viewModel: ArtworkNavViewModel = hiltViewModel()
) {

    val art by viewModel.art.collectAsState()
    val user by viewModel.user.collectAsState()
    val liked by viewModel.liked.collectAsState()

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
                artworkTitle = art.title,
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                ArtworkTitle(art, user) {
                    navController.navigate("${ScreenNames.Profile.name}/${user.id}")
                }
            }
            item {
                ArtworkMap()
            }
            item {
                ArtworkInteract(
                    distance = 0, // TODO
                    onArClicked = {
                        navController.navigateAR(art.storageUri)
                    },
                    )
            }
            item {
                ArtworkDescription(art, liked) {
                    viewModel.onLikeClicked()
                }
            }
        }
    }
}

@Composable
fun ArtworkTitle(
    art: Art,
    user: User,
    onUserClicked: () -> Unit = {},
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
                .background(UnchangingAppColors.main_theme),
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
    navController: NavHostController,
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
fun ArtworkMap() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(200.dp))
        Text("Map goes here")
        Spacer(Modifier.height(200.dp))
    }
}

@Composable
fun ArtworkInteract(
    distance: Int,
    onArClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(UnchangingAppColors.main_theme),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (distance == 0) {
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
                text = "$distance m",
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
    onLikeClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                enabled = liked != null,
                onClick = onLikeClicked,
            ) {
                Icon(
                    imageVector = if (liked != null && liked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "heart",
                    tint = if (liked != null && liked) Color.Red else Color.Unspecified,
                )
            }
            Spacer(Modifier.width(5.dp))
            Text("${art.likeCount} likes")
            Spacer(Modifier.width(10.dp))
            IconButton(
                onClick = { },
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
fun PreviewArtworkNavScreen() {
    ArtworkNavScreen(
        "1",
        rememberNavController()
    )
}
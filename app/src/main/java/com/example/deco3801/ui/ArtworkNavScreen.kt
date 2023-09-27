package com.example.deco3801.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deco3801.R
import com.example.deco3801.ui.components.TopBar
import com.example.deco3801.ui.theme.UnchangingAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtworkNavScreen(
    navigateUp: () -> Unit,
    artworkTitle: String,
    username: String,
    dateCreated: String,
    distance: Int,
    description: String,
    numLikes: Int,
    numReviews: Int
) {
    Scaffold (
        topBar = { TopBar(
            canNavigateBack = true,
            showSettings = false,
            navigateUp = navigateUp
        ) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                ArtworkTitle(
                    artworkTitle = artworkTitle,
                    username = username,
                    dateCreated = dateCreated
                )
            }
            item {
                ArtworkMap()
            }
            item {
                ArtworkInteract(distance = distance)
            }
            item {
                ArtworkDescription(
                    description = description,
                    numLikes = numLikes,
                    numReviews = numReviews
                )
            }
        }
    }
}

@Composable
fun ArtworkTitle(
    artworkTitle: String,
    username: String,
    dateCreated: String
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(UnchangingAppColors.main_theme)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = artworkTitle,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 25.dp,
                    top = 10.dp,
                    bottom = 25.dp
                )
        ) {
            Text(
                text = "Created by: $username",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "on $dateCreated",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun ArtworkMap() {
    Column(modifier = Modifier
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(200.dp))
        Text("Map goes here")
        Spacer(Modifier.height(200.dp))
    }
}

@Composable
fun ArtworkInteract(distance: Int) {
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
                onClick = { /*TODO*/ },
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
            Text(text = "$distance m",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                modifier = Modifier.padding(15.dp)
            )
        }
    }
}

@Composable
fun ArtworkDescription(
    description: String,
    numLikes: Int,
    numReviews: Int
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row() {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = "heart"
            )
            Spacer(Modifier.width(10.dp))
            Text("$numLikes likes")
            Spacer(Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Outlined.Message,
                contentDescription = "reviews"
            )
            Spacer(Modifier.width(10.dp))
            Text("$numReviews reviews")
        }
        Spacer(Modifier.height(10.dp))
        Text(description)
    }
}

@Preview
@Composable
fun PreviewArtworkNavScreen() {
    ArtworkNavScreen(
        {},
        "Artwork Title",
        "Username",
        "11/09/2001",
        0,
        stringResource(id = R.string.placeholder),
        13,
        5
    )
}
package com.example.deco3801.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deco3801.R
import com.example.deco3801.ui.data.DataSource
import com.example.deco3801.ui.model.ProfilePost


@Composable
fun ProfileScreen(modifier: Modifier = Modifier.padding(12.dp)) {
    val spacerModifier : Modifier = Modifier.height(12.dp)
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        item( span = { GridItemSpan(2) } ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pfp),
                        contentDescription = "profile",
                        modifier = Modifier.size(92.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Full Name",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "@Username",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = { /*TODO*/ }) {
                            Text(text = "Edit Profile")
                        }
                    }
                }
                Spacer(modifier = spacerModifier)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "5 posts"
                    )
                    Text(
                        text = "12 followers"
                    )
                    Text(
                        text = "13 following"
                    )
                }

        }
    }
    items(DataSource.profiles) { topic ->
        ArtworkTile(topic)
    }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}

@Composable
fun ArtworkTile(
    profilePost : ProfilePost, modifier: Modifier = Modifier
    ) {
    val spacerModifier : Modifier = Modifier.height(8.dp)
    Card {
        Column () {
            Spacer(modifier = Modifier.height(14.dp))
            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = profilePost.imagePreview),
                    contentDescription = "post",
                    modifier = Modifier.size(146.dp)
                )
            }
            Spacer(modifier = spacerModifier)
            Text(
                text = stringResource(id = profilePost.artworkTitle),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = "Created " + stringResource(id = profilePost.date),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
            Row () {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "location",
                    Modifier
                        .padding(start = 16.dp)
                        .size(16.dp)
                )
                Text(
                    text = stringResource(id = profilePost.location),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row () {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "heart",
                    Modifier
                        .padding(start = 16.dp)
                        .size(16.dp)
                )
                Text(
                    text = profilePost.likes.toString() + " likes",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer (modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "heart",
                    Modifier.size(16.dp)
                )

                Text(
                    text = profilePost.reviews.toString() + " reviews",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
fun ProfileGrid (modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(DataSource.profiles) { topic ->
            ArtworkTile(topic)
        }
    }
}




package com.example.deco3801.ui

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import com.example.deco3801.ui.components.NavBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.R


@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.pfp),
            contentDescription = "profile",
            modifier = Modifier.size(62.dp)
        )
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
        ArtworkTile("Artwork Title", "30/08/2023","St Lucia, 4067", 78,
            21, R.drawable.default_img)

    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}

@Composable
fun ArtworkTile(
    artworkTitle: String,
    date: String,
    location: String,
    likes: Int,
    reviews: Int,
    img: Int
    ) {

    Image(
        painter = painterResource(id = img),
        contentDescription = "post",
        modifier = Modifier.size(130.dp)
    )
    Text(
        text = artworkTitle,
        style = MaterialTheme.typography.titleMedium
    )
    Text(
        text = "Created on $date",
        style = MaterialTheme.typography.bodySmall
    )
    Row () {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = "location",
            Modifier.size(16.dp)
        )
        Text(
            text = location,
            style = MaterialTheme.typography.bodySmall
        )
    }
    Row () {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = "heart",
            Modifier.size(16.dp)
        )
        Text(
            text = "$likes likes",
            style = MaterialTheme.typography.bodySmall
        )
        Icon(
            imageVector = Icons.Filled.FavoriteBorder,
            contentDescription = "heart",
            Modifier.size(16.dp)
        )
        Text(
            text = "$reviews reviews",
            style = MaterialTheme.typography.bodySmall
        )
    }





}



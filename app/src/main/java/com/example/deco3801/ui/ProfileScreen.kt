package com.example.deco3801.ui

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import com.example.deco3801.ui.components.NavBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
        modifier = Modifier.size(62.dp)
    )
    Text(
        text = artworkTitle,
        style = MaterialTheme.typography.titleMedium
    )
    Text(
        text = "Created on $date"
    )
    Text(
        text = location
    )
    Text(
        text = "" + likes + "likes"
    )
    Text(
        text = "" + reviews + "reviews"
    )




}



package com.example.deco3801.ui

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.deco3801.R
import com.example.deco3801.ScreenNames
import com.example.deco3801.data.model.User
import com.example.deco3801.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowScreen(
    userId: String,
    navController: NavHostController,
    modifier: Modifier = Modifier.padding(8.dp),
    viewModel: ProfileViewModel = hiltViewModel(),
    follow: String = "Followers"
) {
    val user by viewModel.user.collectAsState()
    val follows by viewModel.follows.collectAsState()
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        val textModifier: Modifier = Modifier
        val spacerModifier: Modifier = Modifier.height(10.dp)
        item {
            Text(
                text = "Edit Profile",
                modifier = textModifier,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        items(follows) { acc ->
            ProfileTile(acc)
        }
    }
}


@Composable
fun ProfileTile (user: User) {
    Card() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            AsyncImage(
                model = user.pictureUri.ifBlank { R.drawable.pfp },
                placeholder = painterResource(id = R.drawable.pfp),
                contentDescription = "profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(82.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (user.fullname.isNotEmpty()) {
                    Text(
                        text = user.fullname,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    // TODO Maybe add unfollow/remove button for the current user
            }
        }
    }
}
package com.example.deco3801.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deco3801.R
import java.lang.Boolean.TRUE




////below this is temp testing
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.painter.Painter
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import com.example.deco3801.R
//import com.bumptech.glide.Glide

//
//@Composable
//fun ProfilePictureSection() {
//    var profilePictureUri by remember { mutableStateOf<String?>(null) }
//
//    // Define a launcher for the image picker
//    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        profilePictureUri = uri.toString()
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Box(
//            modifier = Modifier
//                .size(120.dp)
//                .clickable {
//                    // Launch the image picker when the user clicks the profile picture
//                    imagePickerLauncher.launch("image/*")
//                }
//                .clip(CircleShape)
//                .background(MaterialTheme.colorScheme.primary)
//                .padding(4.dp),
//            contentAlignment = Alignment.Center
//        ) {
//
//                // Display a placeholder image or icon
//                val placeholder: Painter = painterResource(id = R.drawable.pfp)
//                Image(
//                    painter = placeholder,
//                    contentDescription = null,
//                    modifier = Modifier.fillMaxSize()
//                )
//
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(
//            text = "Change Profile Picture",
//            style = MaterialTheme.typography.body1,
//            color = Color.Gray,
//            modifier = Modifier.clickable {
//                // Launch the image picker when the user clicks the text
//                imagePickerLauncher.launch("image/*")
//            }
//        )
//    }
//}
//
//@Preview
//@Composable
//fun ProfilePictureSectionPreview() {
//    ProfilePictureSection()
//}
/// above this is temp testing

@Composable
fun EditProfileScreen(modifier : Modifier = Modifier) {
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
    ) {
        val isPrivate = TRUE //TODO viewModel.isPrivateAccountState
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
        item {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Column() {
                    Image(
                        painter = painterResource(id = R.drawable.pfp),
                        contentDescription = "profile",
                        modifier = Modifier.size(108.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ClickableText(text = AnnotatedString("Edit profile picture"), onClick = {})
                }
            }
        }
        item {
            Spacer(modifier = spacerModifier)
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .clickable {
                        /*TODO*/
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Recently visited artworks")
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        item {
            Spacer(modifier = spacerModifier)
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .clickable {
                        /*TODO*/
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Following")
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        item {
            Spacer(modifier = spacerModifier)
        }
        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Save Changes")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen()
}
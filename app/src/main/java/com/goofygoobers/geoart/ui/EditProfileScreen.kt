/**
 * Screen for editing the user's profile.
 */
package com.goofygoobers.geoart.ui

import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.goofygoobers.geoart.R
import com.goofygoobers.geoart.ui.components.BioField
import com.goofygoobers.geoart.ui.components.NameField
import com.goofygoobers.geoart.ui.components.SnackbarManager
import com.goofygoobers.geoart.ui.components.TopBar
import com.goofygoobers.geoart.viewmodel.EditProfileViewModel
import java.io.File

/**
 * Displays the edit profile screen, allowing users to change their profile picture, username,
 * full name and bio.
 *
 * @param navController The navigation controller to use.
 * @param modifier The modifier to apply to this layout node.
 * @param viewModel The edit profile view model, injected by Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var takePhotoFile by remember { mutableStateOf<File?>(null) }
    var takePhotoUri by remember { mutableStateOf<Uri?>(null) }
    val user by viewModel.newUser.collectAsState()

    // Activity to launch the image picker when the user clicks the choose from library button.
    val imagePicker =
        rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
            uri?.let(viewModel::onPictureChange)
        }
    // Activity to launch the camera when the user clicks the take photo button.
    val takePhoto =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { bool ->
            if (!bool || takePhotoUri == null || takePhotoFile == null) {
                SnackbarManager.showError("Failed to take photo, please try again!")
                return@rememberLauncherForActivityResult
            }
            viewModel.onPictureChange(takePhotoUri!!)
        }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                canNavigateBack = true,
            )
        },
    ) { innerPadding ->
        if (showBottomSheet) {
            // Bottom sheet for choosing options for changing the profile picture.
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
            ) {
                // Sheet content
                BottomSheetSurface(
                    text = "Choose from library",
                    onClick = {
                        showBottomSheet = false
                        imagePicker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                    },
                    icon = Icons.Filled.Image,
                    iconDescription = "image",
                )
                BottomSheetSurface(
                    text = "Take photo",
                    onClick = {
                        showBottomSheet = false
                        takePhotoFile =
                            File.createTempFile(
                                System.currentTimeMillis().toString(),
                                ".jpg",
                                File(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                                    "Camera",
                                ),
                            )
                        takePhotoUri =
                            FileProvider.getUriForFile(
                                context,
                                context.applicationContext.packageName + ".provider",
                                takePhotoFile!!,
                            )
                        takePhoto.launch(takePhotoUri)
                    },
                    icon = Icons.Filled.PhotoCamera,
                    iconDescription = "camera",
                )
                BottomSheetSurface(
                    text = "Remove current picture",
                    onClick = {
                        showBottomSheet = false
                        viewModel.onPictureRemove()
                    },
                    icon = Icons.Filled.Delete,
                    iconDescription = "delete",
                    contentColor = Color.Red,
                )
                Spacer(Modifier.height(20.dp))
            }
        }
        LazyColumn(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
        ) {
            val textModifier: Modifier = Modifier
            val spacerModifier: Modifier = Modifier.height(10.dp)
            item {
                Text(
                    text = "Edit Profile",
                    modifier = textModifier,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            item {
                Row(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = modifier.fillMaxWidth(),
                    ) {
                        AsyncImage(
                            model = user.pictureUri.ifBlank { R.drawable.pfp },
                            placeholder = painterResource(id = R.drawable.pfp),
                            contentDescription = "profile",
                            contentScale = ContentScale.Crop,
                            modifier =
                                Modifier
                                    .clip(CircleShape)
                                    .size(108.dp)
                                    .clickable {
                                        showBottomSheet = true
                                    },
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ClickableText(
                            text = AnnotatedString("Change profile picture"),
                            onClick = {
                                showBottomSheet = true
                            },
                        )
                    }
                }
            }
            item {
                Spacer(modifier = spacerModifier)
            }
            item {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp),
                ) {
                    Column {
                        Text(text = "Change Username")
                        Spacer(modifier = spacerModifier)
                        NameField(
                            modifier = modifier.fillMaxWidth(),
                            value = user.username,
                            label = "Username",
                            onValueChange = viewModel::onUsernameChange,
                        )
                    }
                }
            }
            item {
                Spacer(modifier = spacerModifier)
            }
            item {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp),
                ) {
                    Column {
                        Text(text = "Change Full Name")
                        Spacer(modifier = spacerModifier)
                        NameField(
                            modifier = modifier.fillMaxWidth(),
                            value = user.fullname,
                            label = "Full Name",
                            onValueChange = viewModel::onFullnameChange,
                        )
                    }
                }
            }
            item {
                Spacer(modifier = spacerModifier)
            }
            item {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp),
                ) {
                    Column {
                        Text(text = "Edit Bio")
                        Spacer(modifier = spacerModifier)
                        BioField(
                            value = user.bio,
                            onValueChange = viewModel::onBioChange,
                            modifier = modifier.fillMaxWidth(),
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
                    modifier =
                        modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                ) {
                    Button(onClick = {
                        viewModel.onSave(navController::popBackStack)
                    }) {
                        Text(text = "Save Changes")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    EditProfileScreen(rememberNavController())
}

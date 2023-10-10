package com.example.deco3801.ui

import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.R
import com.example.deco3801.ScreenNames
import com.example.deco3801.ui.components.ExpandableAsyncImage
import com.example.deco3801.ui.components.SnackbarManager
import com.example.deco3801.ui.components.TopBar
import com.example.deco3801.util.LocationUtil.getCurrentLocation
import com.example.deco3801.viewmodel.CreateViewModel
import com.example.deco3801.viewmodel.getFileName
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    navController: NavHostController,
    viewModel: CreateViewModel = hiltViewModel(),
) {
    val textModifier: Modifier = Modifier
    val textFieldModifier: Modifier = Modifier.fillMaxWidth()
    val spacerModifier: Modifier = Modifier.height(10.dp)
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var takePhotoFile by remember { mutableStateOf<File?>(null) }
    var takePhotoUri by remember { mutableStateOf<Uri?>(null) }

    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.onLocationChange(getCurrentLocation(context))
    }

    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            try {
                val bytes = context.contentResolver
                    .openInputStream(uri)
                    ?.use { it.buffered().readBytes() }
                viewModel.onSelectImage(uri.getFileName(context), bytes!!)
            } catch (e: Exception) {
                SnackbarManager.showError("Unable to upload file!")
                Log.e("CREATE", e.stackTraceToString())
            }
        }

    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            viewModel.onSelectFile(uri.getFileName(context), uri)
        }


    val takePhoto =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { bool ->
            if (!bool || takePhotoUri == null || takePhotoFile == null) {
                Log.e("CREATE", "Failed to take photo")
                return@rememberLauncherForActivityResult
            }
            try {
                val bytes = context.contentResolver
                    .openInputStream(takePhotoUri!!)
                    ?.use { it.buffered().readBytes() }

                viewModel.onSelectImage(takePhotoUri!!.getFileName(context), bytes!!)
            } catch (e: Exception) {
                SnackbarManager.showError("Unable to upload file!")
                Log.e("CREATE", e.stackTraceToString())
            } finally {
                takePhotoFile!!.delete()
            }
        }

    Scaffold(
        topBar = {
            TopBar(
                canNavigateBack = false,
                showSettings = false,
                navigateUp = {}
            )
        },
    ) { innerPadding ->
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
            ) {
                // Sheet content
                BottomSheetSurface(
                    text = "Pick image",
                    onClick = {
                        showBottomSheet = false
                        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    icon = Icons.Filled.Image,
                    iconDescription = "image"
                )
                BottomSheetSurface(
                    text = "Pick 3D Model",
                    onClick = {
                        showBottomSheet = false
                        filePicker.launch("model/gltf-binary")
//                        filePicker.launch("*/*")
                    },
                    icon = Icons.Filled.ViewInAr,
                    iconDescription = "model"
                )
                BottomSheetSurface(
                    text = "Take photo",
                    onClick = {
                        showBottomSheet = false
                        takePhotoFile = File.createTempFile(
                            System.currentTimeMillis().toString(),
                            ".jpg",
                            File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                                "Camera"
                            )
                        )
                        takePhotoUri = FileProvider.getUriForFile(
                            context,
                            context.applicationContext.packageName + ".provider",
                            takePhotoFile!!
                        )
                        takePhoto.launch(takePhotoUri)
                    },
                    icon = Icons.Filled.PhotoCamera,
                    iconDescription = "camera"
                )
            }
        }
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 35.dp,
                        end = 35.dp
                    )
            ) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Upload an Artwork",
                        modifier = textModifier,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = spacerModifier)
                }
                item {
                    Text(
                        text = "Title",
                        modifier = textModifier,
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextField(
                        value = uiState.title,
                        onValueChange = viewModel::onTitleChange,
                        modifier = textFieldModifier,
                    )
                    Spacer(modifier = spacerModifier)


                    Text(
                        text = "Upload Artwork",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(
                        onClick = {
                            showBottomSheet = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 50.dp, end = 50.dp)
                    ) {
                        Icon(Icons.Filled.Upload, contentDescription = "upload")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Upload")
                    }
                    Button(
                        onClick = {
                            navController.navigate(ScreenNames.ARscreen.name)
                        },
                        enabled = uiState.uri != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 50.dp, end = 50.dp)
                    ) {
                        Icon(Icons.Filled.ViewInAr, contentDescription = "preview")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Preview in AR")
                    }

                    Spacer(modifier = spacerModifier)
                    if (uiState.imageBytes != null) {
                        ExpandableAsyncImage(
                            model = uiState.imageBytes,
                            placeholder = painterResource(id = R.drawable.default_img),
                            contentDescription = "profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = spacerModifier)

                    } else if (uiState.filename.isNotBlank()) {
                        Text(
                            text = "Selected File: ${uiState.filename}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = spacerModifier)
                    }

                    Text(
                        text = "Artwork Description",
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextField(
                        value = uiState.description,
                        onValueChange = viewModel::onDescriptionChange,
                        modifier = textFieldModifier.height(130.dp)
                    )
                    Spacer(modifier = spacerModifier)


                    Text(
                        text = "Select Location",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 50.dp, end = 50.dp),
                        onClick = { /* TODO */ }
                    ) {
                        Text(text = "Select Location")
                    }
                    Spacer(modifier = spacerModifier)
                    // Display the selected location
                    uiState.location?.let {
                        Text(
                            text = "Location: $it", /* TODO: Make this a map? */
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = spacerModifier)
                    }


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                viewModel.onPostArtwork(
                                    open = {
                                        navController.navigate(it)
                                    }
                                )
                            },
                            enabled = viewModel.isValid()
                        ) {
                            Text(text = "Post Artwork")
                        }
                    }
                    Spacer(Modifier.height(50.dp))
                }
            }
        }
    }

}

@Composable
fun BottomSheetSurface(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector,
    iconDescription: String,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(color),
) {
    Surface(
        onClick = onClick,
        enabled = true,
        modifier = Modifier.fillMaxWidth(),
        color = color,
        contentColor= contentColor,
    ) {
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription
            )
            Spacer(Modifier.width(10.dp))
            Text(text)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CreateScreenPreview() {
    CreateScreen(navController = rememberNavController())
}

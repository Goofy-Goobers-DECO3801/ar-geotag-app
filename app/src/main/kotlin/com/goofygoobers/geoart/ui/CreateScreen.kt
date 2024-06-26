/**
 * Composable components for the Create screen.
 */
package com.goofygoobers.geoart.ui

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
import androidx.compose.material.icons.filled.Animation
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
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.goofygoobers.geoart.R
import com.goofygoobers.geoart.navigateAR
import com.goofygoobers.geoart.ui.components.ExpandableAsyncImage
import com.goofygoobers.geoart.ui.components.GetLocationName
import com.goofygoobers.geoart.ui.components.SnackbarManager
import com.goofygoobers.geoart.ui.components.TopBar
import com.goofygoobers.geoart.util.getCurrentLocation
import com.goofygoobers.geoart.viewmodel.CreateViewModel
import com.goofygoobers.geoart.viewmodel.getFileName
import java.io.File

/**
 * Displays the create screen.
 * This screen allows the user to create a new artwork with a title, description,
 * and image ,photo or 3D model.
 *
 * @param navController The navigation controller.
 * @param viewModel The create view model injected by Hilt.
 */
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
    var showSampleList by remember { mutableStateOf(false) }

    val uiState = viewModel.uiState
    var locationName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.onLocationChange(context.getCurrentLocation())
    }

    // Decode the latitude and longitude to an address.
    GetLocationName(location = uiState.location, onLocationName = {
        locationName = it
    }, fullAddress = true)

    // Activity to launch the image picker when the user clicks the select image button.
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            try {
                val bytes =
                    context.contentResolver
                        .openInputStream(uri)
                        ?.use { it.buffered().readBytes() }
                viewModel.onSelectImage(uri.getFileName(context), bytes!!)
            } catch (e: Exception) {
                SnackbarManager.showError("Unable to upload file!")
                Log.e("CREATE", e.stackTraceToString())
            }
        }

    // Activity to launch the file picker when the user clicks the select 3D model button.
    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            viewModel.onSelectFile(uri.getFileName(context), uri)
        }

    // Activity to launch the camera when the user clicks the take photo button.
    val takePhoto =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { bool ->
            if (!bool || takePhotoUri == null || takePhotoFile == null) {
                SnackbarManager.showError("Failed to take photo, please try again!")
                return@rememberLauncherForActivityResult
            }
            try {
                val bytes =
                    context.contentResolver
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
            TopBar(navController)
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
                    text = "Select image",
                    onClick = {
                        showBottomSheet = false
                        imagePicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly,
                            ),
                        )
                    },
                    icon = Icons.Filled.Image,
                    iconDescription = "image",
                )
                BottomSheetSurface(
                    text = "Select 3D model",
                    onClick = {
                        showBottomSheet = false
                        filePicker.launch("application/octet-stream")
//                        filePicker.launch("*/*")
                    },
                    icon = Icons.Filled.ViewInAr,
                    iconDescription = "model",
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
                    text = "Select from samples",
                    onClick = {
                        showBottomSheet = false
                        showSampleList = true
                    },
                    icon = Icons.Filled.Animation,
                    iconDescription = "sample",
                )
                Spacer(Modifier.height(20.dp))
            }
        }
        if (showSampleList) {
            SampleModelList(
                onDismissRequest = {
                    showSampleList = false
                },
                onSelect = viewModel::onSelectFile,
            )
        }
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            start = 30.dp,
                            end = 30.dp,
                        ),
            ) {
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Upload an Artwork",
                        modifier = textModifier,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = spacerModifier)
                }
                item {
                    Text(
                        text = "Title",
                        modifier = textModifier,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    TextField(
                        value = uiState.title,
                        onValueChange = viewModel::onTitleChange,
                        modifier = textFieldModifier,
                        singleLine = true,
                    )
                    Spacer(modifier = spacerModifier)

                    Text(
                        text = "Upload Artwork",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Button(
                        onClick = {
                            showBottomSheet = true
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 50.dp, end = 50.dp),
                    ) {
                        Icon(Icons.Filled.Upload, contentDescription = "upload")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Upload")
                    }
                    Button(
                        onClick = {
                            navController.navigateAR(uiState.uri.toString())
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 50.dp, end = 50.dp),
                        enabled = uiState.uri != null,
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
                            modifier = Modifier.size(50.dp),
                        )
                        Spacer(modifier = spacerModifier)
                    } else if (uiState.filename.isNotBlank()) {
                        Text(
                            text = "Selected File: ${uiState.filename}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(modifier = spacerModifier)
                    }

                    Text(
                        text = "Artwork Description",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    TextField(
                        value = uiState.description,
                        onValueChange = viewModel::onDescriptionChange,
                        modifier = textFieldModifier.height(130.dp),
                    )
                    Spacer(modifier = spacerModifier)

                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.titleMedium,
                    )
//                    Button(
//                        modifier =
//                            Modifier
//                                .fillMaxWidth()
//                                .padding(start = 50.dp, end = 50.dp),
//                        onClick = { /* TODO */ }
//                    ) {
//                        Text(text = "Select Location")
//                    }
//                    Spacer(modifier = spacerModifier)
                    // Display the selected location
                    uiState.location?.let {
                        Text(
                            text = locationName, // TODO: Make this a map?
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(modifier = spacerModifier)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            onClick = {
                                viewModel.onPostArtwork(
                                    open = {
                                        navController.navigate(it)
                                    },
                                )
                            },
                            enabled = viewModel.isValid(),
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

/**
 * Display a clickable surface with an icon and text.
 *
 * @param text The text to display.
 * @param onClick The callback to run when the surface is clicked.
 * @param icon The icon to display.
 * @param iconDescription The description of the icon.
 * @param color The background color of the surface.
 * @param contentColor The content color of the surface.
 */
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
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = iconDescription,
            )
            Spacer(Modifier.width(10.dp))
            Text(text)
        }
    }
}

/**
 * Display a list of sample 3D models for demo and prototyping purposes.
 *
 * @param onDismissRequest The callback to run when the sheet is dismissed.
 * @param onSelect The callback to run when a model is selected.
 *
 * @see [ModalBottomSheet]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleModelList(
    onDismissRequest: () -> Unit,
    onSelect: (String, Uri) -> Unit,
) {
    val sampleSheetState = rememberModalBottomSheetState()
    val samples by remember {
        mutableStateOf(
            listOf(
                /**
                 * @reference Sceneview, "Assets," 2023. \[Online].
                 * Available: https://sceneview.github.io/assets/. [Accessed 10 October 2023].
                 */
                "https://sceneview.github.io/assets/models/Halloween.glb",
                "https://sceneview.github.io/assets/models/DamagedHelmet.glb",
                "https://sceneview.github.io/assets/models/GameBoy.glb",
                "https://sceneview.github.io/assets/models/Gumball.glb",
                "https://sceneview.github.io/assets/models/Hair.glb",
                "https://sceneview.github.io/assets/models/MetalRoughSpheres.glb",
                "https://sceneview.github.io/assets/models/Spoons.glb",
                "https://sceneview.github.io/assets/models/FiatPunto.glb",
                "https://sceneview.github.io/assets/models/ClearCoat.glb",
                "https://sceneview.github.io/assets/models/MaterialSuite.glb",
            ),
        )
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = sampleSheetState,
    ) {
        // Sheet content
        samples.forEach {
            val name = it.substringAfterLast("/")
            BottomSheetSurface(
                text = name,
                onClick = {
                    onDismissRequest()
                    onSelect(name, it.toUri())
                },
                icon = Icons.Filled.ViewInAr,
                iconDescription = "image",
            )
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateScreenPreview() {
    CreateScreen(navController = rememberNavController())
}

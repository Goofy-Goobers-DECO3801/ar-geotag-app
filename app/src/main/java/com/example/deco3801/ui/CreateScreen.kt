package com.example.deco3801.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.ScreenNames
import com.example.deco3801.ui.components.SnackbarManager
import com.example.deco3801.ui.components.TopBar
import com.example.deco3801.util.LocationUtil.getCurrentLocation
import com.example.deco3801.viewmodel.CreateViewModel
import com.example.deco3801.viewmodel.getFileName

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

    val uiState = viewModel.uiState
    LaunchedEffect(Unit) {
        viewModel.onLocationChange(getCurrentLocation(context))
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        try {
            val bytes =
                context.contentResolver.openInputStream(uri)?.use { it.buffered().readBytes() }
            viewModel.onFileChange(uri, uri.getFileName(context), bytes!!)
        } catch (e: Exception) {
            SnackbarManager.showError("Unable to upload file!")
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
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 35.dp,
                        end = 35.dp,
                        top = 40.dp
                    )
            ) {
                item {
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp)
                    ) {
                        Button(
                            onClick = {
                                // Open the file selection dialog
                                launcher.launch("*/*")// You can specify MIME types if needed
                            }
                        ) {
                            Text(text = "Upload from files")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                navController.navigate(ScreenNames.ARscreen.name)
                            },
                            enabled = uiState.uri != null
                        ) {
                            Text(text = "Preview in AR")
                        }
                    }

                    Spacer(modifier = spacerModifier)
                    // Display the selected file path
                    uiState.uri?.let {
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
                        modifier = Modifier.padding(start = 10.dp),
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
                        Spacer(Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateScreenPreview() {
    CreateScreen(navController = rememberNavController())
}

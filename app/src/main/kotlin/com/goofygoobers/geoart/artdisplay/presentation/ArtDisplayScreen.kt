package com.goofygoobers.geoart.artdisplay.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.goofygoobers.geoart.R
import com.goofygoobers.geoart.ui.components.ProgressbarState
import com.goofygoobers.geoart.ui.components.TopBar
import com.google.ar.core.Config
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

// AR screen was modelled after below sample
// Blizl, “Blizl/sceneview-android,” 21 September 2023. [Online]. Available: https://github.com/Blizl/sceneview-android/tree/blizl/ecommerce-compose-mvvm-app.

/*
 * The AR screen composable
 *
 * This is implemented using a ViewModel for the AR display which itself utilises the ViewState
 * data class to keep track of the state of the AR screen. A single instance of ArtDisplayViewModel
 * defined in the main activity of the app controls the logic for the AR screens.
 * Therefore, at each AR screen creation, the state of the ViewModel is reset.
 *
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ArtDisplayScreen(
    navigator: NavHostController,
    artAddress: String,
    artDisplayViewModel: ArtDisplayViewModel,
    artDisplayMode: PlacementMode = PlacementMode.BEST_AVAILABLE,
) {
    val nodes = remember { mutableStateListOf<ArNode>() }

    LaunchedEffect(Unit) {
        artDisplayViewModel.dispatchEvent(ArtDisplayUIEvent.FetchAsset(artAddress))
    }

    var sceneView by remember { mutableStateOf<ArSceneView?>(null) }
    val viewState by artDisplayViewModel.state.collectAsState()
    val uiAction by artDisplayViewModel.uiAction.collectAsState()
    var modelNode by remember { mutableStateOf<ArModelNode?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            ProgressbarState.resetProgressbar()
            onReturn(modelNode, viewState)
        }
    }

    when (uiAction) {
        is ArtDisplayUIAction.ShowModalPlaced -> {
            LaunchedEffect(Unit) {
                artDisplayViewModel.onConsumedUiAction()
            }
        }

        null -> {}
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navigator,
                canNavigateBack = true,
            ) {
                IconButton(onClick = { onRefresh(modelNode, viewState) }) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp),
                    )
                }
            }
        },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ARScene(
                modifier = Modifier.fillMaxSize(),
                nodes = nodes,
                planeRenderer = true,
                onCreate = { arSceneView ->
                    // Apply your configuration
                    sceneView = arSceneView
                },
                onSessionCreate = { session ->
                    // Configure the ARCore session if you need augmented faces, images, etc
                    session.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                    session.depthMode = Config.DepthMode.AUTOMATIC
                    session.instantPlacementEnabled = true
                },
                onFrame = { arFrame ->
                    // Update planes state to determine whether or not to UI message
                    // WARNING: DO NOT PASS ARSceneView/ARFrame TO ViewModel to avoid memory leaks
                    artDisplayViewModel.dispatchEvent(
                        ArtDisplayUIEvent.OnPlanesUpdated(arFrame.updatedPlanes),
                    )
                },
                onTap = {
                    // User tapped in the AR view
                    if (!viewState.modelPlaced) {
                        sceneView?.let {
                            modelNode = onUserTap(it, viewState, artDisplayMode)
                        }
                    }
                },
                onTrackingFailureChanged = {},
            )

            if (viewState.downloadingAsset) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                if (!viewState.modelPlaced) {
                    if (viewState.readyToPlaceModel) {
                        Box(
                            modifier =
                                Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp, bottom = 100.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(colorResource(id = R.color.translucent)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "Ready to place model!",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = Color.White,
                            )
                        }
                    } else {
                        Box(
                            modifier =
                                Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp, bottom = 100.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(colorResource(id = R.color.translucent)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "Tap now for instant placement or scan the area with your camera for best placement!",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = Color.White,
                            )
                        }
                    }
                }

                if (!viewState.modelPlaced) {
                    modelNode?.let {
                        sceneView?.planeRenderer?.isVisible = true
                        sceneView?.addChild(it)
                        sceneView?.selectedNode = it
                        artDisplayViewModel.dispatchEvent(ArtDisplayUIEvent.ModelPlaced)
                    }
                }
                if (viewState.modelPlaced && modelNode != null) {
                    Box(
                        modifier =
                            Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp, bottom = 100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(colorResource(id = R.color.translucent)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "Use one finger to move and two fingers to rotate the model!",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}

/*
 * Removes and destroys any placed model and resets the state for replacing
 */
private fun onRefresh(
    modelNode: ArModelNode?,
    viewState: ArtDisplayViewState?,
) {
    modelNode?.destroy()
    viewState?.modelPlaced = false
    viewState?.downloadingAsset = false
}

/*
 * Updates the state of view model and returns to previous screen
 */
private fun onReturn(
    modelNode: ArModelNode?,
    viewState: ArtDisplayViewState?,
) {
    onRefresh(modelNode, viewState)
    viewState?.readyToPlaceModel = false
    viewState?.modelAsset = null
}

/*
 * Return the AR model to be placed
 */
fun onUserTap(
    sceneView: ArSceneView,
    viewState: ArtDisplayViewState,
    artDisplayMode: PlacementMode,
): ArModelNode {
    // Try to avoid placing 3d models in ViewModel to avoid memory leaks since ARNodes contains context
    ProgressbarState.showIndeterminateProgressbar()
    val tmp =
        ArModelNode(
            sceneView.engine,
            artDisplayMode,
        ).apply {
            viewState.modelAsset?.let {
                Log.d("ARMODEL", it)
                loadModelGlbAsync(
                    glbFileLocation = it,
                    scaleToUnits = 1f,
                    centerOrigin = Position(-0.5f),
                    onLoaded = {
                        ProgressbarState.resetProgressbar()
                    },
                )
            }
        }
    return tmp
}

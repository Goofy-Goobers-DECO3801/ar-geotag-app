package com.example.deco3801.artdisplay.presentation


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import com.example.deco3801.R
import com.google.ar.core.Config
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

@Composable
fun ArtDisplayScreen(
    artAddress: String,
    artDisplayViewModel: ArtDisplayViewModel,
    artDisplayMode: PlacementMode = PlacementMode.BEST_AVAILABLE
) {
    val nodes = remember { mutableStateListOf<ArNode>() }

    LaunchedEffect(Unit) {
        artDisplayViewModel.dispatchEvent(ArtDisplayUIEvent.FetchAsset(artAddress))
    }

    val context = LocalContext.current
    var sceneView by remember { mutableStateOf<ArSceneView?>(null) }
    val viewState by artDisplayViewModel.state.collectAsState()
    val uiAction by artDisplayViewModel.uiAction.collectAsState()
    var modelNode by remember { mutableStateOf<ArModelNode?>(null) }

    when (uiAction) {
        is ArtDisplayUIAction.ShowModalPlaced -> {
            LaunchedEffect(Unit) {
                Toast.makeText(context, "Placed model!", Toast.LENGTH_SHORT).show()
                artDisplayViewModel.onConsumedUiAction()
            }
        }
        null -> {}
    }

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
                artDisplayViewModel.resetStates()

            },
            onFrame = { arFrame ->
                // Update planes state to determine whether or not to UI message
                // WARNING: DO NOT PASS ARSceneView/ARFrame TO ViewModel to avoid memory leaks
                artDisplayViewModel.dispatchEvent(ArtDisplayUIEvent.OnPlanesUpdated(arFrame.updatedPlanes))
            },
            onTap = { hitResult ->
                // User tapped in the AR view
                sceneView?.let {
                    modelNode = onUserTap(it, viewState, artDisplayMode)

                }
            },
            onTrackingFailureChanged = { trackingFailureReason ->
                """
                 You can also show a tracking failure message if needed
                    
                 virtualTryOnViewModel.dispatchEvent(
                      VirtualTryOnUIEvent.OnTrackingFailure(
                            trackingFailureReason
                    )
                 )
                """
            }
        )


        FloatingActionButton(
            onClick = { onClick(modelNode, viewState) },
            shape = CircleShape,
        ) {
            Icon(Icons.Filled.Refresh, "Large floating action button")
        }


        if (viewState.downloadingAsset) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            if (!viewState.modelPlaced) {
                if (viewState.readyToPlaceModel) {

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp, bottom = 100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colorResource(id = R.color.translucent)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Ready to place model!",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp, bottom = 100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colorResource(id = R.color.translucent)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Move your phone to place model",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = Color.White
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
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp, bottom = 100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colorResource(id = R.color.translucent)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Use one finger to move and two fingers to rotate the model!",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }


        }
    }
}
fun onClick(modelNode: ArModelNode?, viewState: ArtDisplayViewState?) {
    modelNode?.destroy()
    viewState?.modelPlaced = false
}



fun onUserTap(sceneView: ArSceneView, viewState: ArtDisplayViewState, artDisplayMode: PlacementMode): ArModelNode {
    // Try to avoid placing 3d models in ViewModel to avoid memory leaks since ARNodes contains context
    return ArModelNode(
        sceneView.engine, artDisplayMode
    ).apply {
        viewState.modelAsset?.let {
            Log.d("ARMODEL", it)
            loadModelGlbAsync(
                glbFileLocation = it,
//                glbFileLocation = "models/bear.glb",
                scaleToUnits = 1f,
                centerOrigin = Position(-0.5f)
            )
        }
    }
}
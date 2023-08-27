package com.example.deco3801.ui

import com.example.deco3801.ui.components.NavBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.isGone
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Config
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

@Composable
fun CreateScreen() {
    val nodes = remember { mutableStateListOf<ArNode>() }
    lateinit var modelNode: ArModelNode

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                arSceneView.apply {
                    this.lightEstimationMode = Config.LightEstimationMode.DISABLED
                }

                // add AR model

//                modelNode = ArModelNode(arSceneView.engine, PlacementMode.INSTANT).apply {
//                    loadModelGlbAsync(
//                        glbFileLocation = "models/bear.glb",
//                        scaleToUnits = 1f,
//                        centerOrigin = Position(-0.5f)
//
//                    )
//                    {
//                        arSceneView.planeRenderer.isVisible = true
//                        val materialInstance = it.materialInstances[0]
//                    }
//                    onAnchorChanged = {
////                        placeButton.isGone = it != null
//                    }
//
//                }
//                nodes.add(modelNode)
//                arSceneView.addChild(modelNode)

            },
            onSessionCreate = { session ->
                session.display
            },
            onFrame = { arFrame ->
                // Retrieve ARCore frame update

            },
            onTap = { hitResult ->
                // User tapped in the AR view
            }
        )
        FloatingActionButton(

            onClick = {
                //OnClick Method
            },
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = "Add FAB",
                tint = Color.White,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateScreenPreview() {
    CreateScreen()
}

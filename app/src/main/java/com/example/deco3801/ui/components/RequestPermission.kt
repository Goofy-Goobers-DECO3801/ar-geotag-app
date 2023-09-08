package com.example.deco3801.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Permission requester
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissions(
    permissions: List<String>,
    title: String,
    description: String,
) {
    val openDialog = remember { mutableStateOf(true) }
    val permissionsState = rememberMultiplePermissionsState(permissions = permissions)
    if (openDialog.value && permissionsState.shouldShowRationale) {
        AlertDialog(onDismissRequest = { openDialog.value = false }, title = {
            Text(text = title)
        }, text = {
            Text(text = description)
        }, confirmButton = {
            TextButton(onClick = {
                permissionsState.launchMultiplePermissionRequest()
                openDialog.value = false
            }) {
                Text("Continue")
            }
        }, dismissButton = {
            TextButton(onClick = {
                openDialog.value = false
            }) {
                Text("Dismiss")
            }
        })
    } else {
        LaunchedEffect(Unit) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }
}
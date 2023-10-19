/**
 * This file contains the code for the permission requester component.
 */
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
 * Composable function for requesting permissions from the user and prompts them
 * with the rationale behind the permission request.
 *
 * @param permissions The list of permissions to request.
 * @param title The title of the dialog.
 * @param description The description of the dialog.
 * @param onRevoked The callback to run when the user revokes the permission.
 * @param onGranted The callback to run when the user grants the permission.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissions(
    permissions: List<String>,
    title: String,
    description: String,
    onRevoked: @Composable () -> Unit = {},
    onGranted: @Composable () -> Unit,
) {
    val openDialog = remember { mutableStateOf(true) }
    val permissionsState = rememberMultiplePermissionsState(permissions = permissions)
    if (permissionsState.allPermissionsGranted) {
        onGranted()
    } else if (permissionsState.revokedPermissions.any { it.permission in permissions }) {
        onRevoked()
    }

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

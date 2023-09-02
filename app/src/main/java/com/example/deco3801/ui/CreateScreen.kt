package com.example.deco3801.ui

import android.net.Uri
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deco3801.model.Art
import com.example.deco3801.ui.theme.MyColors
import com.example.deco3801.util.LocationUtil.getCurrentLocation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen() {
    val textModifier: Modifier = Modifier
    val textFieldModifier: Modifier = Modifier.fillMaxWidth()
    val spacerModifier: Modifier = Modifier.height(10.dp)

    var art by remember { mutableStateOf(Art()) }

    getCurrentLocation(LocalContext.current) { lat, lng ->
        art = art.copy(location = GeoPoint(lat, lng))
    }

    var file by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            file = it
        }
    }

    var showAlert by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("") }
    var alertDescription by remember { mutableStateOf("") }

    val isNameValid = art.title.isNotEmpty()
    val isDescriptionValid = art.description.isNotEmpty()
    val isLocationValid = art.location != null
    val isFileValid = file != null
    val isAllFieldsValid = isNameValid && isDescriptionValid && isLocationValid && isFileValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 35.dp,
                end = 35.dp,
                top = 40.dp
            )
    ) {
        Text(
            text = "Upload an Artwork",
            modifier = textModifier,
            style = MaterialTheme.typography.titleLarge,
            color = MyColors.DarkOrange
        )
        Spacer(modifier = spacerModifier)


        Text(
            text = "Title",
            modifier = textModifier,
            style = MaterialTheme.typography.titleMedium
        )
        TextField(
            value = art.title,
            onValueChange = { newTitle -> art = art.copy(title = newTitle) },
            modifier = textFieldModifier,
        )
        Spacer(modifier = spacerModifier)


        Text(
            text = "Upload Artwork",
            style = MaterialTheme.typography.titleMedium
        )
        Button(
            onClick = {
                // Open the file selection dialog
                launcher.launch("*/*") // You can specify MIME types if needed
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Upload from files")
        }
        Spacer(modifier = spacerModifier)
        // Display the selected file path
        file?.let { filePath ->
            Text(
                text = "Selected File: $filePath",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = spacerModifier)
        }

        Text(
            text = "Artwork Description",
            style = MaterialTheme.typography.titleMedium
        )
        TextField(
            value = art.description,
            onValueChange = { newDescription -> art = art.copy(description = newDescription) },
            modifier = textFieldModifier.height(130.dp)
        )
        Spacer(modifier = spacerModifier)


        Text(
            text = "Select Location",
            style = MaterialTheme.typography.titleMedium
        )
        Button(onClick = { /* TODO */ }) {
            Text(text = "Select Location")
        }
        Spacer(modifier = spacerModifier)
        // Display the selected location
        art.location?.let {
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
                    if (!isAllFieldsValid) {
                        return@Button
                    }

                    val db = FirebaseFirestore.getInstance()
                    val storage = Firebase.storage.reference
                    val user = Firebase.auth.currentUser
                    val uid = user?.uid ?: "TODO"
                    art.storageRef = "$uid/${file!!.lastPathSegment}"
                    storage.child(art.storageRef).putFile(file!!)
                        .addOnSuccessListener {
                            db.collection("art").add(art)
                                .addOnSuccessListener {
                                    art = Art() // Clear art
                                    file = null
                                    showAlert = true
                                    alertTitle = "Upload Successful"
                                    alertDescription =
                                        "Your artwork has been successfully uploaded."
                                }
                                .addOnFailureListener {
                                    showAlert = true
                                    alertTitle = "Upload Failed"
                                    alertDescription = it.toString()
                                }
                        }
                        .addOnFailureListener {
                            showAlert = true
                            alertTitle = "Upload Failed"
                            alertDescription = it.toString()
                        }
                },
                enabled = isAllFieldsValid
            ) {
                Text(text = "Post Artwork")
            }
            // Show an alert dialog if showAlert is true
            if (showAlert) {
                AlertDialog(
                    onDismissRequest = { showAlert = false },
                    title = {
                        Text(text = alertTitle)
                    },
                    text = {
                        Text(text = alertDescription)
                    },
                    confirmButton = {
                        Button(onClick = { showAlert = false }) {
                            Text(text = "OK")
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateScreenPreview() {
    CreateScreen()
}

/**
 * ViewModel for the Create Screen
 */
package com.example.deco3801.viewmodel

import android.content.Context
import android.location.Location
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.chaquo.python.PyException
import com.chaquo.python.Python
import com.example.deco3801.ScreenNames
import com.example.deco3801.data.repository.ArtRepository
import com.example.deco3801.ui.components.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * The state of the create screen
 */
data class CreateUiState(
    var title: String = "",
    var description: String = "",
    var location: Location? = null,
    var uri: Uri? = null,
    var filename: String = "",
    var imageBytes: ByteArray? = null

)

/**
 * Get the filename from a uri
 *
 * @param context The context to use
 *
 * @reference
 * "Retrieving File Information," Android Developers, 27 October 2021. \[Online].
 * Available: https://developer.android.com/training/secure-file-sharing/retrieve-info.
 * [Accessed 15 September 2023].
 */
fun Uri.getFileName(context: Context): String {
    var result = ""
    if (this.scheme == "content") {
        val cursor = context.contentResolver.query(this, null, null, null, null)
        cursor.use {
            if (it != null && it.moveToFirst()) {
                result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
    }

    // If the filename is not found, use the path
    if (result == "") {
        result = this.path.toString()
        val cut = result.lastIndexOf('/')
        if (cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    return result
}

/**
 * Contains the logic and state for the CreateScreen
 *
 * @constructor Create a Create view model with dependency injection
 * @property artRepo The art repository to use, injected by Hilt
 */
@HiltViewModel
class CreateViewModel @Inject constructor(
    private val artRepo: ArtRepository,
) : AppViewModel() {
    var uiState by mutableStateOf(CreateUiState())
        private set

    /**
     * Update the title in the state to [newValue]
     */
    fun onTitleChange(newValue: String) {
        uiState = uiState.copy(title = newValue)
    }

    /**
     * Update the description in the state to [newValue]
     */
    fun onDescriptionChange(newValue: String) {
        uiState = uiState.copy(description = newValue)
    }

    /**
     * Update the location in the state to [newValue]
     */
    fun onLocationChange(newValue: Location?) {
        uiState = uiState.copy(location = newValue)
    }

    /**
     * Update the uri in the state to [uri] and the filename to [filename]
     */
    fun onSelectFile(filename: String, uri: Uri) {
        uiState = uiState.copy(
            uri = uri,
            filename = filename,
            imageBytes = null,
        )
    }

    /**
     * Update the image bytes in the state to [inBytes] and convert the image to a 3d model.
     * The conversion happens by calling the python script to convert the 2d image byte stream into
     * a 3d model byte stream, which we then write to disk.
     */
    fun onSelectImage(filename: String, inBytes: ByteArray) {

        val py = Python.getInstance()
        val module = py.getModule("jpeg_glb_template_converter")
        try {
            val glbPy = module.callAttr("convert", inBytes)
            val outBytes = glbPy.toJava(ByteArray::class.java)

            val tempFile = File.createTempFile(filename, ".glb")
            FileOutputStream(tempFile).use { outputStream ->
                outputStream.write(outBytes)
            }

            uiState = uiState.copy(
                uri = tempFile.toUri(),
                filename = tempFile.name,
                imageBytes = inBytes,
            )
        } catch (e: PyException) {
            SnackbarManager.showError("Failed to convert image to 3d")
            Log.e("CREATE", e.stackTraceToString())
        }

    }

    /**
     * Check if the input is valid
     *
     * @return True if the input is valid, false otherwise
     */
    fun isValid(): Boolean {
        return uiState.title.isNotEmpty()
                && uiState.description.isNotEmpty()
                && uiState.location != null
                && uiState.uri != null
    }

    /**
     * Post the artwork with the current state and [open] a new screen
     */
    fun onPostArtwork(open: (String) -> Unit) {
        if (!isValid()) {
            SnackbarManager.showError("Input is invalid.")
            return
        }
        val tmp = uiState
        uiState = CreateUiState()

        launchCatching(onFailure = { uiState = tmp }) {
            artRepo.createArt(
                tmp.title,
                tmp.description,
                tmp.location!!,
                tmp.uri!!,
                tmp.filename,
            )
            SnackbarManager.showMessage("Artwork Posted!")
            open(ScreenNames.Home.name)
        }
    }
}

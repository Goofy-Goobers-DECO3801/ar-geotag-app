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

data class CreateUiState(
    var title: String = "",
    var description: String = "",
    var location: Location? = null,
    var uri: Uri? = null,
    var filename: String = "",
    var imageBytes: ByteArray? = null

)

/**
 * @reference
 * "Retrieving File Information," Android Developers, Oct. 27, 2021.
 * https://developer.android.com/training/secure-file-sharing/retrieve-info
 * (accessed Oct. 16, 2023).
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
    if (result == "") {
        result = this.path.toString()
        val cut = result.lastIndexOf('/')
        if (cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    return result
}

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val artRepo: ArtRepository,
) : AppViewModel() {
    var uiState by mutableStateOf(CreateUiState())
        private set

    fun onTitleChange(newValue: String) {
        uiState = uiState.copy(title = newValue)
    }

    fun onDescriptionChange(newValue: String) {
        uiState = uiState.copy(description = newValue)
    }

    fun onLocationChange(newValue: Location?) {
        uiState = uiState.copy(location = newValue)
    }

    fun onSelectFile(filename: String, uri: Uri) {
        uiState = uiState.copy(
            uri = uri,
            filename = filename,
            imageBytes = null,
        )
    }

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

    fun isValid(): Boolean {
        return uiState.title.isNotEmpty()
                && uiState.description.isNotEmpty()
                && uiState.location != null
                && uiState.uri != null
    }

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

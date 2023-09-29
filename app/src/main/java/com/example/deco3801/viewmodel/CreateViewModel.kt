package com.example.deco3801.viewmodel

import android.content.Context
import android.location.Location
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.deco3801.ScreenNames
import com.example.deco3801.data.repository.ArtRepository
import com.example.deco3801.ui.components.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.chaquo.python.PyException
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import okio.ByteString.Companion.toByteString
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter

data class CreateUiState(
    var title: String = "",
    var description: String = "",
    var location: Location? = null,
    var uri: Uri? = null,
    var filename: String = "",
)

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

    private var fileBytes: ByteArray? = null
    public var filePath: String = ""

    fun onTitleChange(newValue: String) {
        uiState = uiState.copy(title = newValue)
    }

    fun onDescriptionChange(newValue: String) {
        uiState = uiState.copy(description = newValue)
    }

    fun onLocationChange(newValue: Location?) {
        uiState = uiState.copy(location = newValue)
    }

    fun onFileChange(newUri: Uri, newFilename: String, bytes: ByteArray) {
        uiState = uiState.copy(uri = newUri, filename = newFilename)
        fileBytes = bytes

        val py = Python.getInstance()
        val module = py.getModule("convert")

        try {
            val glbPy = module.callAttr("convert", fileBytes!!.toByteString())
            fileBytes = glbPy.toJava(ByteArray::class.java)

            val tempFile = File.createTempFile("user_image", ".glb")
            FileOutputStream(tempFile).use { outputStream ->
                outputStream.write(fileBytes)
            }

            filePath = tempFile.absolutePath
        } catch (e: PyException) {
        }
    }

    fun isValid(): Boolean {
        return uiState.title.isNotEmpty()
                && uiState.description.isNotEmpty()
                && uiState.location != null
                && uiState.uri != null
                && fileBytes != null
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
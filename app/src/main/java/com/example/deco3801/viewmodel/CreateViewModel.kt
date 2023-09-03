package com.example.deco3801.viewmodel

import android.location.Location
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deco3801.data.repository.ArtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateUiState(
    var title: String = "",
    var description: String = "",
    var location: Location? = null,
    var uri: Uri? = null,
)

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val artRepo: ArtRepository,
) : ViewModel() {
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

    fun onFileChange(newValue: Uri) {
        uiState = uiState.copy(uri = newValue)
    }

    fun isValid(): Boolean {
        return uiState.title.isNotEmpty() && uiState.description.isNotEmpty() && uiState.location != null && uiState.uri != null
    }

    fun onPostArtwork(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (!isValid()) {
            onFailure("Input is invalid.")
            return
        }
        val tmp = uiState
        uiState = CreateUiState()
        viewModelScope.launch {
            try {
                artRepo.create(tmp.title, tmp.description, tmp.location!!, tmp.uri!!)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e.message ?: "Upload Failed.")
                uiState = tmp
            }
        }
    }
}
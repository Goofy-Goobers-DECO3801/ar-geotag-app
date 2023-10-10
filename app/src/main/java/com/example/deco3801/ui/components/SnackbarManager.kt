package com.example.deco3801.ui.components

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SnackbarMessage(
    val message: String,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val _trigger: Boolean = false,
)

object SnackbarManager {
    private val _messages: MutableStateFlow<SnackbarMessage?> = MutableStateFlow(null)
    val messages: StateFlow<SnackbarMessage?>
        get() = _messages.asStateFlow()

    fun showMessage(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ) {
        val trigger = !(_messages.value?._trigger ?: true)
        _messages.value = SnackbarMessage(message, duration, trigger)
    }

    fun showError(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Long,
    ) {
        return showMessage(message, duration)
    }

    fun showError(
        e: Exception,
        duration: SnackbarDuration = SnackbarDuration.Long,
    ) {
        return showError(e.message ?: "Failed!", duration)
    }
}

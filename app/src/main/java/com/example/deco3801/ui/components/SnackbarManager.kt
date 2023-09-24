package com.example.deco3801.ui.components

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    fun showMessage(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
        val trigger = !(_messages.value?._trigger ?: true)
        _messages.value = SnackbarMessage(message, duration, trigger)
    }

    fun showError(message: String, duration: SnackbarDuration = SnackbarDuration.Indefinite) {
        val trigger = !(_messages.value?._trigger ?: true)
        _messages.value = SnackbarMessage(message, duration, trigger)
    }

    fun showError(e: Exception, duration: SnackbarDuration = SnackbarDuration.Indefinite) {
        val trigger = !(_messages.value?._trigger ?: true)
        _messages.value = SnackbarMessage(e.message ?: "Failed!", duration, trigger)
    }
}
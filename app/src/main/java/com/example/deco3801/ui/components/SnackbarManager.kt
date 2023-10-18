/**
 * This file contains the SnackbarManager object, which is used to display the snackbar messages.
 */
package com.example.deco3801.ui.components

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Data class representing the state of the Snackbar.
 */
data class SnackbarMessage(
    val message: String,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val _trigger: Boolean = false,
)

/**
 * Object containing the state of the Snackbar.
 */
object SnackbarManager {
    private val _messages: MutableStateFlow<SnackbarMessage?> = MutableStateFlow(null)
    val messages: StateFlow<SnackbarMessage?>
        get() = _messages.asStateFlow()

    /**
     * Show a snackbar with the given [message] and [duration].
     * The default [duration] is [SnackbarDuration.Short].
     */
    fun showMessage(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ) {
        val trigger = !(_messages.value?._trigger ?: true)
        _messages.value = SnackbarMessage(message, duration, trigger)
    }

    /**
     * Show a snackbar with the given [message] and [duration].
     * The default [duration] is [SnackbarDuration.Long].
     */
    fun showError(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Long,
    ) {
        return showMessage(message, duration)
    }

    /**
     * Show a snackbar with the given [exception] and [duration].
     * The default [duration] is [SnackbarDuration.Long].
     * The message of the exception is used as the message or "Failed!" if the message is null.
     */
    fun showError(
        exception: Exception,
        duration: SnackbarDuration = SnackbarDuration.Long,
    ) {
        return showError(exception.message ?: "Failed!", duration)
    }
}

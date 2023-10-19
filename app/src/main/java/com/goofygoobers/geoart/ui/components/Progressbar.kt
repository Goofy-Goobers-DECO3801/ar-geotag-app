/**
 * File containing the Progressbar component and state.
 */
package com.goofygoobers.geoart.ui.components

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Data class representing the state of the Progressbar.
 */
data class ProgressbarData(
    val visible: Boolean = false,
    val progress: Float = 0f,
)

/**
 * Object containing the state of the Progressbar.
 */
object ProgressbarState {
    private val _state = MutableStateFlow(ProgressbarData())
    val state: StateFlow<ProgressbarData> = _state

    /**
     * Show the progressbar with an indeterminate progress.
     */
    fun showIndeterminateProgressbar() {
        _state.value = _state.value.copy(visible = true, progress = -1f)
    }

    /**
     * Show the progressbar with the existing progress.
     */
    fun showProgressbar() {
        _state.value = _state.value.copy(visible = true)
    }

    /**
     * Hide the progressbar.
     */
    fun hideProgressbar() {
        _state.value = _state.value.copy(visible = false)
    }

    /**
     * Reset the progressbar to 0 and hide it.
     */
    fun resetProgressbar() {
        _state.value = ProgressbarData()
    }

    /**
     * Update the progressbar with a new progress and show it.
     */
    fun updateProgressbar(progress: Float) {
        _state.value = _state.value.copy(progress = progress, visible = true)
    }
}

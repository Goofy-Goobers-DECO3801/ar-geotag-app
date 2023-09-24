package com.example.deco3801.ui.components

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ProgressbarData(
    val visible: Boolean = false,
    val progress: Float = 0f
)

object ProgressbarState {
    private val _state = MutableStateFlow(ProgressbarData())
    val state: StateFlow<ProgressbarData> = _state

    fun showIndeterminateProgressbar() {
        _state.value = _state.value.copy(visible = true, progress = -1f)
    }

    fun showProgressbar() {
        _state.value = _state.value.copy(visible = true)
    }

    fun hideProgressbar() {
        _state.value = _state.value.copy(visible = false)
    }

    fun resetProgressbar() {
        _state.value = ProgressbarData()
    }

    fun updateProgressbar(progress: Float) {
        _state.value = _state.value.copy(progress = progress, visible = true)
    }
}
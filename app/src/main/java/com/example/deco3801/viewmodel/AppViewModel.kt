package com.example.deco3801.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deco3801.ui.components.ProgressbarState
import com.example.deco3801.ui.components.SnackbarManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class AppViewModel : ViewModel() {
    fun launchCatching(onFailure: () -> Unit = {}, block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                SnackbarManager.showError(e)
                onFailure()
            } finally {
                ProgressbarState.resetProgressbar()
            }

        }
}
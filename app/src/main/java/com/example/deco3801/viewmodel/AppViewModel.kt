package com.example.deco3801.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deco3801.ui.components.ProgressbarState
import com.example.deco3801.ui.components.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class AppViewModel : ViewModel() {
    fun launchCatching(onFailure: () -> Unit = {}, block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                Log.e("ERROR", e.toString())
                SnackbarManager.showError(e)
                onFailure()
            } finally {
                ProgressbarState.resetProgressbar()
            }

        }
}
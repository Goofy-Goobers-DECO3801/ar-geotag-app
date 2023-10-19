/**
 * ViewModel for the app
 */
package com.goofygoobers.geoart.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goofygoobers.geoart.ui.components.ProgressbarState
import com.goofygoobers.geoart.ui.components.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * ViewModel for the app
 *
 * @constructor Create empty App view model
 *
 * @see [ViewModel]
 */
open class AppViewModel : ViewModel() {
    /**
     * Launch a coroutine that will catch any exceptions thrown and display a snackbar with the error message
     *
     * @param onFailure The function to run if an exception is thrown
     * @param showErrorMsg Whether to show the error message in a snackbar
     * @param block The coroutine block to run
     */
    fun launchCatching(
        onFailure: () -> Unit = {},
        showErrorMsg: Boolean = true,
        block: suspend CoroutineScope.() -> Unit,
    ) = viewModelScope.launch {
        try {
            block()
        } catch (e: Exception) {
            Log.e("ERROR", e.toString())
            if (showErrorMsg) {
                SnackbarManager.showError(e)
            }
            onFailure()
        } finally {
            ProgressbarState.resetProgressbar()
        }
    }
}

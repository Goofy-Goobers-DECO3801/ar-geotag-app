/**
 * App state data and functions
 */
package com.goofygoobers.geoart

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.goofygoobers.geoart.ui.components.ProgressbarState
import com.goofygoobers.geoart.ui.components.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

/**
 * The global app state.
 * This should not be used directly, instead use [rememberAppState].
 *
 * @see [rememberAppState]
 */
class AppState(
    val snackbarManager: SnackbarManager,
    val snackbarHostState: SnackbarHostState,
    val scope: CoroutineScope,
    val navController: NavHostController,
    val progressbarState: ProgressbarState,
) {
    init {
        // Listens for snackbar messages and displays them.
        scope.launch {
            snackbarManager.messages.filterNotNull().collect {
                snackbarHostState.showSnackbar(
                    message = it.message,
                    duration = it.duration,
                    withDismissAction = true,
                )
            }
        }
    }
}

/**
 * Remember the global app state.
 *
 * @param snackbarManager The [SnackbarManager] to use.
 * @param snackbarHostState The [SnackbarHostState] to use.
 * @param scope The [CoroutineScope] to use.
 * @param navController The [NavHostController] to use.
 * @param progressbarState The [ProgressbarState] to use.
 *
 * @return The [AppState].
 */
@Composable
fun rememberAppState(
    snackbarManager: SnackbarManager = remember { SnackbarManager },
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    progressbarState: ProgressbarState = remember { ProgressbarState },
) = remember(snackbarManager, snackbarHostState, navController, scope, progressbarState) {
    AppState(
        snackbarManager = SnackbarManager,
        snackbarHostState = snackbarHostState,
        navController = navController,
        scope = scope,
        progressbarState = progressbarState,
    )
}

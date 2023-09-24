package com.example.deco3801

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.ui.components.ProgressbarState
import com.example.deco3801.ui.components.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class AppState(
    val snackbarManager: SnackbarManager,
    val snackbarHostState: SnackbarHostState,
    val scope: CoroutineScope,
    val navController: NavHostController,
    val progressbarState: ProgressbarState,
) {
    init {
        scope.launch {
            snackbarManager.messages.filterNotNull().collect {
                snackbarHostState.showSnackbar(message = it.message, duration = it.duration, withDismissAction = true)
            }
        }
    }

}

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


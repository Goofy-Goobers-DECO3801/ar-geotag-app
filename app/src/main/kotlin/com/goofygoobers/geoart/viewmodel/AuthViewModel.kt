/**
 * This file is used to handle the authentication of the user.
 */
package com.goofygoobers.geoart.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.goofygoobers.geoart.ScreenNames
import com.goofygoobers.geoart.data.repository.UserRepository
import com.goofygoobers.geoart.ui.components.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * The state of the auth screen
 */
data class AuthUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
)

/**
 * Contains the logic and state for the auth screen
 *
 * @constructor Create an Auth view model with dependency injection
 * @property userRepo The user repository to use, injected by Hilt
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepo: UserRepository,
) : AppViewModel() {
    var uiState by mutableStateOf(AuthUiState())
        private set

    private val username
        get() = uiState.username
    private val email
        get() = uiState.email
    private val password
        get() = uiState.password

    /**
     * Update the username in the state to [newValue]
     */
    fun onUsernameChange(newValue: String) {
        uiState = uiState.copy(username = newValue)
    }

    /**
     * Update the email in the state to [newValue]
     */
    fun onEmailChange(newValue: String) {
        uiState = uiState.copy(email = newValue)
    }

    /**
     * Update the password in the state to [newValue]
     */
    fun onPasswordChange(newValue: String) {
        uiState = uiState.copy(password = newValue)
    }

    /**
     * Login the user with the email and password in the state and [open] a new screen
     */
    fun onLoginClicked(open: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            SnackbarManager.showError("Email or password is blank!")
            return
        }

        launchCatching {
            userRepo.loginUser(email, password)
            open(ScreenNames.Home.name)
        }
    }

    /**
     * Sign up the user with the email, password and username in the state and [open] a new screen
     */
    fun onSignUpClicked(open: (String) -> Unit) {
        if (email.isBlank() || password.isBlank() || username.isBlank()) {
            SnackbarManager.showError("Username, email or password is blank!")
            return
        }
        launchCatching {
            userRepo.createUser(username, email, password)
            open(ScreenNames.Home.name)
        }
    }
}

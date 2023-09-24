package com.example.deco3801.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.deco3801.ScreenNames
import com.example.deco3801.data.repository.UserRepository
import com.example.deco3801.ui.components.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class AuthUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
)


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


    fun onUsernameChange(newValue: String) {
        uiState = uiState.copy(username = newValue)
    }

    fun onEmailChange(newValue: String) {
        uiState = uiState.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState = uiState.copy(password = newValue)
    }

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
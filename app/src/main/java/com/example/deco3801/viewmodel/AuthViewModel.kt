package com.example.deco3801.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deco3801.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AuthUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
)


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepo: UserRepository,
) : ViewModel() {
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

    fun onLoginClicked(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            onFailure("Email or password is blank!")
            return
        }
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e.message ?: "Login Failed.")
            }
        }
    }

    fun onSignUpClicked(onSuccess: () -> Unit, onFail: (String) -> Unit) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            onFail("Username, email or password is blank!")
            return
        }
        viewModelScope.launch {
            try {
                userRepo.create(username, email, password)
                onSuccess()
            } catch (e: Exception) {
                onFail(e.message ?: "Signup Failed!")
            }
        }
    }
}
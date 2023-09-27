package com.example.deco3801.viewmodel

import com.example.deco3801.data.model.User
import com.example.deco3801.data.repository.UserRepository
import com.example.deco3801.ui.components.ProgressbarState
import com.example.deco3801.ui.components.SnackbarManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class SettingsUiState(
    var isPrivate: Boolean = false,
    val email: String = "",
    val oldPassword: String = "",
    val newPassword: String = "",

    )


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepo: UserRepository,
) : AppViewModel() {

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        launchCatching {
            _user.value = userRepo.getUser(Firebase.auth.uid!!)!!
            _uiState.value = _uiState.value.copy(isPrivate = _user.value.isPrivate)
        }
    }

    fun updatePasswordEnabled() =
        _uiState.value.oldPassword.isNotBlank() && _uiState.value.newPassword.isNotBlank()

    fun updatePassword() {
        if (!updatePasswordEnabled()) {
            SnackbarManager.showError("Old password or new password is blank!")
            return
        }
        launchCatching {
            ProgressbarState.showIndeterminateProgressbar()
            userRepo.updatePassword(_uiState.value.oldPassword, _uiState.value.newPassword)
            SnackbarManager.showMessage("Password updated!")
            _uiState.value = _uiState.value.copy(oldPassword = "", newPassword = "")
        }
    }

    fun onOldPasswordChange(newValue: String) {
        _uiState.value = _uiState.value.copy(oldPassword = newValue)
    }

    fun onNewPasswordChange(newValue: String) {
        _uiState.value = _uiState.value.copy(newPassword = newValue)
    }

    fun onPrivate(value: Boolean) {
        launchCatching {
            userRepo.updateIsPrivate(value)
            _uiState.value = _uiState.value.copy(isPrivate = value)
        }

    }

}
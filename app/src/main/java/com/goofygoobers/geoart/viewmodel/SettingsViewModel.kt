/**
 * Settings view model
 */
package com.goofygoobers.geoart.viewmodel

import com.goofygoobers.geoart.data.model.User
import com.goofygoobers.geoart.data.repository.UserRepository
import com.goofygoobers.geoart.ui.components.ProgressbarState
import com.goofygoobers.geoart.ui.components.SnackbarManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * The state of the settings screen
 */
data class SettingsUiState(
    var isPrivate: Boolean = false,
    val email: String = "",
    val oldPassword: String = "",
    val newPassword: String = "",
)

/**
 * Contains the logic and state for the settings screen
 *
 * @constructor Create a Settings view model with dependency injection
 * @property userRepo The user repository to use, injected by Hilt
 */
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

    /**
     * Checks if the update password button should be enabled
     */
    fun updatePasswordEnabled() =
        _uiState.value.oldPassword.isNotBlank() && _uiState.value.newPassword.isNotBlank()

    /**
     * Update the user's password
     */
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

    /**
     * Change the state of the old password to [newValue]
     */
    fun onOldPasswordChange(newValue: String) {
        _uiState.value = _uiState.value.copy(oldPassword = newValue)
    }

    /**
     * Change the state of the new password to [newValue]
     */
    fun onNewPasswordChange(newValue: String) {
        _uiState.value = _uiState.value.copy(newPassword = newValue)
    }

    /**
     * Change the users privacy setting to [value]
     */
    fun onPrivate(value: Boolean) {
        launchCatching {
            userRepo.updateIsPrivate(value)
            _uiState.value = _uiState.value.copy(isPrivate = value)
        }
    }
}

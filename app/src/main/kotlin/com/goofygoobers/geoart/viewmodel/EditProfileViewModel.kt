/**
 * ViewModel for the EditProfileScreen.
 */
package com.goofygoobers.geoart.viewmodel

import android.net.Uri
import com.goofygoobers.geoart.data.model.User
import com.goofygoobers.geoart.data.repository.UserRepository
import com.goofygoobers.geoart.ui.components.SnackbarManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Contains the logic and state for the EditProfileScreen
 */
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val auth: FirebaseAuth,
) : AppViewModel() {
    private var _loading = false

    private val _newUser = MutableStateFlow(User())
    val newUser: StateFlow<User> = _newUser

    private val _oldUser = MutableStateFlow(User())

    init {
        launchCatching {
            _oldUser.value = userRepo.getUser(auth.uid!!)!!
            _newUser.value = _oldUser.value
        }
    }

    /**
     * Save the user and [open] a new screen
     */
    fun onSave(open: () -> Unit) {
        if (_loading) {
            return
        }

        _loading = true
        launchCatching(
            onFailure = { _loading = false },
        ) {
            userRepo.editUser(_oldUser.value, _newUser.value)
            SnackbarManager.showMessage("Updated profile!")
            open()
        }
    }

    /**
     * Remove the user's profile picture
     */
    fun onPictureRemove() {
        _newUser.value = _newUser.value.copy(pictureUri = "")
    }

    /**
     * Update the user's profile picture to [value]
     */
    fun onPictureChange(value: Uri) {
        _newUser.value = _newUser.value.copy(pictureUri = value.toString())
    }

    /**
     * Update the user's username to [value]
     */
    fun onUsernameChange(value: String) {
        _newUser.value = _newUser.value.copy(username = value)
    }

    /**
     * Update the user's fullname to [value]
     */
    fun onFullnameChange(value: String) {
        _newUser.value = _newUser.value.copy(fullname = value)
    }

    /**
     * Update the user's bio to [value]
     */
    fun onBioChange(value: String) {
        _newUser.value = _newUser.value.copy(bio = value)
    }
}

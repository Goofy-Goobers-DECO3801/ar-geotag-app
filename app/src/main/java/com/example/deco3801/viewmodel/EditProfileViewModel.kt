package com.example.deco3801.viewmodel

import android.net.Uri
import com.example.deco3801.data.model.User
import com.example.deco3801.data.repository.UserRepository
import com.example.deco3801.ui.components.SnackbarManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val auth: FirebaseAuth
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

    fun onSave(open: () -> Unit) {
        if (_loading) {
            return
        }

        _loading = true
        launchCatching(
            onFailure = { _loading = false }
        ) {
            userRepo.editUser(_oldUser.value, _newUser.value)
            SnackbarManager.showMessage("Updated profile!")
            open()
        }
    }

    fun onPictureRemove() {
        _newUser.value = _newUser.value.copy(pictureUri = "")
    }

    fun onPictureChange(value: Uri) {
        _newUser.value = _newUser.value.copy(pictureUri = value.toString())
    }

    fun onUsernameChange(value: String) {
        _newUser.value = _newUser.value.copy(username = value)
    }

    fun onFullnameChange(value: String) {
        _newUser.value = _newUser.value.copy(fullname = value)
    }

    fun onBioChange(value: String) {
        _newUser.value = _newUser.value.copy(bio = value)
    }

}
package com.example.deco3801.viewmodel

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
    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user


    init {
        launchCatching {
            _user.value = userRepo.getUser(auth.uid!!)!!
        }
    }

    fun onSave(open: () -> Unit) {
        launchCatching {
            userRepo.editUser(_user.value)
            SnackbarManager.showMessage("Updated profile!")
            open()
        }
    }

    fun onUserChange(user: User) {
        _user.value = user
    }
}
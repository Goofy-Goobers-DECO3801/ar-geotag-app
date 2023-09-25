package com.example.deco3801.viewmodel

import com.example.deco3801.data.model.Art
import com.example.deco3801.data.model.User
import com.example.deco3801.data.repository.ArtRepository
import com.example.deco3801.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject




@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val artRepo: ArtRepository,
) : AppViewModel() {
    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user

    private val _art = MutableStateFlow<List<Art>>(emptyList())
    val art: StateFlow<List<Art>> = _art

    fun attachListener(userid: String) {
        userRepo.attachListenerId(userid) {
            onUserChange(it)
        }
        artRepo.attachListenerByUserId(userid) {
            onArtChange(it)
        }
    }

    fun detachListener() {
        userRepo.detachListener()
        artRepo.detachListener()
    }


    fun onUserChange(user: User) {
        _user.value = user
    }

    fun onArtChange(art: List<Art>) {
        _art.value = art
    }
}
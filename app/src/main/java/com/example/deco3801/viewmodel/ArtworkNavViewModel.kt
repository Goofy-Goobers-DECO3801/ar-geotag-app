package com.example.deco3801.viewmodel

import android.util.Log
import com.example.deco3801.data.model.Art
import com.example.deco3801.data.model.User
import com.example.deco3801.data.repository.ArtRepository
import com.example.deco3801.data.repository.LikeRepository
import com.example.deco3801.data.repository.UserRepository
import com.example.deco3801.ui.components.SnackbarManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class ArtworkNavViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val artRepo: ArtRepository,
    private val likeRepo: LikeRepository,
    private val auth: FirebaseAuth,
) : AppViewModel() {
    private val _art = MutableStateFlow(Art())
    val art: StateFlow<Art> = _art

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user

    private val _liked = MutableStateFlow<Boolean?>(null)
    val liked: StateFlow<Boolean?> = _liked

    fun hasLiked(artId: String) {
        launchCatching {
            _liked.value = likeRepo.hasLiked(artId) != null
        }
    }

    fun onLikeClicked() {
        if (_liked.value == null) {
            return
        }

        launchCatching {
            if (_liked.value!!) {
                likeRepo.unLikeArt(_art.value.id)
            } else {
                likeRepo.likeArt(_art.value.id)
            }
            _liked.value = !_liked.value!!
        }
    }

    fun attachListener(artId: String) {
        artRepo.attachListenerById(artId) {
            onArtChange(it)
        }
        launchCatching {
            val userid = _art.first { it.userId != "" }.userId
            userRepo.attachListenerById(userid) {
                onUserChange(it)
            }
        }
        Log.d("PROFILE", "attaching listeners")
    }

    fun detachListener() {
        artRepo.detachListener()
        userRepo.detachListener()
        Log.d("PROFILE", "detaching listeners")
    }

    fun onArtChange(art: Art) {
        _art.value = art
    }

    fun onUserChange(user: User) {
        _user.value = user
    }

    fun onDeleteClicked() {
        launchCatching {
            artRepo.deleteArt(_art.value)
            SnackbarManager.showMessage("Post deleted!")
        }
    }

    fun onReportClicked() {
        launchCatching {
            artRepo.reportArt(_art.value)
            SnackbarManager.showMessage("Thank you for reporting!")
        }
    }

    fun isCurrentUser(userId: String) = userId == auth.uid
}

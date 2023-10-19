/**
 * ViewModel for the ArtworkNavScreen
 */
package com.goofygoobers.geoart.viewmodel

import android.util.Log
import com.goofygoobers.geoart.data.model.Art
import com.goofygoobers.geoart.data.model.User
import com.goofygoobers.geoart.data.repository.ArtRepository
import com.goofygoobers.geoart.data.repository.LikeRepository
import com.goofygoobers.geoart.data.repository.UserRepository
import com.goofygoobers.geoart.ui.components.SnackbarManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Contains the logic and state for the ArtworkNavScreen
 *
 * @constructor Create an Artwork nav view model with dependency injection
 *
 * @property userRepo The user repository to use, injected by Hilt
 * @property artRepo The art repository to use, injected by Hilt
 * @property likeRepo The like repository to use, injected by Hilt
 * @property auth The firebase auth instance to use, injected by Hilt
 */
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

    /**
     * Check if the current user has liked the art with [artId]
     */
    fun hasLiked(artId: String) {
        launchCatching {
            _liked.value = likeRepo.hasLiked(artId) != null
        }
    }

    /**
     * Like or unlike the art
     */
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

    /**
     * Attach listeners to the art with [artId] and user
     */
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

    /**
     * Detach listeners from the art and user
     */
    fun detachListener() {
        artRepo.detachListener()
        userRepo.detachListener()
        Log.d("PROFILE", "detaching listeners")
    }

    /**
     * Update the art with [art]
     */
    fun onArtChange(art: Art) {
        _art.value = art
    }

    /**
     * Update the user with [user]
     */
    fun onUserChange(user: User) {
        _user.value = user
    }

    /**
     * Callback run to delete the art
     */
    fun onDeleteClicked() {
        launchCatching {
            artRepo.deleteArt(_art.value)
            SnackbarManager.showMessage("Post deleted!")
        }
    }

    /**
     * Callback run to report the art
     */
    fun onReportClicked() {
        launchCatching {
            artRepo.reportArt(_art.value)
            SnackbarManager.showMessage("Thank you for reporting!")
        }
    }

    /**
     * Check if the current user's id is [userId]
     */
    fun isCurrentUser(userId: String) = userId == auth.uid
}

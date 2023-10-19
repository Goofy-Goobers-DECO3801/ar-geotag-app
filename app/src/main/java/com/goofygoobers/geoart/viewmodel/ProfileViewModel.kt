/**
 * The view model for the profile screen
 */
package com.goofygoobers.geoart.viewmodel

import android.util.Log
import com.goofygoobers.geoart.data.model.Art
import com.goofygoobers.geoart.data.model.User
import com.goofygoobers.geoart.data.repository.ArtRepository
import com.goofygoobers.geoart.data.repository.FollowRepository
import com.goofygoobers.geoart.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * The state of the follower sheet screen
 */
enum class FollowSheetState {
    HIDDEN,
    FOLLOWERS,
    FOLLOWING,
}

/**
 * Contains the logic and state for the profile screen
 *
 * @constructor Create a Profile view model with dependency injection
 * @property userRepo The user repository to use, injected by Hilt
 * @property artRepo The art repository to use, injected by Hilt
 * @property followRepo The follow repository to use, injected by Hilt
 * @property auth The firebase auth instance to use, injected by Hilt
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val artRepo: ArtRepository,
    private val followRepo: FollowRepository,
    private val auth: FirebaseAuth,
) : AppViewModel() {
    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user

    private val _art = MutableStateFlow<List<Art>>(emptyList())
    val art: StateFlow<List<Art>> = _art

    private val _isFollowing = MutableStateFlow<Boolean?>(null)
    val isFollowing: StateFlow<Boolean?> = _isFollowing

    private val _followSheetState = MutableStateFlow<FollowSheetState>(FollowSheetState.HIDDEN)
    val followSheetState: StateFlow<FollowSheetState> = _followSheetState

    private val _follows = MutableStateFlow<List<User>>(emptyList())
    val follows: StateFlow<List<User>> = _follows

    /**
     * Check if the current user is the user with [userId]
     */
    fun isCurrentUser(userId: String) = userId == auth.uid

    /**
     * Hide the follower sheet
     */
    fun hideFollowSheet() {
        _followSheetState.value = FollowSheetState.HIDDEN
    }

    /**
     * Get the users followers and show the follower sheet
     */
    fun onFollowersClick() {
        launchCatching {
            val followers = followRepo.getFollowers(_user.value)
            val users = userRepo.getUsers(followers.map { it.followerId })
            _follows.value = users
            _followSheetState.value = FollowSheetState.FOLLOWERS
        }
    }

    /**
     * Get the users following and show the follower sheet
     */
    fun onFollowingClick() {
        launchCatching {
            val following = followRepo.getFollowing(_user.value)
            val users = userRepo.getUsers(following.map { it.followingId })
            _follows.value = users
            _followSheetState.value = FollowSheetState.FOLLOWING
        }
    }

    /**
     * Check if the current user is following the user with [userId]
     */
    fun isFollowing(userId: String) {
        if (!isCurrentUser(userId)) {
            launchCatching {
                _isFollowing.value = followRepo.isFollowing(userId) != null
            }
        }
    }

    /**
     * Follow or unfollow the user
     */
    fun follow() {
        if (_isFollowing.value == null) {
            return
        }

        launchCatching {
            if (_isFollowing.value!!) {
                // Unfollow
                followRepo.unfollowUser(_user.value)
                _isFollowing.value = false
            } else {
                // Follow
                followRepo.followUser(_user.value)
                _isFollowing.value = true
            }
        }
    }

    /**
     * Attach listeners to the user with [userid] and their art
     */
    fun attachListener(userid: String) {
        userRepo.attachListenerById(userid) {
            onUserChange(it)
            Log.d("PROFILE", it.toString())
        }
        artRepo.attachListenerByUserId(userid) {
            onArtChange(it)
        }
        Log.d("PROFILE", "attaching listeners")
    }

    /**
     * Detach listeners from the user and their art
     */
    fun detachListener() {
        userRepo.detachListener()
        artRepo.detachListener()
        Log.d("PROFILE", "detaching listeners")
    }

    /**
     * Update the user state to [user]
     */
    fun onUserChange(user: User) {
        _user.value = user
    }

    /**
     * Update the art state to [art]
     */
    fun onArtChange(art: List<Art>) {
        _art.value = art
    }
}

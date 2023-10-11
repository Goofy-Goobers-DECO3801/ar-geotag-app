package com.example.deco3801.viewmodel

import android.util.Log
import com.example.deco3801.data.model.Art
import com.example.deco3801.data.model.User
import com.example.deco3801.data.repository.ArtRepository
import com.example.deco3801.data.repository.FollowRepository
import com.example.deco3801.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

enum class FollowSheetState {
    HIDDEN,
    FOLLOWERS,
    FOLLOWING,
}


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

    fun isCurrentUser(userId: String) = userId == auth.uid

    fun hideFollowSheet() {
        _followSheetState.value = FollowSheetState.HIDDEN
    }

    fun onFollowersClick() {
        launchCatching {
            val followers = followRepo.getFollowers(_user.value)
            val users = userRepo.getUsers(followers.map { it.followerId })
//            _follows.value = followers.zip(users)
            _follows.value = users
            _followSheetState.value = FollowSheetState.FOLLOWERS
        }

    }

    fun onFollowingClick() {
        launchCatching {
            val following = followRepo.getFollowing(_user.value)
            val users = userRepo.getUsers(following.map { it.followingId })
//            _follows.value = following.zip(users)
            _follows.value = users
            _followSheetState.value = FollowSheetState.FOLLOWING
        }
    }

    fun isFollowing(userId: String) {
        if (!isCurrentUser(userId)) {
            launchCatching {
                _isFollowing.value = followRepo.isFollowing(userId) != null
            }
        }
    }

    fun removeFollower(user: User) {
        // TODO:
    }

    fun unfollowUser(user: User) {
        launchCatching {
            followRepo.unfollowUser(user)
            _follows.value = _follows.value.toMutableList().apply { remove(user) }
        }
    }

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

    fun detachListener() {
        userRepo.detachListener()
        artRepo.detachListener()
        Log.d("PROFILE", "detaching listeners")
    }


    fun onUserChange(user: User) {
        _user.value = user
    }

    fun onArtChange(art: List<Art>) {
        _art.value = art
    }
}

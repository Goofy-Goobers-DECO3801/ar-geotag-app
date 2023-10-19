/**
 * CommentViewModel.kt
 */
package com.goofygoobers.geoart.viewmodel

import android.util.Log
import com.goofygoobers.geoart.data.model.Comment
import com.goofygoobers.geoart.data.model.User
import com.goofygoobers.geoart.data.repository.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Contains the state for the CommentScreen
 */
data class CommentState(
    val comment: Comment,
    val user: User,
)

/**
 * Contains the logic and state for the CommentScreen
 *
 * @constructor Create a Comment view model with dependency injection
 * @property commentRepo The comment repository to use, injected by Hilt
 */
@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepo: CommentRepository,
) : AppViewModel() {
    private val _comments = MutableStateFlow<List<CommentState>>(emptyList())
    val comments: StateFlow<List<CommentState>> = _comments

    /**
     * Attach a listener to the comments for the art with [artId]
     */
    fun attachListener(artId: String) {
        commentRepo.attachListenerByArt(artId) {
            onCommentChange(it)
        }
        Log.d("COMMENT", "attaching listeners")
    }

    /**
     * Detach the listener from the comments
     */
    fun detachListener() {
        commentRepo.detachListener()
        Log.d("COMMENT", "detaching listeners")
    }

    /**
     * Post a comment with [text] on the art with [artId]
     */
    fun onCommentPosted(
        artId: String,
        text: String,
    ) {
        launchCatching {
            commentRepo.commentOnArt(artId, text)
        }
    }

    /**
     * Update the comments state to [comments]
     */
    private fun onCommentChange(comments: List<Comment>) {
        val state: MutableList<CommentState> = mutableListOf()
        launchCatching {
            comments.forEach {
                state.add(CommentState(comment = it, user = commentRepo.getCommenter(it)))
            }
            _comments.value = state
        }
    }
}

package com.example.deco3801.viewmodel

import android.util.Log
import com.example.deco3801.data.model.Comment
import com.example.deco3801.data.model.User
import com.example.deco3801.data.repository.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class CommentState(
    val comment: Comment,
    val user: User,
)

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepo: CommentRepository,
) : AppViewModel() {
    private val _comments = MutableStateFlow<List<CommentState>>(emptyList())
    val comments: StateFlow<List<CommentState>> = _comments

    fun attachListener(artId: String) {
        commentRepo.attachListenerByArt(artId) {
            onCommentChange(it)
        }
        Log.d("COMMENT", "attaching listeners")
    }

    fun detachListener() {
        commentRepo.detachListener()
        Log.d("COMMENT", "detaching listeners")
    }

    fun onCommentPosted(artId: String, text: String) {
        launchCatching {
            commentRepo.commentOnArt(artId, text)
        }
    }

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

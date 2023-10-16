package com.example.deco3801.data.repository

import com.example.deco3801.data.model.Comment
import com.example.deco3801.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val artRepo: ArtRepository,
    private val userRepository: UserRepository,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : Repository<Comment>(Comment::class.java) {

    fun commentOnArt(artId: String, text: String) {
        val uid = auth.uid!!
        val comment = Comment(
            artId = artId,
            userId = uid,
            text = text,
        )

        val commentRef = getCollectionRef(artId).document()
        val artRef = artRepo.getCollectionRef().document(artId)

        db.runBatch {
            it.set(commentRef, comment)
            it.update(artRef, "commentCount", FieldValue.increment(1))
        }
    }

    suspend fun getCommenter(comment: Comment): User {
        return userRepository.getUser(comment.userId)!!
    }

    fun attachListenerByArt(artId: String, callback: (List<Comment>) -> Unit) {
        attachListenerWithQuery(callback, subCollectionId = artId) { query ->
            query.orderBy("timestamp", Query.Direction.DESCENDING)
        }
    }


    override fun getCollectionRef(docId: String?): CollectionReference {
        assert(docId != null)
        return artRepo.getCommentSubCollectionRef(docId!!)
    }
}

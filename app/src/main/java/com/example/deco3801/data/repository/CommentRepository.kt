/**
 * Contains the repository used to interact with the Comment collection on Firebase.
 */
package com.example.deco3801.data.repository

import com.example.deco3801.data.model.Comment
import com.example.deco3801.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import javax.inject.Inject

/**
 * Repository used to interact with the Comment collection on Firebase.
 *
 * @constructor Creates a new CommentRepository with injectable dependencies.
 * @property artRepo The ArtRepository instance.
 * @property userRepository The UserRepository instance.
 * @property db The Firestore database instance.
 * @property auth The Firebase Authentication instance.
 */
class CommentRepository @Inject constructor(
    private val artRepo: ArtRepository,
    private val userRepository: UserRepository,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : Repository<Comment>(Comment::class.java) {

    /**
     * Creates a new comment on a specific art and updates the increments count on the art.
     *
     * @param artId The id of the art the comment is being created on.
     * @param text The text of the comment being created.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when adding or updating a document.
     */
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

    /**
     * Gets the commenter of a comment.
     *
     * @param comment The comment to get the commenter of.
     * @return The commenter of the comment.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when getting a document.
     */
    suspend fun getCommenter(comment: Comment): User {
        return userRepository.getUser(comment.userId)!!
    }

    /**
     * Attaches a listener to the comments of a specific art.
     *
     * @param artId The id of the art to attach the listener to.
     * @param callback The callback to be called when the listener is triggered.
     */
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

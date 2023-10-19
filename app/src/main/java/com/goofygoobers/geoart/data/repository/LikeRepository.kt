/**
 * Contains the repository used to interact with the Like collection on Firebase.
 */
package com.goofygoobers.geoart.data.repository

import com.goofygoobers.geoart.data.model.Like
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import javax.inject.Inject

/**
 * Repository used to interact with the Like collection on Firebase.
 *
 * @constructor Creates a new LikeRepository with injectable dependencies.
 * @property artRepo The ArtRepository instance.
 * @property db The Firestore database instance.
 * @property auth The Firebase Authentication instance.
 */
class LikeRepository @Inject constructor(
    private val artRepo: ArtRepository,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : Repository<Like>(Like::class.java) {
    /**
     * Checks if the current user has liked a specific art.
     *
     * @param artId The id of the art being checked.
     * @return The Like model if the user has liked the art, null otherwise.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when reading a document.
     */
    suspend fun hasLiked(artId: String): Like? {
        return getById(auth.uid!!, subCollectionId = artId)
    }

    /**
     * Add a Like to a specific art for the current user and increment the like count on the art.
     *
     * @param artId The id of the art being liked.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when creating or updating a document.
     */
    fun likeArt(artId: String) {
        val uid = auth.uid!!

        val like =
            Like(
                artId = artId,
                userId = uid,
            )
        val likeRef = getCollectionRef(artId).document(uid)
        val artRef = artRepo.getCollectionRef().document(artId)

        db.runBatch {
            it.set(likeRef, like)
            it.update(artRef, "likeCount", FieldValue.increment(1))
        }
    }

    /**
     * Remove a Like from a specific art for the current user and decrement the like count on the art.
     *
     * @param artId The id of the art being unliked.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when deleting or updating a document.
     */
    fun unLikeArt(artId: String) {
        val uid = auth.uid!!
        val likeRef = getCollectionRef(artId).document(uid)
        val artRef = artRepo.getCollectionRef().document(artId)

        db.runBatch {
            it.delete(likeRef)
            it.update(artRef, "likeCount", FieldValue.increment(-1))
        }
    }

    override fun getCollectionRef(docId: String?): CollectionReference {
        assert(docId != null)
        return artRepo.getLikeSubCollectionRef(docId!!)
    }
}

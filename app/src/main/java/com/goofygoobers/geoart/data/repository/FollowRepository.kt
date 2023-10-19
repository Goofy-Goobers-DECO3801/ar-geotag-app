/**
 * Contains the repository used to interact with the Follow collection on Firebase.
 */
package com.goofygoobers.geoart.data.repository

import com.goofygoobers.geoart.data.model.Follow
import com.goofygoobers.geoart.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repository used to interact with the Follow collection on Firebase.
 *
 * @constructor Creates a new FollowRepository with injectable dependencies.
 * @property userRepo The UserRepository instance.
 * @property db The Firestore database instance.
 * @property auth The Firebase Authentication instance.
 */
class FollowRepository @Inject constructor(
    private val userRepo: UserRepository,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : Repository<Follow>(Follow::class.java) {
    /**
     * Checks if the current user is following a specific user.
     *
     * @param userId The id of the user being checked.
     * @return The Follow model if the user is following the user, null otherwise.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when reading a document.
     */
    suspend fun isFollowing(userId: String): Follow? {
        return getById(userId)
    }

    /**
     * Checks if the current user is following a specific user.
     *
     * @param user The user being checked.
     * @return The Follow model if the user is following the user, null otherwise.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when reading a document.
     */
    suspend fun isFollowing(user: User): Follow? {
        return isFollowing(user.id)
    }

    /**
     * Gets all the users that the given user is following.
     *
     * @param user The user being checked.
     * @return A list of Follow models representing the users that the current user is following.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when reading a document.
     */
    suspend fun getFollowing(user: User): List<Follow> {
        return getAll(subCollectionId = user.id)
    }

    /**
     * Gets all the users that are following the given user.
     *
     * @param user The user being checked.
     * @return A list of Follow models representing the users that are following the given user.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when reading a document.
     */
    suspend fun getFollowers(user: User): List<Follow> {
        return db.collectionGroup("following")
            .whereEqualTo("followingId", user.id)
            .get()
            .await()
            .toObjects()
    }

    /**
     * Add a follow for the current user to the given user into the database and
     * increment the current user's following count and the given user's follower count.
     *
     * @param user The user being followed.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when creating or updating a document.
     */
    fun followUser(user: User) {
        val follow =
            Follow(
                followerId = auth.uid!!,
                followingId = user.id,
            )
        val followingRef = getCollectionRef().document(user.id)
        val me = userRepo.getCollectionRef().document(auth.uid!!)
        val them = userRepo.getCollectionRef().document(user.id)

        db.runBatch {
            it.set(followingRef, follow)
            it.update(me, "followingCount", FieldValue.increment(1))
            it.update(them, "followerCount", FieldValue.increment(1))
        }
    }

    /**
     * Remove a follow for the current user to the given user from the database and
     * decrement the current user's following count and the given user's follower count.
     *
     * @param user The user being unfollowed.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when deleting or updating a document.
     */
    fun unfollowUser(user: User) {
        val followingRef = getCollectionRef().document(user.id)
        val me = userRepo.getCollectionRef().document(auth.uid!!)
        val them = userRepo.getCollectionRef().document(user.id)

        db.runBatch {
            it.delete(followingRef)
            it.update(me, "followingCount", FieldValue.increment(-1))
            it.update(them, "followerCount", FieldValue.increment(-1))
        }
    }

    override fun getCollectionRef(docId: String?): CollectionReference {
        return userRepo.getFollowSubCollectionRef(docId ?: auth.uid!!)
    }
}

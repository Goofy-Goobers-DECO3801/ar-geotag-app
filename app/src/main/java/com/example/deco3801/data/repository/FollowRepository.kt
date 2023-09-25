package com.example.deco3801.data.repository

import com.example.deco3801.data.model.Follow
import com.example.deco3801.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FollowRepository @Inject constructor(
    private val userRepo: UserRepository,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : Repository<Follow>(Follow::class.java) {

    suspend fun isFollowing(userId: String): Follow? {
        return getCollectionRef()
            .document(userId)
            .get()
            .await()
            .toObject()
    }

    suspend fun getFollowing(user: User): List<Follow> {
        return userRepo.getCollectionRef()
            .document(user.id)
            .collection(FOLLOW_COLLECTION)
            .get()
            .await()
            .toObjects()
    }

    suspend fun getFollowers(user: User): List<Follow> {
        return db.collectionGroup("following")
            .whereEqualTo("followingId", user.id)
            .get()
            .await()
            .toObjects()
    }

    suspend fun isFollowing(user: User): Follow? {
        return isFollowing(user.id)
    }

    suspend fun followUser(user: User) {
        val follow = Follow(
            followerId = auth.uid!!,
            followerName = auth.currentUser!!.displayName!!,
            followingId = user.id,
            followingName = user.username
        )
        val followingRef = getCollectionRef().document(user.id)
        val me = userRepo.getCollectionRef().document(auth.uid!!)
        val them = userRepo.getCollectionRef().document(user.id)

        db.runBatch {
            it.set(followingRef, follow)
            it.update(me, "followingCount", FieldValue.increment(1))
            it.update(them, "followerCount", FieldValue.increment(1))
        }.await()
    }

    suspend fun unfollowUser(user: User) {
        val followingRef = getCollectionRef().document(user.id)
        val me = userRepo.getCollectionRef().document(auth.uid!!)
        val them = userRepo.getCollectionRef().document(user.id)

        db.runBatch {
            it.delete(followingRef)
            it.update(me, "followingCount", FieldValue.increment(-1))
            it.update(them, "followerCount", FieldValue.increment(-1))
        }.await()
    }

    override fun getCollectionRef(): CollectionReference {
        return userRepo.getCollectionRef().document(auth.uid!!).collection(FOLLOW_COLLECTION)
    }


    companion object {
        private const val FOLLOW_COLLECTION = "following"
    }
}


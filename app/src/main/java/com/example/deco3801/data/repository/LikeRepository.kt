package com.example.deco3801.data.repository

import com.example.deco3801.data.model.Like
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LikeRepository @Inject constructor(
    private val artRepo: ArtRepository,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : Repository<Like>(Like::class.java) {

    suspend fun hasLiked(artId: String): Like? {
        return getCollectionRef(artId)
            .document(auth.uid!!)
            .get()
            .await()
            .toObject()
    }

    fun likeArt(artId: String) {
        val uid = auth.uid!!

        val like = Like(
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

    fun unLikeArt(artId: String) {
        val uid = auth.uid!!
        val likeRef = getCollectionRef(artId).document(uid)
        val artRef = artRepo.getCollectionRef().document(artId)

        db.runBatch {
            it.delete(likeRef)
            it.update(artRef, "likeCount", FieldValue.increment(-1))
        }
    }

    fun getCollectionRef(artId: String): CollectionReference {
        return artRepo.getCollectionRef().document(artId).collection(LIKE_COLLECTION)
    }


    override fun getCollectionRef(): CollectionReference {
        return artRepo.getCollectionRef()
    }


    companion object {
        private const val LIKE_COLLECTION = "likes"
    }
}


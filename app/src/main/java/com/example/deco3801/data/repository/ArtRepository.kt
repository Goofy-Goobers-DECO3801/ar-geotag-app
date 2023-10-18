package com.example.deco3801.data.repository

import android.location.Location
import android.net.Uri
import android.util.Log
import com.example.deco3801.data.model.Art
import com.example.deco3801.data.model.User
import com.example.deco3801.ui.components.ProgressbarState
import com.example.deco3801.util.toGeoLocation
import com.example.deco3801.util.toGeoPoint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import org.imperiumlabs.geofirestore.core.GeoHash
import javax.inject.Inject

class ArtRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    private val userRepo: UserRepository,
) : Repository<Art>(Art::class.java) {
    suspend fun createArt(
        title: String,
        description: String,
        location: Location,
        uri: Uri,
        filename: String,
    ): Art {
        val uid = auth.uid!!
        var storageRef: StorageReference? = null
        var storagePath = ""

        if (uri.scheme != "https" ) {
            storagePath = "$uid/art/${System.currentTimeMillis()}-${filename}"
            storageRef = storage.reference.child(storagePath)
            storageRef.putFile(uri).addOnProgressListener {
                val progress = it.bytesTransferred.toFloat() / it.totalByteCount
                ProgressbarState.updateProgressbar(progress)
            }.await()
        }
        val art = Art(
            title = title,
            description = description,
            location = location.toGeoPoint(),
            altitude = location.altitude,
            geohash = GeoHash(location.toGeoLocation()).geoHashString,
            userId = uid,
            storageUri = storageRef?.downloadUrl?.await()?.toString() ?: uri.toString(),
            storageRef = storagePath
        )

        Log.d(ART_COLLECTION, art.toString())

        getCollectionRef().add(art).addOnFailureListener {
            storageRef?.delete()
        }.addOnSuccessListener {
            art.id = it.id
        }.await()
        return art
    }

    suspend fun deleteArt(art: Art) {
        getCollectionRef().document(art.id).delete().addOnSuccessListener {
            if(art.storageRef.isNotBlank()) {
                storage.reference.child(art.storageRef).delete()
            }
//            getCommentSubCollectionRef(art.id).delete()
//            getLikeSubCollectionRef(art.id).delete()
        }.await()
    }

    suspend fun reportArt(art: Art) {
        getCollectionRef().document(art.id).update("reportCount", FieldValue.increment(1)).await()
    }

    fun attachListenerByUserId(userId: String, callback: (List<Art>) -> Unit) {
        attachListenerWithQuery(callback) { query ->
            query.whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
        }
    }

    suspend fun getArtist(art: Art): User {
        return userRepo.getUser(art.userId)!!
    }

    override fun getCollectionRef(docId: String?): CollectionReference {
        return db.collection(ART_COLLECTION)
    }

    fun getCommentSubCollectionRef(artId: String): CollectionReference {
        return getCollectionRef().document(artId).collection(COMMENT_COLLECTION)
    }

    fun getLikeSubCollectionRef(artId: String): CollectionReference {
        return getCollectionRef().document(artId).collection(LIKE_COLLECTION)
    }

    companion object {
        private const val ART_COLLECTION = "art"
        private const val COMMENT_COLLECTION = "comments"
        private const val LIKE_COLLECTION = "likes"
    }
}

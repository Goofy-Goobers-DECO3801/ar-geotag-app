package com.example.deco3801.data.repository

import android.location.Location
import android.net.Uri
import android.util.Log
import com.example.deco3801.data.model.Art
import com.example.deco3801.ui.components.ProgressbarState
import com.example.deco3801.util.toGeoLocation
import com.example.deco3801.util.toGeoPoint
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import org.imperiumlabs.geofirestore.core.GeoHash
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val user: FirebaseUser?
) {
    suspend fun createArt(
        title: String,
        description: String,
        location: Location,
        uri: Uri,
        filename: String,
    ): Art {

        if (user == null) {
            throw Exception("User is not logged in.")
        }
        val uid = user.uid

        val storagePath = "$uid/art/${System.currentTimeMillis()}-${filename}"
        val storageRef = storage.reference.child(storagePath)
        storageRef.putFile(uri).addOnProgressListener {
            val progress = it.bytesTransferred.toFloat() / it.totalByteCount
            ProgressbarState.updateProgressbar(progress)
        }.await()

        val art = Art(
            title = title,
            description = description,
            location = location.toGeoPoint(),
            altitude = location.altitude,
            geohash = GeoHash(location.toGeoLocation()).geoHashString,
            userId = uid,
            storageUrl = storageRef.downloadUrl.await().toString(),
        )

        Log.d(ART_COLLECTION, art.toString())

        db.collection(ART_COLLECTION).add(art).addOnFailureListener {
            storageRef.delete()
            throw it
        }.await()
        return art
    }

    fun getCollectionRef(): CollectionReference {
        return db.collection(ART_COLLECTION)
    }

    companion object {
        private const val ART_COLLECTION = "art"
    }
}
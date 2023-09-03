package com.example.deco3801.data.repository

import android.location.Location
import android.net.Uri
import android.util.Log
import com.example.deco3801.data.model.Art
import com.example.deco3801.data.model.toGeotag
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val user: FirebaseUser?
) {
    suspend fun create(title: String, description: String, location: Location, uri: Uri): Art {
        if (user == null) {
            throw Exception("User is not logged in.")
        }

        val uid = user.uid
        val art = Art(
            title = title,
            description = description,
            geotag = location.toGeotag(),
            userId = uid,
            storagePath = "$uid/${System.currentTimeMillis()}-${File(uri.path!!).name}"
        )

        Log.d(ART_COLLECTION, art.toString())

        storage.reference.child(art.storagePath).putFile(uri).await()
        db.collection(ART_COLLECTION).add(art).await()
        return art
    }

    companion object {
        private const val ART_COLLECTION = "Art"
    }
}
package com.example.deco3801.model.service

import android.location.Location
import android.net.Uri
import android.util.Log
import com.example.deco3801.model.Art
import com.example.deco3801.model.Geotag
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
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
            geotag = Geotag(
                location.latitude,
                location.longitude,
                location.altitude,
                GeoFireUtils.getGeoHashForLocation(
                    GeoLocation(
                        location.latitude,
                        location.longitude,
                    )
                ),
            ),
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
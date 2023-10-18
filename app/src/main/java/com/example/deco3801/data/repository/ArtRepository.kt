/**
 * Contains the repository used to interact with the Art collection on Firebase.
 *
 * @author Jed Willick
 */
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
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import org.imperiumlabs.geofirestore.core.GeoHash
import javax.inject.Inject

/**
 * Repository used to interact with the Art data on Firebase.
 *
 * @constructor Creates a new ArtRepository with injectable dependencies.
 * @property db The Firestore database instance.
 * @property storage The Firebase Storage instance.
 * @property auth The Firebase Authentication instance.
 * @property userRepo The UserRepository instance.
 */
class ArtRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    private val userRepo: UserRepository,
) : Repository<Art>(Art::class.java) {

    /**
     * Create a new artwork on the Firestore database and upload the model to Storage.
     *
     * @param title Title of the art being created.
     * @param description Description of the art being created.
     * @param uri URI location of the art (If this is a URL nothing is uploaded to Storage).
     * @param filename The name of the file being uploaded.
     * @return the Art model that was created.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when adding a document.
     * @throws StorageException Storage exceptions that may occur when adding an object.
     */
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

    /**
     * Delete an artwork from the Firestore database and Storage.
     *
     * @param art the Art model being deleted.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when deleting a document.
     * @throws StorageException Storage exceptions that may occur when deleting an object.
     */
    suspend fun deleteArt(art: Art) {
        getCollectionRef().document(art.id).delete().addOnSuccessListener {
            if(art.storageRef.isNotBlank()) {
                storage.reference.child(art.storageRef).delete()
            }
//            getCommentSubCollectionRef(art.id).delete()
//            getLikeSubCollectionRef(art.id).delete()
        }.await()
    }

    /**
     * Update an artwork on the Firestore database.
     *
     * @param art the Art model being updated.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when updating a document.
     */
    suspend fun reportArt(art: Art) {
        getCollectionRef().document(art.id).update("reportCount", FieldValue.increment(1)).await()
    }

    /**
     * Attach a listener on changes to Art created by a specific user.
     *
     * @param userId the id of the user whose art is being listened to.
     * @param callback the callback function to be called when the data changes.
     */
    fun attachListenerByUserId(userId: String, callback: (List<Art>) -> Unit) {
        attachListenerWithQuery(callback) { query ->
            query.whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
        }
    }

    /**
     * Get the user who created an artwork.
     *
     * @param art the Art model being queried.
     * @return the User model of the user who created the artwork.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when getting a document.
     * @throws NullPointerException if the artist does not exist.
     */
    suspend fun getArtist(art: Art): User {
        return userRepo.getUser(art.userId)!!
    }

    override fun getCollectionRef(docId: String?): CollectionReference {
        return db.collection(ART_COLLECTION)
    }

    /**
     * Get the sub-collection of comments for an artwork.
     *
     * @param artId the id of the artwork whose comments are being queried.
     * @return the CollectionReference of the comments sub-collection.
     */
    fun getCommentSubCollectionRef(artId: String): CollectionReference {
        return getCollectionRef().document(artId).collection(COMMENT_COLLECTION)
    }

    /**
     * Get the sub-collection of likes for an artwork.
     *
     * @param artId the id of the artwork whose likes are being queried.
     * @return the CollectionReference of the likes sub-collection.
     */
    fun getLikeSubCollectionRef(artId: String): CollectionReference {
        return getCollectionRef().document(artId).collection(LIKE_COLLECTION)
    }

    companion object {
        private const val ART_COLLECTION = "art"
        private const val COMMENT_COLLECTION = "comments"
        private const val LIKE_COLLECTION = "likes"
    }
}

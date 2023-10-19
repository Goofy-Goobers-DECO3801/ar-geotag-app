/**
 * Contains the repository used to interact with the User collection on Firebase.
 */
package com.example.deco3801.data.repository

import android.util.Log
import androidx.core.net.toUri
import com.example.deco3801.data.model.User
import com.example.deco3801.ui.components.ProgressbarState
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import javax.inject.Inject

/**
 * Repository used to interact with the User collection on Firebase.
 *
 * @constructor Creates a new UserRepository with injectable dependencies.
 * @property db The Firestore database instance.
 * @property auth The Firebase Authentication instance.
 * @property storage The Firebase Storage instance.
 */
class UserRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
) : Repository<User>(User::class.java) {
    /**
     * Creates and authenticates a new user with their [email] and [password] on Firebase Authentication
     * and creates a new user document on Firestore.
     *
     * The [username] and [email] must be globally unique to all users on the app.
     *
     * @param username The username of the user being created.
     * @param email The email of the user being created.
     * @param password The password of the user being created.
     * @return The User model that was created.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when adding a document.
     * @throws FirebaseAuthException FirebaseAuth exceptions that may occur when creating and authenticating a user.
     */
    suspend fun createUser(
        username: String,
        email: String,
        password: String,
    ): User {
        val authUser = auth.createUserWithEmailAndPassword(email, password).await().user!!
        val id = authUser.uid
        val user = User(username = username, email = email)

        Log.d(USER_COLLECTION, user.toString())

        // Ensure that the username is unique if its not delete the authUser
        val userRef = db.collection(USER_COLLECTION).document(id)
        val indexRef = db.collection(INDEX_COLLECTION).document("$USERNAME_INDEX/$username")
        try {
            db.runBatch {
                it.set(userRef, user)
                it.set(indexRef, hashMapOf("value" to id))
            }.await()
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> throw Exception(
                    "Username is already taken!",
                    e,
                )

                else -> {
                    throw e
                }
            }
        }
        return user
    }

    /**
     * Re-authenticates the user and updates the password of the current user.
     *
     * @param oldPassword The old password of the current user.
     * @param newPassword The new password of the current user.
     *
     * @throws FirebaseAuthException FirebaseAuth exceptions that may occur when re-authenticating or updating a user.
     */
    suspend fun updatePassword(
        oldPassword: String,
        newPassword: String,
    ) {
        val user = auth.currentUser!!
        user.reauthenticate(EmailAuthProvider.getCredential(user.email!!, oldPassword)).await()
        user.updatePassword(newPassword).await()
    }

    /**
     * Updates the privacy setting of the current user.
     *
     * @param isPrivate The new privacy setting of the current user.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when updating a document.
     */
    suspend fun updateIsPrivate(isPrivate: Boolean) {
        updateUser(auth.uid!!, hashMapOf("isPrivate" to isPrivate))
    }

    /**
     * Edits the current user with the new user information.
     *
     * If the profile picture is changed the old profile picture is deleted from Storage
     * and the new one is uploaded.
     *
     * @param oldUser The old user information.
     * @param newUser The new user information.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when updating a document.
     * @throws StorageException Storage exceptions that may occur when updating an object.
     */
    suspend fun editUser(
        oldUser: User,
        newUser: User,
    ) {
        val uid = auth.uid!!
        var storageRef: StorageReference? = null
        if (oldUser == newUser) {
            return
        }

        val newerUser = newUser.copy()

        if (newerUser.pictureUri.isNotBlank() && oldUser.pictureUri != newerUser.pictureUri) {
            val storagePath = "$uid/profile-picture-${System.currentTimeMillis()}"
            storageRef = storage.reference.child(storagePath)
            storageRef.putFile(newerUser.pictureUri.toUri()).addOnProgressListener {
                val progress = it.bytesTransferred.toFloat() / it.totalByteCount
                ProgressbarState.updateProgressbar(progress)
            }.await()
            newerUser.pictureUri = storageRef.downloadUrl.await().toString()
        }

        Log.d(USER_COLLECTION, "NEW: $newerUser")
        Log.d(USER_COLLECTION, "OLD: $oldUser")

        // Ensure that the username is unique
        val userRef = getCollectionRef().document(uid)
        val indexRef =
            db.collection("${INDEX_COLLECTION}/${USERNAME_INDEX}").document(newerUser.username)
        val oldIndexRef =
            db.collection("${INDEX_COLLECTION}/${USERNAME_INDEX}").document(oldUser.username)
        try {
            db.runBatch {
                it.set(userRef, newerUser, SetOptions.merge())
                if (oldUser.username != newerUser.username) {
                    Log.d(USER_COLLECTION, "Changing Index")
                    it.delete(oldIndexRef)
                    it.set(indexRef, hashMapOf("value" to uid))
                }
            }.addOnFailureListener {
                storageRef?.delete()
            }.await()
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> throw Exception(
                    "Username is already taken!",
                    e,
                )

                else -> {
                    throw e
                }
            }
        }
    }

    /**
     * Updates the user with the given [userId] with the given [fields].
     *
     * @param userId The id of the user being updated.
     * @param fields The fields being updated.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when updating a document.
     */
    private suspend fun updateUser(
        userId: String,
        fields: HashMap<String, Serializable?>,
    ) {
        getCollectionRef().document(userId)
            .set(fields, SetOptions.mergeFields(fields.keys.toList())).await()
    }

    /**
     * Gets the user with the given [userId].
     *
     * @param userId The id of the user being retrieved.
     * @return The User model with the given id.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when reading a document.
     */
    suspend fun getUser(userId: String): User? {
        return getCollectionRef().document(userId).get().await().toObject()
    }

    /**
     * Gets the users with the given [userIds].
     *
     * @param userIds The ids of the users being retrieved.
     * @return A list of User models with the given ids.
     *
     * @throws FirebaseFirestoreException Firestore exceptions that may occur when reading a document.
     */
    suspend fun getUsers(userIds: List<String>): List<User> {
        if (userIds.isEmpty()) {
            return emptyList()
        }

        return getCollectionRef().whereIn(FieldPath.documentId(), userIds).get().await().toObjects()
    }

    /**
     * Login and authenticate the user with the given [email] and [password] and return the result.
     *
     * @param email The email of the user being logged in.
     * @param password The password of the user being logged in.
     * @return The AuthResult of the login attempt.
     *
     * @throws FirebaseAuthException FirebaseAuth exceptions that may occur when logging in a user.
     */
    suspend fun loginUser(
        email: String,
        password: String,
    ): AuthResult? {
        return auth.signInWithEmailAndPassword(email, password).await()
    }

    override fun getCollectionRef(docId: String?): CollectionReference {
        return db.collection(USER_COLLECTION)
    }

    /**
     * Get the collection reference of the following sub-collection for a specific user.
     *
     * @param userId The id of the user whose following sub-collection is being retrieved.
     * @return The CollectionReference of the following sub-collection.
     */
    fun getFollowSubCollectionRef(userId: String = auth.uid!!): CollectionReference {
        return getCollectionRef().document(userId).collection(FOLLOW_COLLECTION)
    }

    companion object {
        private const val USER_COLLECTION = "user"
        private const val INDEX_COLLECTION = "index"
        private const val USERNAME_INDEX = "user/username"
        private const val FOLLOW_COLLECTION = "following"
    }
}

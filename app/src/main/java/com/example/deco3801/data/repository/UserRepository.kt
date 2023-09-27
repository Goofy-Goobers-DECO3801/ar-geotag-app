package com.example.deco3801.data.repository

import android.util.Log
import androidx.core.net.toUri
import com.example.deco3801.data.model.User
import com.example.deco3801.ui.components.ProgressbarState
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : Repository<User>(User::class.java) {
    suspend fun createUser(username: String, email: String, password: String): User {
        val authUser = auth.createUserWithEmailAndPassword(email, password).await().user!!
        val id = authUser.uid
        val user = User(username = username, email = email)

        authUser.updateProfile(userProfileChangeRequest {
            displayName = username
        }).await()
        Log.d(USER_COLLECTION, user.toString())

        // Ensure that the username is unique if its not delete the authUser
        val userRef = db.collection(USER_COLLECTION).document(id)
        val indexRef = db.collection(INDEX_COLLECTION).document("$USERNAME_INDEX/${username}")
        try {
            db.runBatch {
                it.set(userRef, user)
                it.set(indexRef, hashMapOf("value" to id))
            }.await()
        } catch (e: Exception) {
            authUser.delete()
            throw Exception("Username is already taken!", e)
        }
        return user
    }

    suspend fun updatePassword(oldPassword: String, newPassword: String) {
        val user = auth.currentUser!!
        user.reauthenticate(EmailAuthProvider.getCredential(user.email!!, oldPassword)).await()
        user.updatePassword(newPassword).await()
    }

    suspend fun updateIsPrivate(isPrivate: Boolean) {
        updateUser(auth.uid!!, hashMapOf("isPrivate" to isPrivate))
    }

    suspend fun editUser(user: User) {
        val uid = auth.uid!!
        val newUser = user.copy()
        var storageRef: StorageReference? = null

        if (!user.pictureUri.startsWith("https://firebasestorage")) {
            val storagePath = "$uid/profile-picture-${System.currentTimeMillis()}"
            storageRef = storage.reference.child(storagePath)
            storageRef.putFile(user.pictureUri.toUri()).addOnProgressListener {
                val progress = it.bytesTransferred.toFloat() / it.totalByteCount
                ProgressbarState.updateProgressbar(progress)
            }.await()
            newUser.pictureUri = storageRef.downloadUrl.await().toString()
        }

        Log.d(USER_COLLECTION, newUser.toString())

        getCollectionRef().document(uid).set(newUser, SetOptions.merge()).addOnFailureListener {
            storageRef?.delete()
        }.await()
    }

    private suspend fun updateUser(userId: String, fields: HashMap<String, Serializable?>) {
        getCollectionRef().document(userId)
            .set(fields, SetOptions.mergeFields(fields.keys.toList())).await()
    }

    suspend fun getUser(userId: String): User? {

        return getCollectionRef().document(userId).get().await().toObject()
    }

    suspend fun getUsers(userIds: List<String>): List<User> {
        if (userIds.isEmpty()) {
            return emptyList()
        }

        return getCollectionRef().whereIn(FieldPath.documentId(), userIds).get().await().toObjects()
    }

    suspend fun loginUser(email: String, password: String): AuthResult? {
        return auth.signInWithEmailAndPassword(email, password).await()
    }


    override fun getCollectionRef(): CollectionReference {
        return db.collection(USER_COLLECTION)
    }


    companion object {
        private const val USER_COLLECTION = "user"
        private const val INDEX_COLLECTION = "index"
        private const val USERNAME_INDEX = "user/username"
    }
}


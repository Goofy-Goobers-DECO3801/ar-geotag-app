package com.example.deco3801.data.repository

import android.util.Log
import com.example.deco3801.data.model.Art
import com.example.deco3801.data.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
):Repository<User>(User::class.java) {
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


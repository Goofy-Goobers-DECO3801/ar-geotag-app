package com.example.deco3801.data.repository

import android.util.Log
import com.example.deco3801.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    suspend fun create(username: String, email: String, password: String): User {
        val authUser = auth.createUserWithEmailAndPassword(email, password).await().user!!
        val id = authUser.uid
        val user = User(id = id, username = username, email = email)
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

        Log.d(USER_COLLECTION, user.toString())
        return user
    }

    companion object {
        private const val USER_COLLECTION = "User"
        private const val INDEX_COLLECTION = "Index"
        private const val USERNAME_INDEX = "User/username"
    }
}


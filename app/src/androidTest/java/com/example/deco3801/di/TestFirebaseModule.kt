package com.example.deco3801.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [FirebaseModule::class])
object TestFirebaseModule {
    private const val HOST = "10.0.2.2"
    private const val AUTH_PORT = 9099
    private const val FIRESTORE_PORT = 8080
    private const val STORAGE_PORT = 9199

    @Singleton
    @Provides
    fun auth(): FirebaseAuth = Firebase.auth.apply {
        try {

            useEmulator(HOST, AUTH_PORT)
        } catch (_: IllegalStateException) { }
    }

    @Singleton
    @Provides
    fun firestore(): FirebaseFirestore = Firebase.firestore.apply {
        try {
            clearPersistence()
            useEmulator(HOST, FIRESTORE_PORT)
        } catch (_: IllegalStateException) { }
    }

    @Singleton
    @Provides
    fun storage(): FirebaseStorage = Firebase.storage.apply {
        try {
            useEmulator(HOST, STORAGE_PORT)
        } catch (_: IllegalStateException) { }
    }

    @Singleton
    @Provides
    fun user(): FirebaseUser? = auth().currentUser
}
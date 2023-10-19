/**
 * This file contains the Firebase module for dependency injection.
 */
package com.goofygoobers.geoart.di

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
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Firebase module that is installed in the SingletonComponent and injected with Hilt.
 * This module provides all Firebase instances that are used in the app.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    /**
     * Provides the Firebase Authentication instance as a singleton.
     *
     * @return The Firebase Authentication instance.
     */
    @Singleton
    @Provides
    fun auth(): FirebaseAuth = Firebase.auth

    /**
     * Provides the Firestore database instance as a singleton.
     *
     * @return The Firestore database instance.
     */
    @Singleton
    @Provides
    fun firestore(): FirebaseFirestore = Firebase.firestore

    /**
     * Provides the Firebase Storage instance as a singleton.
     *
     * @return The Firebase Storage instance.
     */
    @Singleton
    @Provides
    fun storage(): FirebaseStorage = Firebase.storage

    /**
     * Provides the current user as a singleton.
     *
     * @return The current user or null if there is no authenticated user.
     */
    @Singleton
    @Provides
    fun user(): FirebaseUser? = auth().currentUser
}

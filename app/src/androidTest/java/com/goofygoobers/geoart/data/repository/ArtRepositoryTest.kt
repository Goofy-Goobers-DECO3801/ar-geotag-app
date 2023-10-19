package com.goofygoobers.geoart.data.repository

import com.goofygoobers.geoart.createOrLogin
import com.goofygoobers.geoart.createTestArt
import com.goofygoobers.geoart.data.model.Art
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class ArtRepositoryTest {
    @get:Rule val hilt = HiltAndroidRule(this)

    @Inject lateinit var firestore: FirebaseFirestore

    @Inject lateinit var auth: FirebaseAuth

    @Inject lateinit var storage: FirebaseStorage

    @Inject lateinit var userRepository: UserRepository

    @Inject lateinit var artRepository: ArtRepository

    @Before
    fun setup() {
        hilt.inject()
        userRepository.createOrLogin()
    }

    @Test
    fun testNonExistentArt() =
        runTest {
            val result = artRepository.getById("Nope")
            assertThat(result).isNull()
        }

    @Test
    fun testCreateArt() =
        runTest {
            val art = artRepository.createTestArt()
            assertThat(art).isNotNull()
            assertThat(art.id).isNotEmpty()

            val artCmp = artRepository.getById(art.id)
            assertThat(artCmp).isNotNull()
            artCmp as Art
            assertThat(artCmp.timestamp).isNotNull()
            artCmp.timestamp = null
            assertThat(artCmp).isEqualTo(art)
            val artModel = storage.reference.child(art.storageRef).getBytes(1024 * 1024).await()
            assertThat(artModel).isEqualTo("geoARt".toByteArray())
        }
}

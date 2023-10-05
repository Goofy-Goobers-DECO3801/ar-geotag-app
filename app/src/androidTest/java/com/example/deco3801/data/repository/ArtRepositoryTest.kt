package com.example.deco3801.data.repository

import android.location.Location
import android.net.Uri
import android.os.FileUtils
import androidx.test.platform.app.InstrumentationRegistry
import com.example.deco3801.data.model.Art
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltAndroidTest
class ArtRepositoryTest {
    @get:Rule val hilt = HiltAndroidRule(this)
    @Inject lateinit var artRepository: ArtRepository
    @Inject lateinit var firestore: FirebaseFirestore
    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var storage: FirebaseStorage

    @Before
    fun setup() {
        hilt.inject()
        runBlocking {
            try {
                auth.createUserWithEmailAndPassword("test@test.com", "Password1").await()
            } catch (e: FirebaseAuthException) {
                auth.signInWithEmailAndPassword("test@test.com", "Password1").await()
            }
        }
    }

    @Test
    fun testNonExistentArt() = runTest {
        val result = artRepository.getById("Nope")
        assertThat(result).isNull()
    }

    @Test
    fun testCreateArt() = runTest {
        val location = Location("test")
        location.latitude = 27.234
        location.longitude = -122.2
        location.altitude = 60.0
        val tempFile = withContext(Dispatchers.IO) {
            File.createTempFile("art", ".txt")
        }
        tempFile.writeText("geoARt")
        val uri =  Uri.fromFile(tempFile)

        val art = artRepository.createArt(
            "Test art",
            "testing 123",
            location,
            uri,
            "art.txt"
            )
        assertThat(art).isNotNull()

        val artCmp = artRepository.getById(art.id)
        assertThat(artCmp).isNotNull()
        artCmp as Art
        assertThat(artCmp.timestamp).isNotNull()
        artCmp.timestamp = null
        assertThat(artCmp).isEqualTo(art)
        val artModel = storage.reference.child(art.storageRef).getBytes(1024 * 1024).await()
        assertThat(artModel).isEqualTo("geoARt".toByteArray())
    }

    @Test
    fun testCreateArtWithoutAccount() = runTest {
        val location = Location("test")
        location.latitude = 27.234
        location.longitude = -122.2
        location.altitude = 60.0
        val tempFile = withContext(Dispatchers.IO) {
            File.createTempFile("art", ".txt")
        }
        tempFile.writeText("geoARt")
        val uri =  Uri.fromFile(tempFile)

        val art = artRepository.createArt(
            "Test art",
            "testing 123",
            location,
            uri,
            "art.txt"
        )
        assertThat(art).isNull()
    }
}

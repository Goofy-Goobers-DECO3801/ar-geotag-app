package com.example.deco3801

import android.location.Location
import android.net.Uri
import com.example.deco3801.data.model.Art
import com.example.deco3801.data.repository.ArtRepository
import com.example.deco3801.di.TestFirebaseModule
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.junit.Assert.assertThrows
import java.io.File

fun createOrLogin(email: String = "test@test.com", password: String = "Password1"): Unit = runBlocking {
    try {
        TestFirebaseModule.auth().createUserWithEmailAndPassword(email, password).await()
    } catch (e: FirebaseAuthException) {
        TestFirebaseModule.auth().signInWithEmailAndPassword(email, password).await()
    }
}

fun ArtRepository.createTestArt(): Art = runBlocking {
    val location = Location("test")
    location.latitude = 27.234
    location.longitude = -122.2
    location.altitude = 60.0
    val tempFile = withContext(Dispatchers.IO) {
        File.createTempFile("art", ".txt")
    }

    tempFile.writeText("geoARt")
    val uri = Uri.fromFile(tempFile)

    return@runBlocking createArt(
        "Test art",
        "testing 123",
        location,
        uri,
        "art.txt"
    )
}

inline fun <reified T : Throwable> assertThrows(
    noinline executable: suspend () -> Unit
): T = assertThrows(T::class.java) {
    runBlocking {
        executable()
    }
}
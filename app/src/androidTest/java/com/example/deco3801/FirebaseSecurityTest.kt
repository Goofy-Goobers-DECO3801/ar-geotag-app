package com.example.deco3801

import com.example.deco3801.data.repository.ArtRepository
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
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
class FirebaseSecurityTest {
    @get:Rule val hilt = HiltAndroidRule(this)
    @Inject lateinit var firestore: FirebaseFirestore
    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var storage: FirebaseStorage
    @Inject lateinit var artRepo: ArtRepository

    @Before
    fun setup() {
        hilt.inject()
    }

    @Test
    fun testArtWriteUserProtected() = runTest {
        createOrLogin()
        val art = artRepo.createTestArt()
        auth.signOut()
        createOrLogin("hacker@hack.com", "Password1")
        val e = assertThrows<FirebaseFirestoreException> {
            artRepo.getCollectionRef().document(art.id).delete().await()
        }
        assertThat(e.code).isEqualTo(FirebaseFirestoreException.Code.PERMISSION_DENIED)

    }

    @Test
    fun testArtReadAuthorizedOnly() = runTest {
        createOrLogin()
        artRepo.createTestArt()
        auth.signOut()
        val e = assertThrows<FirebaseFirestoreException> {
            artRepo.getAll()
        }
        assertThat(e.code).isEqualTo(FirebaseFirestoreException.Code.PERMISSION_DENIED)

    }
}

package com.goofygoobers.geoart

import com.goofygoobers.geoart.data.repository.ArtRepository
import com.goofygoobers.geoart.data.repository.UserRepository
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
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

    @Inject lateinit var userRepo: UserRepository

    @Before
    fun setup() {
        hilt.inject()
        userRepo.createOrLogin()
    }

    @Test
    fun testFirestoreArtWriteUserOnly() =
        runTest {
            val art = artRepo.createTestArt()
            auth.signOut()
            userRepo.createOrLogin("hacker", "hacker@hack.com", "Password1")
            val e =
                assertThrows<FirebaseFirestoreException> {
                    artRepo.getCollectionRef().document(art.id).delete().await()
                }
            assertThat(e.code).isEqualTo(FirebaseFirestoreException.Code.PERMISSION_DENIED)
        }

    @Test
    fun testFirestoreArtReadAuthorizedOnly() =
        runTest {
            artRepo.createTestArt()
            auth.signOut()
            val e =
                assertThrows<FirebaseFirestoreException> {
                    artRepo.getAll()
                }
            assertThat(e.code).isEqualTo(FirebaseFirestoreException.Code.PERMISSION_DENIED)
        }

    @Test
    fun testStorageArtWriteUserOnly() =
        runTest {
            val art = artRepo.createTestArt()
            auth.signOut()
            userRepo.createOrLogin("hacker", "hacker@hack.com", "Password1")
            val e =
                assertThrows<StorageException> {
                    storage.reference.child(art.storageRef).delete().await()
                }
            assertThat(e.errorCode).isEqualTo(StorageException.ERROR_NOT_AUTHORIZED)
        }

    @Test
    fun testStorageArtReadAuthorizedOnly() =
        runTest {
            val art = artRepo.createTestArt()
            auth.signOut()
            val e =
                assertThrows<StorageException> {
                    storage.reference.child(art.storageRef).getBytes(1024 * 1024).await()
                }
            assertThat(e.errorCode).isEqualTo(StorageException.ERROR_NOT_AUTHORIZED)
        }

    @Test
    fun testFirestoreUserWriteUserOnly() =
        runTest {
            val uid = auth.uid!!
            auth.signOut()
            userRepo.createOrLogin("hacker", "hacker@hack.com", "Password1")
            val e =
                assertThrows<FirebaseFirestoreException> {
                    userRepo.getCollectionRef().document(uid).delete().await()
                }
            assertThat(e.code).isEqualTo(FirebaseFirestoreException.Code.PERMISSION_DENIED)
        }

    @Test
    fun testFirestoreUserReadAuthorizedOnly() =
        runTest {
            auth.signOut()
            val e =
                assertThrows<FirebaseFirestoreException> {
                    userRepo.getAll()
                }
            assertThat(e.code).isEqualTo(FirebaseFirestoreException.Code.PERMISSION_DENIED)
        }
}

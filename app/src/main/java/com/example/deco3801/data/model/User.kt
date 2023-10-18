/**
 * File containing the User model.
 *
 * @author Jed Willick
 */
package com.example.deco3801.data.model

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

/**
 * User model representing the structure of the document in Firestore.
 * Used for storing user-data and other information displayed on their profile page.
 */
@Serializable
data class User(
    @DocumentId
    val id: String = "",
    var username: String = "",
    var email: String = "",
    var fullname: String = "",
    @field:JvmField
    var isPrivate: Boolean = false,
    var pictureUri: String = "",
    var bio: String = "",
    var followerCount: Int = 0,
    var followingCount: Int = 0,
)

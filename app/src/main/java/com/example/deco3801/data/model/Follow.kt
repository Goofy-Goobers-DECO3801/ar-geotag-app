/**
 * File containing the Follow model.
 *
 * @author Jed Willick
 */
package com.example.deco3801.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Follow model representing the structure of the document in Firestore.
 * Used for storing data for users following other users.
 */
data class Follow(
    var followerId: String = "",
    var followingId: String = "",
    @ServerTimestamp
    var timestamp: Date? = null,
)

/**
 * File containing the Comment model.
 *
 * @author Jed Willick
 */
package com.example.deco3801.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Comment model representing the structure of the document in Firestore.
 * Used for storing all data for a Comment on an artwork.
 */
data class Comment(
    var artId: String = "",
    var userId: String = "",
    var text: String = "",
    @ServerTimestamp
    var timestamp: Date? = null,
)

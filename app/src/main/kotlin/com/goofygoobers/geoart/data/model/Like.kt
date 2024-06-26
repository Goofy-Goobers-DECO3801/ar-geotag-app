/**
 * File containing the Like model.
 */
package com.goofygoobers.geoart.data.model

/**
 * Like model representing the structure of the document in Firestore.
 * Used for storing data about a particular Like on an artwork.
 */
data class Like(
    var artId: String = "",
    var userId: String = "",
)

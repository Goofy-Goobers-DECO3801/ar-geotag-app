/**
 * File containing the Art model.
 */
package com.goofygoobers.geoart.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Art model representing the structure of the document in Firestore.
 * Used for storing all information pertinent to a particular artwork.
 */
data class Art(
    @DocumentId
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var altitude: Double = 0.0,
    // GeoFirestore needs l and g fields.
    @set:PropertyName("l")
    @get:PropertyName("l")
    var location: GeoPoint? = null,
    @set:PropertyName("g")
    @get:PropertyName("g")
    var geohash: String = "",
    @ServerTimestamp
    var timestamp: Date? = null,
    var userId: String = "",
    var storageUri: String = "",
    var storageRef: String = "",
    var likeCount: Int = 0,
    var commentCount: Int = 0,
    var reportCount: Int = 0,
)

package com.example.deco3801.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Art(
    var title: String = "",
    var description: String = "",
    var location: GeoPoint? = null,
    var altitude: Double? = null,
    @ServerTimestamp var timestamp: Date? = null,
    var userRef: String = "",
    var storageRef: String = "",
)


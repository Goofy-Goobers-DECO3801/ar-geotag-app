package com.example.deco3801.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Geotag(
    var latitude: Double,
    var longitude: Double,
    var altitude: Double,
    var geoHash: String,
)

data class Art(
    var title: String = "",
    var description: String = "",
    var geotag: Geotag? = null,
    @ServerTimestamp var timestamp: Date? = null,
    var userId: String = "",
    var storagePath: String = "",
)


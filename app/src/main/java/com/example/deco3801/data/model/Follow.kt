package com.example.deco3801.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Follow(
    var followerId: String = "",
    var followingId: String = "",
    @ServerTimestamp
    var timestamp: Date? = null,
)

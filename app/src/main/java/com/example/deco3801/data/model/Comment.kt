package com.example.deco3801.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Comment(
    var artId: String = "",
    var userId: String = "",
    var text: String = "",
    @ServerTimestamp
    var timestamp: Date? = null,
)

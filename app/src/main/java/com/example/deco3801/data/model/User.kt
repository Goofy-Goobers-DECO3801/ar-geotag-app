package com.example.deco3801.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String = "",
    var username: String = "",
    var email: String = "",
    @field:JvmField
    var isPrivate: Boolean = false,
    var pictureRef: String? = null,
    var bio: String = "",
)
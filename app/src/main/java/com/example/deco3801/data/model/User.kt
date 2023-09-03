package com.example.deco3801.data.model

data class User(
    var id: String = "",
    var username: String = "",
    var email: String = "",
    @field:JvmField
    var isPrivate: Boolean = false,
    var pictureRef: String? = null,
    var bio: String = "",
)
package com.example.deco3801.model

data class User(
    var username: String ="",
    var email: String = "",
    @field:JvmField
    var isPrivate: Boolean = false,
)
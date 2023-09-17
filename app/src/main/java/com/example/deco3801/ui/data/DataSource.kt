package com.example.deco3801.ui.data

import com.example.deco3801.R
import com.example.deco3801.ui.model.ProfilePost

object DataSource {
    val profiles = listOf(
        ProfilePost(R.string.artwork_title, 20, 8, R.string.location, R.string.date, R.drawable.default_img),
        ProfilePost(R.string.artwork_title, 38, 11, R.string.location, R.string.date, R.drawable.default_img),
        ProfilePost(R.string.artwork_title, 16, 3, R.string.location, R.string.date, R.drawable.default_img),
        ProfilePost(R.string.artwork_title, 39, 14, R.string.location, R.string.date, R.drawable.default_img)
    )
}
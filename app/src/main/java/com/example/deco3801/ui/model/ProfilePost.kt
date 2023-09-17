package com.example.deco3801.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class ProfilePost (
    @StringRes val artworkTitle: Int,
    val likes: Int,
    val reviews: Int,
    @StringRes val location: Int,
    @StringRes val date: Int,
    @DrawableRes val imagePreview: Int
)

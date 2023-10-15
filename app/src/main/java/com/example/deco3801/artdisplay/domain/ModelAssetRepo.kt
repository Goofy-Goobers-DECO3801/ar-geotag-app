package com.example.deco3801.artdisplay.domain

// AR screen was modelled after below sample
// Blizl, “Blizl/sceneview-android,” 21 September 2023. [Online]. Available: https://github.com/Blizl/sceneview-android/tree/blizl/ecommerce-compose-mvvm-app.

interface ModelAssetRepo {
    suspend fun fetchAsset(artAddress: String): String
}

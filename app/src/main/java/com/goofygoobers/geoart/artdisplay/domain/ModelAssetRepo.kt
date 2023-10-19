package com.goofygoobers.geoart.artdisplay.domain

// AR screen was modelled after below sample
// Blizl, “Blizl/sceneview-android,” 21 September 2023. [Online]. Available: https://github.com/Blizl/sceneview-android/tree/blizl/ecommerce-compose-mvvm-app.

/*
 * Model Asset Repository Interface
 */
interface ModelAssetRepo {
    /*
     * Returns the URI string address of the art model
     */
    suspend fun fetchAsset(artAddress: String): String
}

package com.example.deco3801.artdisplay.data

import com.example.deco3801.artdisplay.domain.ModelAssetRepo

// AR screen was modelled after below sample
// Blizl, “Blizl/sceneview-android,” 21 September 2023. [Online]. Available: https://github.com/Blizl/sceneview-android/tree/blizl/ecommerce-compose-mvvm-app.

/*
 * Model Asset Repository Concrete Class
 */
class ModelAssetRepoImpl : ModelAssetRepo {
    /*
     * Returns the URI string address of the art model
     */
    override suspend fun fetchAsset(artAddress: String): String {
        // This is trivial
        // I think we should keep this fetch structure in case
        return artAddress
    }
}

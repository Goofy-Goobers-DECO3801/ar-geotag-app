package com.example.deco3801.artdisplay.data

import com.example.deco3801.artdisplay.domain.ModelAssetRepo
import kotlinx.coroutines.delay

// AR screen was modelled after below sample
// Blizl, “Blizl/sceneview-android,” 21 September 2023. [Online]. Available: https://github.com/Blizl/sceneview-android/tree/blizl/ecommerce-compose-mvvm-app.
class ModelAssetRepoImpl : ModelAssetRepo {
    override suspend fun fetchAsset(artAddress: String): String {
        // This is trivial
        // I think we should keep this fetch structure in case
        return artAddress
    }

}

package com.example.deco3801.artdisplay.data

import com.example.deco3801.artdisplay.domain.ModelAssetRepo
import kotlinx.coroutines.delay

class ModelAssetRepoImpl : ModelAssetRepo {
    override suspend fun fetchAsset(artAddress: String): String {
        // This is trivial
        // I think we should keep this fetch structure in case
        return artAddress
    }

}
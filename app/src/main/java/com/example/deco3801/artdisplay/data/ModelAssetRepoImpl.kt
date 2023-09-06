package com.example.deco3801.artdisplay.data

import com.example.deco3801.artdisplay.domain.ModelAssetRepo
import kotlinx.coroutines.delay

class ModelAssetRepoImpl : ModelAssetRepo {
    override suspend fun fetchAsset(artID: Int): String {
        // TODO("replace trivial implementation")
        delay(1000)
        return "models/sofa.glb"
    }

}
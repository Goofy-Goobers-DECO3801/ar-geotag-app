package com.example.deco3801.artdisplay.domain

interface ModelAssetRepo {
    suspend fun fetchAsset(artID: Int): String
}
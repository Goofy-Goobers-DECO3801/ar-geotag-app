package com.example.deco3801.artdisplay.domain

interface ModelAssetRepo {
    suspend fun fetchAsset(artAddress: String): String
}
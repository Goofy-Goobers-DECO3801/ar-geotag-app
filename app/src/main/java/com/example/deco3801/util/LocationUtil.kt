package com.example.deco3801.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.example.deco3801.data.model.Geotag
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

object LocationUtil {
    suspend fun getCurrentLocation(context: Context): Location? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        return fusedLocationClient.lastLocation.await()
    }
}

fun Location.toGeotag(): Geotag {
    return Geotag(
        this.latitude,
        this.longitude,
        this.altitude,
        GeoFireUtils.getGeoHashForLocation(
            GeoLocation(
                this.latitude,
                this.longitude,
            )
        ),
    )
}

fun Location.toGeoLocation(): GeoLocation {
    return GeoLocation(this.latitude, this.longitude)
}
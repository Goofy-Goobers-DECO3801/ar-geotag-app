package com.example.deco3801.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await
import org.imperiumlabs.geofirestore.GeoLocation


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

fun Location.toGeoPoint(): GeoPoint {
    return GeoPoint(this.latitude, this.longitude)
}

fun Location.toGeoLocation(): GeoLocation {
    return GeoLocation(this.latitude, this.longitude)
}

fun Location.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

fun GeoPoint.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}
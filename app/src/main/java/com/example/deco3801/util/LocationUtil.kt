package com.example.deco3801.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await
import org.imperiumlabs.geofirestore.GeoLocation
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow


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

fun CameraPosition.toRadius(): Double {
    // From the stackoverflow gods https://gis.stackexchange.com/a/127949
    return 156543.03392 * cos(this.target.latitude * PI / 180) / 2f.pow(this.zoom)
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

fun LatLng.toGeoPoint(): GeoPoint {
    return GeoPoint(this.latitude, this.longitude)
}
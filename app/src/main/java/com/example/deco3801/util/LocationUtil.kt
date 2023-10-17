package com.example.deco3801.util

import android.Manifest
import android.annotation.SuppressLint
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

@SuppressLint("MissingPermission")
suspend fun Context.getCurrentLocation(): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    return if (this.hasLocationPermissions()) fusedLocationClient.lastLocation.await() else null
}

fun Context.hasLocationPermissions() = ActivityCompat.checkSelfPermission(
    this,
    Manifest.permission.ACCESS_FINE_LOCATION,
) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
    this,
    Manifest.permission.ACCESS_COARSE_LOCATION,
) == PackageManager.PERMISSION_GRANTED


/**
 * @reference
 * G. Mayani, "What ratio scales do Google Maps zoom levels correspond to?,"
 * Geographic Information Systems Stack Exchange, Aug. 19, 2020.
 * https://gis.stackexchange.com/a/127949 (accessed Oct. 16, 2023).
 */
fun CameraPosition.toRadius(): Double {
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

fun GeoPoint.toGeoLocation(): GeoLocation {
    return GeoLocation(this.latitude, this.longitude)
}

fun LatLng.toGeoPoint(): GeoPoint {
    return GeoPoint(this.latitude, this.longitude)
}

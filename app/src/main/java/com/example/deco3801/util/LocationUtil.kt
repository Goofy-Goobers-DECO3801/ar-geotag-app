/**
 * Contains utility functions for location related operations.
 */
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

/**
 * Uses Fused Location Provider to get the last location of the user.
 *
 * @return The last location of the user.
 */
@SuppressLint("MissingPermission") // We check for permissions in a function call.
suspend fun Context.getCurrentLocation(): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    return if (this.hasLocationPermissions()) fusedLocationClient.lastLocation.await() else null
}

/**
 * Checks if the app has location permissions.
 */
fun Context.hasLocationPermissions() =
    ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED

/**
 * Convert a [CameraPosition] to a radius in meters.
 *
 * @reference
 * G. Mayani and sergiuteaca, "What ratio scales do Google Maps zoom levels correspond to?,"
 * Geographic Information Systems Stack Exchange, 19 August 2020. \[Online].
 * Available: https://gis.stackexchange.com/a/127949. [Accessed 10 September 2023].
 */
fun CameraPosition.toRadius(): Double {
    return 156543.03392 * cos(this.target.latitude * PI / 180) / 2f.pow(this.zoom)
}

/**
 * Convert a [Location] to a [GeoPoint].
 */
fun Location.toGeoPoint(): GeoPoint {
    return GeoPoint(this.latitude, this.longitude)
}

/**
 * Convert a [Location] to a [GeoLocation].
 */
fun Location.toGeoLocation(): GeoLocation {
    return GeoLocation(this.latitude, this.longitude)
}

/**
 * Convert a [Location] to a [LatLng].
 */
fun Location.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

/**
 * Convert a [GeoPoint] to a [LatLng].
 */
fun GeoPoint.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

/**
 * Convert a [GeoPoint] to a [GeoLocation].
 */
fun GeoPoint.toGeoLocation(): GeoLocation {
    return GeoLocation(this.latitude, this.longitude)
}

/**
 * Convert a [LatLng] to a [GeoPoint].
 */
fun LatLng.toGeoPoint(): GeoPoint {
    return GeoPoint(this.latitude, this.longitude)
}

package com.example.deco3801.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.deco3801.util.getCurrentLocation
import com.example.deco3801.util.hasLocationPermissions
import com.example.deco3801.util.toGeoPoint
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.GeoPoint
import java.util.Locale
import java.util.concurrent.TimeUnit

private const val LOCATION_TAG = "LOCATION"

/**
 * @reference
 * The Android Open Source Project, "LocationUpdateScreen.kt," Android, 16 May 2023. \[Online].
 * Available: https://github.com/android/platform-samples/blob/main/samples/location/src/main/java/com/example/platform/location/locationupdates/LocationUpdatesScreen.kt.
 * [Accessed 10 September 2023].
 */
@SuppressLint("MissingPermission")
@Composable
fun GetUserLocation(
    onChange: (Location?) -> Unit,
    delayInSeconds: Long = 5,
) {
    val context = LocalContext.current
    val currentOnChange by rememberUpdatedState(newValue = onChange)
    LaunchedEffect(Unit) { // This is much faster for the first time
        currentOnChange(context.getCurrentLocation())
    }

    if (!context.hasLocationPermissions()) {
        return
    }

    DisposableEffect(Unit) {
        // The Fused Location Provider provides access to location APIs.
        val locationProvider = LocationServices.getFusedLocationProviderClient(context)

        val locationCallback: LocationCallback =
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    // Run the callback with the user's new location
                    currentOnChange(result.lastLocation)
                    Log.d(LOCATION_TAG, "${result.lastLocation}")
                }
            }

        // Create a location request for precise location every 3 seconds
        val locationRequest =
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                TimeUnit.SECONDS.toMillis(delayInSeconds),
            ).build()

        // Send the location request while we are in the composition
        locationProvider.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper(),
        )

        onDispose {
            // Removes all location updates for the given callback.
            locationProvider.removeLocationUpdates(locationCallback)
        }
    }
}

/**
 * @reference
 * E. Yulianto, "Geocoder - getFromLocation() Deprecated," Stackoverflow, 25 October 2022. \[Online].
 * Available: https://stackoverflow.com/a/74160903. [Accessed 11 October 2023].
 */
@Suppress("DEPRECATION") // Need to support older APIs
@Composable
fun GetLocationName(
    location: GeoPoint?,
    onLocationName: (String) -> Unit,
    fullAddress: Boolean = false,
) {
    fun getLocationName(address: Address?) {
        if (address == null) {
            return
        }
        onLocationName(
            if (fullAddress) {
                address.getAddressLine(0)
            } else {
                "${address.locality}, ${address.adminArea}"
            }
        )

    }

    val context = LocalContext.current
    val gcd = Geocoder(context, Locale.getDefault())

    LaunchedEffect(location) {
        if (location == null) {
            return@LaunchedEffect
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gcd.getFromLocation(location.latitude, location.longitude, 1) {
                getLocationName(it.firstOrNull())
            }
            return@LaunchedEffect
        }
        try {
            getLocationName(
                gcd.getFromLocation(location.latitude, location.longitude, 1)
                    ?.firstOrNull()
            )
        } catch (e: Exception) {
            SnackbarManager.showError(e)
        }
    }
}

@Composable
fun GetLocationName(
    location: Location?,
    onLocationName: (String) -> Unit,
    fullAddress: Boolean = false,
) {
    return GetLocationName(
        location = location?.toGeoPoint(),
        onLocationName = onLocationName,
        fullAddress = fullAddress,
    )
}

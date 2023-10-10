package com.example.deco3801.ui.components

import android.Manifest
import android.content.pm.PackageManager
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

@Composable
fun GetUserLocation(
    onChange: (Location?) -> Unit,
    delayInSeconds: Long = 5,
) {
    val context = LocalContext.current
    val currentOnChange by rememberUpdatedState(newValue = onChange)

    // Check for permissions
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) != PackageManager.PERMISSION_GRANTED
    ) {
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

@Composable
fun GetLocationName(
    location: GeoPoint?,
    onLocationName: (String) -> Unit,
    fullAddress: Boolean = false,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return
    }

    val context = LocalContext.current
    val gcd = Geocoder(context, Locale.getDefault())

    LaunchedEffect(location) {
        if (location != null) {
            gcd.getFromLocation(location.latitude, location.longitude, 1) {
                if (it.size == 0) {
                    return@getFromLocation
                }

                onLocationName(
                    if (fullAddress) {
                        it[0].getAddressLine(0)
                    } else {
                        "${it[0].locality}, ${it[0].adminArea}"
                    }
                )
            }
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

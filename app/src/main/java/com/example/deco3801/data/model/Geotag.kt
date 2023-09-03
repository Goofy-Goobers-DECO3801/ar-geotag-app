package com.example.deco3801.data.model

import android.location.Location
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation

data class Geotag(
    var latitude: Double,
    var longitude: Double,
    var altitude: Double,
    var geoHash: String,
)

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
package com.example.deco3801.directions.data.remote.dto

import com.example.deco3801.directions.domain.model.OverviewPolyline
import com.google.android.gms.maps.model.LatLng

data class OverviewPolylineDto(
    val points: String
){
    fun toOverviewPolyline(): OverviewPolyline{
        return OverviewPolyline(
            points = points
        )
    }
}
package com.example.deco3801.util

import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

inline fun <T> Iterable<T>.forEachOrElse(
    orElse: () -> Unit = {},
    action: (T) -> Unit,
): Unit {
    return if (this.none()) orElse() else this.forEach(action)
}

fun formatDistance(distanceInM: Double): String {
    return when {
        distanceInM >= 1000 -> String.format("%.2fkm", distanceInM / 1000)
        else -> {
            String.format("%.0fm", distanceInM)
        }
    }
}

fun formatDate(date: Date?): String {
    date ?: return ""

    val now = Date()
    val diff = now.time - date.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "just now"
        minutes < 60 -> "$minutes ${if (minutes.toInt() == 1) "minute" else "minutes"} ago"
        hours < 24 -> "$hours ${if (hours.toInt() == 1) "hour" else "hours"} ago"
        days < 7 -> "$days ${if (days.toInt() == 1) "day" else "days"} ago"
        else -> {
            val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(now)
            val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
            if (currentYear == year) {
                SimpleDateFormat("dd MMMM", Locale.getDefault()).format(date)
            } else {
                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
            }
        }
    }
}

fun Context.getGoogleApiKey(): String? {
    return (
        this.packageManager
            .getApplicationInfo(this.packageName, PackageManager.GET_META_DATA)
            .metaData
            .getString("com.google.android.geo.API_KEY")
        )
}

/**
 * General utility functions.
 */
package com.goofygoobers.geoart.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * If the iterable is empty, run the [orElse] function, otherwise run the [action] function.
 *
 * @see [forEach]
 */
inline fun <T> Iterable<T>.forEachOrElse(
    orElse: () -> Unit = {},
    action: (T) -> Unit,
) {
    return if (this.none()) orElse() else this.forEach(action)
}

/**
 * Applies the [action] function to each element in the iterable.
 *
 * @see [forEach]
 */
inline fun <T> Iterable<T>.forEachApply(action: T.() -> Unit) {
    return this.forEach {
        it.apply(action)
    }
}

/**
 * Formats a [distanceInM] in meters to a human readable format.
 */
fun formatDistance(distanceInM: Double): String {
    return when {
        distanceInM >= 1000 -> String.format("%.2fkm", distanceInM / 1000)
        else -> {
            String.format("%.0fm", distanceInM)
        }
    }
}

/**
 * Formats a [date] to a human readable format.
 *
 * The format closely follows the format used by popular social media apps like Instagram and Twitter,
 * that uses relative time for recent dates, and absolute time for older dates.
 */
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

/**
 * Gets the Google API key from the manifest.
 */
fun Context.getGoogleApiKey(): String? {
    return (
        this.packageManager
            .getApplicationInfo(this.packageName, PackageManager.GET_META_DATA)
            .metaData
            .getString("com.google.android.geo.API_KEY")
    )
}

/**
 * Resizes the given resource with [resId] to the given [width] and [height].
 *
 * @param context The context.
 * @param resId The resource ID of the original bitmap.
 * @param width The new width.
 * @param height The new height.
 *
 * @see [BitmapFactory.decodeResource]
 * @see [Bitmap.createScaledBitmap]
 * @reference
 * Taochok, "How to Resize a Bitmap in Android?," Stackoverflow, 6 March 2015. \[Online].
 * Available: https://stackoverflow.com/a/28893299. [Accessed 18 October 2023].
 */
fun resizeBitmap(
    context: Context,
    resId: Int,
    width: Int,
    height: Int,
): Bitmap {
    val imageBitmap =
        BitmapFactory.decodeResource(
            context.resources,
            resId,
        )
    return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
}

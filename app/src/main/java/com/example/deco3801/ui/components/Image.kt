/**
 * Composable functions for displaying images.
 */
package com.example.deco3801.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter

/**
 * Composable function for displaying an async image that can be clicked to expand.
 *
 * @see [AsyncImage] for parameters.
 */
@Composable
fun ExpandableAsyncImage(
    // Either an ImageRequest or ImageRequest.data (passed to AsyncImage)
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Painter? = null,
    error: Painter? = null,
    fallback: Painter? = error,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
) {
    var expandImage by remember { mutableStateOf(false) }

    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        placeholder = placeholder,
        modifier = modifier
            .clickable {
                expandImage = true
            },
        fallback = fallback,
        onLoading = onLoading,
        onSuccess = onSuccess,
        onError = onError,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality
    )

    if (expandImage) {
        Dialog(
            onDismissRequest = { expandImage = false }
        ) {
            // Content of the enlarged image here
            AsyncImage(
                model = model,
                contentDescription = contentDescription,
                placeholder = placeholder,
                fallback = fallback,
                onLoading = onLoading,
                onSuccess = onSuccess,
                onError = onError,
                alignment = alignment,
                contentScale = ContentScale.Fit,
                alpha = alpha,
                colorFilter = colorFilter,
                filterQuality = filterQuality
            )
        }
    }
}

package com.vcam.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.vcam.VCamApplication
import com.vcam.color.Filter
import com.vcam.theme.VColors

@Composable
fun FilterThumb(
    filter: Filter,
    size: Dp,
    cornerRadius: Dp,
    photoKind: PhotoKind,
    modifier: Modifier = Modifier,
) {
    val app = LocalContext.current.applicationContext as VCamApplication
    val bitmap by produceState<android.graphics.Bitmap?>(initialValue = null, key1 = filter.id) {
        value = app.thumbnailRenderer.thumbnailFor(filter)
    }
    Box(
        modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(VColors.Ink12)
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = filter.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

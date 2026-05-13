package com.vcam.ui.filters.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vcam.VCamApplication
import com.vcam.color.Filter
import com.vcam.theme.VColors
import com.vcam.theme.VType

@Composable
fun HeroPreview(
    filter: Filter,
    intensity: Int,
    modifier: Modifier = Modifier,
) {
    val app = LocalContext.current.applicationContext as VCamApplication
    val bitmap by produceState<android.graphics.Bitmap?>(initialValue = null, key1 = filter.id) {
        value = app.thumbnailRenderer.thumbnailFor(filter)
    }
    Box(
        modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(16.dp))
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
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .graphicsLayer { alpha = (100 - intensity).coerceIn(0, 100) / 300f }
        )

        Box(
            Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(VColors.DarkGlass55)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text("HOLD TO COMPARE", style = VType.Mono, color = Color.White)
        }

        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
        ) {
            Text(
                "${filter.category.displayName.uppercase()} · ${filter.shortCode}",
                style = VType.Mono,
                color = Color.White.copy(alpha = 0.85f),
            )
            Text(
                filter.displayName,
                style = VType.HeroDisplay,
                color = Color.White,
            )
        }
    }
}

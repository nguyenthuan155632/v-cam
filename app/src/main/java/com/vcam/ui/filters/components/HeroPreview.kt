package com.vcam.ui.filters.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.vcam.data.Filter
import com.vcam.theme.VColors
import com.vcam.theme.VType
import com.vcam.ui.components.PhotoKind
import com.vcam.ui.components.photoBrush

@Composable
fun HeroPreview(
    filter: Filter,
    intensity: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(16.dp))
            .background(photoBrush(PhotoKind.Pancakes))
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(filter.tintTop, filter.tintBottom)))
                .graphicsLayer { alpha = intensity / 100f }
        )

        // HOLD TO COMPARE pill, top-right
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

        // Filter name / code, bottom-left
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
        ) {
            Text(
                "${filter.category.uppercase()} · ${filter.code}",
                style = VType.Mono,
                color = Color.White.copy(alpha = 0.85f),
            )
            Text(
                filter.name,
                style = VType.HeroDisplay,
                color = Color.White,
            )
        }
    }
}


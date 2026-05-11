package com.vcam.ui.filters.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vcam.data.Filter
import com.vcam.theme.VColors
import com.vcam.theme.VType
import com.vcam.ui.components.photoBrush
import com.vcam.ui.components.photoKindAt

@Composable
fun FilterGrid(
    filters: List<Filter>,
    activeIndex: Int,
    accent: Color,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
    ) {
        itemsIndexed(filters, key = { _, f -> f.id }) { i, f ->
            val active = i == activeIndex
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(photoBrush(photoKindAt(i)))
                            .clickable { onSelect(i) }
                    ) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(f.tintTop, f.tintBottom)))
                                .graphicsLayer { alpha = 0.85f }
                        )
                    }
                    if (active) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .border(2.dp, accent, RoundedCornerShape(14.dp))
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    f.name,
                    style = VType.Caption.copy(fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium),
                    color = if (active) VColors.Ink else VColors.Ink70,
                )
                Text(
                    f.code,
                    style = VType.MonoXSmall,
                    color = VColors.Ink30,
                )
            }
        }
    }
}

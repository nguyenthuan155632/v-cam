package com.vcam.ui.camera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.vcam.data.Filter
import com.vcam.ui.components.photoBrush
import com.vcam.ui.components.photoKindAt

enum class RibbonVariant { Circle, Square, Pill }

@Composable
fun FilterRibbon(
    filters: List<Filter>,
    activeIndex: Int,
    accent: Color,
    variant: RibbonVariant,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(activeIndex) {
        if (activeIndex in filters.indices) listState.animateScrollToItem(activeIndex)
    }
    val baseSize = if (variant == RibbonVariant.Pill) 52 else 44
    val cornerDp = when (variant) {
        RibbonVariant.Circle -> 22
        RibbonVariant.Pill -> 12
        RibbonVariant.Square -> 8
    }
    LazyRow(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
    ) {
        itemsIndexed(filters, key = { _, f -> f.id }) { i, f ->
            val active = i == activeIndex
            Box(
                Modifier
                    .padding(end = 10.dp)
                    .size((baseSize + 4).dp)
                    .clickable { onSelect(i) },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .size(baseSize.dp)
                        .graphicsLayer {
                            scaleX = if (active) 1.12f else 1f
                            scaleY = if (active) 1.12f else 1f
                        }
                        .clip(RoundedCornerShape(cornerDp.dp))
                        .background(photoBrush(photoKindAt(i)))
                ) {
                    Box(
                        Modifier
                            .matchParentSize()
                            .background(Brush.linearGradient(listOf(f.tintTop, f.tintBottom)))
                            .graphicsLayer { alpha = 0.85f }
                    )
                }
                if (active) {
                    Box(
                        Modifier
                            .size((baseSize + 4).dp)
                            .border(2.dp, accent, RoundedCornerShape((cornerDp + 2).dp))
                    )
                }
            }
        }
    }
}

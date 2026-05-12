package com.vcam.ui.camera.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vcam.VCamApplication
import com.vcam.color.Filter
import com.vcam.theme.VColors
import com.vcam.theme.VType

enum class RibbonVariant { Circle, Square, Pill }

@Composable
fun FilterRibbon(
    filters: List<Filter>,
    activeId: String,
    accent: Color,
    variant: RibbonVariant,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LocalContext.current.applicationContext as VCamApplication
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
    ) {
        items(filters, key = { it.id }) { filter ->
            val active = filter.id == activeId
            val shape = when (variant) {
                RibbonVariant.Circle -> CircleShape
                RibbonVariant.Pill -> RoundedCornerShape(12.dp)
                RibbonVariant.Square -> RoundedCornerShape(8.dp)
            }
            val bitmap by produceState<android.graphics.Bitmap?>(initialValue = null, key1 = filter.id) {
                value = app.thumbnailRenderer.thumbnailFor(filter)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .size(56.dp)
                        .clip(shape)
                        .background(VColors.Ink12)
                        .clickable { onSelect(filter.id) },
                    contentAlignment = Alignment.Center,
                ) {
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = filter.displayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    if (active) {
                        Box(Modifier.fillMaxSize().border(2.dp, accent, shape))
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = filter.shortCode,
                    style = VType.MonoLarge,
                    color = if (active) accent else VColors.White65,
                )
            }
        }
    }
}

package com.vcam.ui.filters.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vcam.VCamApplication
import com.vcam.color.Filter
import com.vcam.theme.VColors
import com.vcam.theme.VType

@Composable
fun FilterGrid(
    filters: List<Filter>,
    activeId: String,
    accent: Color,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LocalContext.current.applicationContext as VCamApplication
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
    ) {
        items(filters, key = { it.id }) { filter ->
            val active = filter.id == activeId
            val bitmap by produceState<android.graphics.Bitmap?>(initialValue = null, key1 = filter.id) {
                value = app.thumbnailRenderer.thumbnailFor(filter)
            }
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
                            .background(VColors.Ink12)
                            .clickable { onSelect(filter.id) }
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
                    filter.displayName,
                    style = VType.Caption.copy(fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium),
                    color = if (active) VColors.Ink else VColors.Ink70,
                )
                Text(
                    filter.shortCode,
                    style = VType.MonoXSmall,
                    color = VColors.Ink30,
                )
            }
        }
    }
}

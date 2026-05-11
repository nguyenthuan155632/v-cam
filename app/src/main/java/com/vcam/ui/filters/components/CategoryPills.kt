package com.vcam.ui.filters.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vcam.theme.VColors
import com.vcam.theme.VType

@Composable
fun CategoryPills(
    categories: List<String>,
    active: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
    ) {
        items(categories) { c ->
            val isActive = c == active
            Box(
                Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (isActive) VColors.Ink else VColors.Ink06)
                    .clickable { onSelect(c) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    c,
                    style = VType.SecondarySmall.copy(
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                    ),
                    color = if (isActive) VColors.Paper else VColors.Ink70,
                )
            }
        }
    }
}

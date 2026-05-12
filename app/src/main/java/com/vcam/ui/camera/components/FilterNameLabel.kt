package com.vcam.ui.camera.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.vcam.color.Filter
import com.vcam.theme.VColors
import com.vcam.theme.VType
import com.vcam.ui.icons.VIcons

@Composable
fun FilterNameLabel(
    filter: Filter,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            VIcons.ChevronRight,
            contentDescription = "Previous filter",
            tint = VColors.White65.copy(alpha = 0.5f),
            modifier = Modifier
                .size(14.dp)
                .graphicsLayer { rotationZ = 180f }
                .clickable { onPrev() },
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                filter.displayName,
                style = VType.HeroDisplaySmall,
                color = Color.White,
            )
            Text(
                "${filter.category.displayName.uppercase()} · ${filter.shortCode}",
                style = VType.Mono,
                color = VColors.White65,
            )
        }
        Icon(
            VIcons.ChevronRight,
            contentDescription = "Next filter",
            tint = VColors.White65.copy(alpha = 0.5f),
            modifier = Modifier
                .size(14.dp)
                .clickable { onNext() },
        )
    }
}

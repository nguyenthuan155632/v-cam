package com.vcam.ui.camera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vcam.theme.VColors
import com.vcam.theme.VType

/** Translucent chip floating over the dark camera preview. */
@Composable
fun CameraChip(
    text: String,
    active: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val bg = if (active) VColors.Beige else VColors.White14
    val fg = if (active) VColors.Ink else VColors.White95
    val w = if (active) FontWeight.SemiBold else FontWeight.Medium
    Box(
        modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            style = VType.SecondarySmall.copy(fontWeight = w),
            color = fg,
        )
    }
}

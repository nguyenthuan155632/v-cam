package com.vcam.ui.camera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vcam.theme.VColors
import com.vcam.ui.components.PhotoKind
import com.vcam.ui.components.photoBrush
import com.vcam.ui.icons.VIcons

@Composable
fun CameraBottomRow(
    accent: Color,
    onGallery: () -> Unit,
    onFlip: () -> Unit,
    shutter: @Composable () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.5.dp, VColors.White85, RoundedCornerShape(12.dp))
                .background(photoBrush(PhotoKind.Sushi))
                .clickable { onGallery() }
        )
        shutter()
        CameraIconButton(
            icon = VIcons.Flip,
            contentDescription = "Flip camera",
            onClick = onFlip,
            sizeDp = 44,
            accent = accent,
        )
    }
}

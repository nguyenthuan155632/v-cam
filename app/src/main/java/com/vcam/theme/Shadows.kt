package com.vcam.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object VShadows {
    val RestingCardElevation: Dp = 4.dp
    val GlassOverPreviewElevation: Dp = 12.dp
    val SliderThumbElevation: Dp = 2.dp
}

@Composable
fun Modifier.restingCardShadow(shape: Shape = RoundedCornerShape(16.dp)): Modifier =
    this.shadow(
        elevation = VShadows.RestingCardElevation,
        shape = shape,
        ambientColor = Color(0x1415110E),
        spotColor = Color(0x1F15110E),
    )

@Composable
fun Modifier.glassOverPreviewShadow(shape: Shape = RoundedCornerShape(20.dp)): Modifier =
    this.shadow(
        elevation = VShadows.GlassOverPreviewElevation,
        shape = shape,
        ambientColor = Color(0x80000000),
        spotColor = Color(0x80000000),
    )

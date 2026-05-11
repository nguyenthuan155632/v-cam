package com.vcam.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object VShapes {
    val Pill = RoundedCornerShape(999.dp)

    val GlassCard = RoundedCornerShape(20.dp)
    val GlassSheet = RoundedCornerShape(16.dp)
    val GlassDock = RoundedCornerShape(26.dp)

    val Card = RoundedCornerShape(16.dp)
    val CardSmall = RoundedCornerShape(14.dp)

    val ThumbSquare = RoundedCornerShape(8.dp)
    val ThumbSquareLarge = RoundedCornerShape(12.dp)
    val ThumbPill = RoundedCornerShape(12.dp)
    val ThumbCircle = RoundedCornerShape(22.dp)

    val Gallery = RoundedCornerShape(12.dp)

    val PhoneFrame = RoundedCornerShape(38.dp)

    val FocusRect = RoundedCornerShape(4.dp)

    val SegmentedTrack = RoundedCornerShape(10.dp)
    val SegmentedThumb = RoundedCornerShape(8.dp)
}

val VMaterialShapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(26.dp),
)

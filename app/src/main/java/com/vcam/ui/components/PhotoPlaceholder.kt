package com.vcam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class PhotoKind { Pancakes, Pasta, Coffee, Salad, Sushi, Croissant, Cocktail, Street, Portrait }

fun photoBrush(kind: PhotoKind): Brush = when (kind) {
    PhotoKind.Pancakes -> Brush.radialGradient(
        0.00f to Color(0xFFF4D28A),
        0.35f to Color(0xFFD99A52),
        0.70f to Color(0xFF8A4F24),
        1.00f to Color(0xFF3A1C0C),
    )
    PhotoKind.Pasta -> Brush.radialGradient(
        0.00f to Color(0xFFF6CF85),
        0.30f to Color(0xFFD97F47),
        0.60f to Color(0xFFA64224),
        1.00f to Color(0xFF4A1408),
    )
    PhotoKind.Coffee -> Brush.radialGradient(
        0.00f to Color(0xFFC08A5A),
        0.50f to Color(0xFF6E3E22),
        1.00f to Color(0xFF2A1308),
    )
    PhotoKind.Salad -> Brush.radialGradient(
        0.00f to Color(0xFFC3D97A),
        0.45f to Color(0xFF6EA14A),
        1.00f to Color(0xFF2D4E1C),
    )
    PhotoKind.Sushi -> Brush.radialGradient(
        0.00f to Color(0xFFF4E3B8),
        0.35f to Color(0xFFC98259),
        0.70f to Color(0xFF6B2A18),
        1.00f to Color(0xFF1A0805),
    )
    PhotoKind.Croissant -> Brush.radialGradient(
        0.00f to Color(0xFFF6D68A),
        0.40f to Color(0xFFD8995A),
        0.75f to Color(0xFF7A4022),
        1.00f to Color(0xFF2C1208),
    )
    PhotoKind.Cocktail -> Brush.radialGradient(
        0.00f to Color(0xFFFFC36A),
        0.40f to Color(0xFFD96245),
        0.75f to Color(0xFF7D2230),
        1.00f to Color(0xFF1A0510),
    )
    PhotoKind.Street -> Brush.verticalGradient(
        0.00f to Color(0xFF1A1626),
        0.40f to Color(0xFF3A2030),
        0.75f to Color(0xFFC0594A),
        1.00f to Color(0xFFF4A566),
    )
    PhotoKind.Portrait -> Brush.radialGradient(
        0.00f to Color(0xFFF7D4B2),
        0.40f to Color(0xFFD99A72),
        0.75f to Color(0xFF8A4F3A),
        1.00f to Color(0xFF2A140A),
    )
}

val photoKindCycle = listOf(
    PhotoKind.Pancakes, PhotoKind.Pasta, PhotoKind.Coffee, PhotoKind.Salad,
    PhotoKind.Sushi, PhotoKind.Croissant, PhotoKind.Cocktail, PhotoKind.Street,
    PhotoKind.Portrait,
)

fun photoKindAt(i: Int): PhotoKind = photoKindCycle[i % photoKindCycle.size]

@Composable
fun PhotoPlaceholder(kind: PhotoKind, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .fillMaxSize()
            .background(photoBrush(kind))
    )
}

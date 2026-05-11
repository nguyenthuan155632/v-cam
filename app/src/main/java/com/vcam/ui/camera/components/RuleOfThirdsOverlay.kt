package com.vcam.ui.camera.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun RuleOfThirdsOverlay(show: Boolean) {
    if (!show) return
    Canvas(modifier = Modifier.fillMaxSize()) {
        val color = Color.White.copy(alpha = 0.5f)
        val w = size.width
        val h = size.height
        val sw = 1.2f
        drawLine(color, Offset(w / 3f, 0f), Offset(w / 3f, h), strokeWidth = sw)
        drawLine(color, Offset(2f * w / 3f, 0f), Offset(2f * w / 3f, h), strokeWidth = sw)
        drawLine(color, Offset(0f, h / 3f), Offset(w, h / 3f), strokeWidth = sw)
        drawLine(color, Offset(0f, 2f * h / 3f), Offset(w, 2f * h / 3f), strokeWidth = sw)
    }
}

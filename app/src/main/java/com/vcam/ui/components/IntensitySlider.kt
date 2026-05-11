package com.vcam.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Intensity slider matching the design — 3px track, 5 tick marks, white thumb with
 * 1.5px accent ring. Dark variant uses lighter neutrals on translucent surfaces.
 */
@Composable
fun IntensitySlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    accent: Color,
    modifier: Modifier = Modifier,
    dark: Boolean = false,
) {
    val density = LocalDensity.current
    var widthPx by remember { mutableStateOf(0f) }

    val trackBg = if (dark) Color(0x2EFFFFFF) else Color(0x1F15110E)
    val tickColor = if (dark) Color(0x59FFFFFF) else Color(0x4D15110E)
    val thumbShadow = Color(0x33000000)

    Box(
        modifier
            .fillMaxWidth()
            .height(22.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (widthPx > 0) {
                        val v = ((offset.x / widthPx) * 100f).coerceIn(0f, 100f).toInt()
                        onValueChange(v)
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    if (widthPx > 0) {
                        val v = ((change.position.x / widthPx) * 100f).coerceIn(0f, 100f).toInt()
                        onValueChange(v)
                    }
                }
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(22.dp)) {
            widthPx = size.width
            val centerY = size.height / 2f
            val trackHeight = with(density) { 3.dp.toPx() }
            // Track background
            drawTrack(0f, size.width, centerY, trackHeight, trackBg)
            // Active fill
            val fillEnd = size.width * (value / 100f)
            drawTrack(0f, fillEnd, centerY, trackHeight, accent)
            // Ticks
            val tickHeight = with(density) { 8.dp.toPx() }
            for (t in listOf(0, 25, 50, 75, 100)) {
                val x = size.width * (t / 100f)
                drawLine(
                    color = tickColor,
                    start = Offset(x, centerY + trackHeight / 2f + with(density){0.5.dp.toPx()}),
                    end = Offset(x, centerY + trackHeight / 2f + tickHeight),
                    strokeWidth = with(density){1.dp.toPx()},
                )
            }
            // Thumb
            val thumbCenter = Offset(fillEnd, centerY)
            val thumbR = with(density) { 11.dp.toPx() }
            val ringR = thumbR
            // Outer subtle drop shadow circle
            drawCircle(thumbShadow, radius = thumbR + with(density){1.dp.toPx()}, center = thumbCenter.copy(y = thumbCenter.y + with(density){1.dp.toPx()}))
            // White fill
            drawCircle(Color.White, radius = thumbR, center = thumbCenter)
            // Accent ring
            drawCircle(accent, radius = ringR, center = thumbCenter, style = Stroke(width = with(density){1.5.dp.toPx()}))
        }
    }
}

private fun DrawScope.drawTrack(start: Float, end: Float, y: Float, height: Float, color: Color) {
    if (end <= start) return
    val r = height / 2f
    drawRoundRect(
        color = color,
        topLeft = Offset(start, y - height / 2f),
        size = Size((end - start).coerceAtLeast(0f), height),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
    )
}

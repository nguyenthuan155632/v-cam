package com.vcam.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Stroke / filled vector icons matching the inline SVGs in vcam-icons.jsx.
 * Use the trailing-lambda form of [ImageVector.Builder.path] — the inline DSL
 * call requires the path body as a trailing lambda, not a named argument.
 */
private const val STROKE_WIDTH = 1.6f

private inline fun stroke(
    name: String,
    sw: Float = STROKE_WIDTH,
    crossinline body: PathBuilder.() -> Unit,
): ImageVector {
    val builder = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    )
    builder.path(
        fill = null,
        stroke = SolidColor(Color.Black),
        strokeAlpha = 1f,
        strokeLineWidth = sw,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
        pathFillType = PathFillType.NonZero,
    ) { body() }
    return builder.build()
}

private inline fun filled(
    name: String,
    crossinline body: PathBuilder.() -> Unit,
): ImageVector {
    val builder = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    )
    builder.path(
        fill = SolidColor(Color.Black),
        stroke = null,
        pathFillType = PathFillType.NonZero,
    ) { body() }
    return builder.build()
}

private inline fun strokeTwoPaths(
    name: String,
    sw: Float = STROKE_WIDTH,
    crossinline first: PathBuilder.() -> Unit,
    crossinline second: PathBuilder.() -> Unit,
): ImageVector {
    val builder = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    )
    builder.path(
        fill = null,
        stroke = SolidColor(Color.Black),
        strokeLineWidth = sw,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) { first() }
    builder.path(
        fill = null,
        stroke = SolidColor(Color.Black),
        strokeLineWidth = sw,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) { second() }
    return builder.build()
}

object VIcons {

    val FlashAuto: ImageVector = stroke("FlashAuto") {
        moveTo(13f, 2f); lineTo(4f, 14f); horizontalLineToRelative(7f)
        lineTo(10f, 22f); lineTo(19f, 10f); horizontalLineToRelative(-7f)
        lineTo(13f, 2f); close()
    }

    val FlashOn: ImageVector = filled("FlashOn") {
        moveTo(13f, 2f); lineTo(4f, 14f); horizontalLineToRelative(7f)
        lineTo(10f, 22f); lineTo(19f, 10f); horizontalLineToRelative(-7f)
        lineTo(13f, 2f); close()
    }

    val FlashOff: ImageVector = strokeTwoPaths(
        name = "FlashOff",
        first = {
            moveTo(13f, 2f); lineTo(4f, 14f); horizontalLineToRelative(7f)
            lineTo(10f, 22f); lineTo(19f, 10f); horizontalLineToRelative(-7f)
            lineTo(13f, 2f); close()
        },
        second = { moveTo(3f, 3f); lineTo(21f, 21f) },
    )

    val Timer: ImageVector = stroke("Timer") {
        moveTo(12f, 21f)
        arcToRelative(8f, 8f, 0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 0.001f, dy1 = -16f)
        arcToRelative(8f, 8f, 0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = -0.001f, dy1 = 16f)
        close()
        moveTo(12f, 9f); verticalLineToRelative(4f); lineToRelative(2f, 2f)
        moveTo(9f, 2f); horizontalLineToRelative(6f)
        moveTo(12f, 5f); verticalLineToRelative(-3f)
    }

    val Flip: ImageVector = stroke("Flip") {
        moveTo(3f, 7f); horizontalLineToRelative(13f); lineToRelative(-3f, -3f)
        moveTo(21f, 17f); horizontalLineToRelative(-13f); lineToRelative(3f, 3f)
        moveTo(14f, 10f); horizontalLineToRelative(7f); verticalLineToRelative(5f); horizontalLineToRelative(-7f); close()
        moveTo(3f, 9f); horizontalLineToRelative(7f); verticalLineToRelative(5f); horizontalLineToRelative(-7f); close()
    }

    val Grid: ImageVector = stroke("Grid", sw = 1.5f) {
        moveTo(3f, 3f); horizontalLineToRelative(18f); verticalLineToRelative(18f); horizontalLineToRelative(-18f); close()
        moveTo(3f, 9f); horizontalLineToRelative(18f)
        moveTo(3f, 15f); horizontalLineToRelative(18f)
        moveTo(9f, 3f); verticalLineToRelative(18f)
        moveTo(15f, 3f); verticalLineToRelative(18f)
    }

    val Settings: ImageVector = stroke("Settings") {
        moveTo(12f, 12f)
        arcToRelative(3f, 3f, 0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 0.001f, dy1 = -6f)
        arcToRelative(3f, 3f, 0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = -0.001f, dy1 = 6f)
        close()
        moveTo(19f, 12f)
        arcToRelative(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = -0.1f, dy1 = -1.2f)
        lineToRelative(2f, -1.5f); lineToRelative(-2f, -3.5f); lineToRelative(-2.4f, 0.9f)
        arcToRelative(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = -2f, dy1 = -1.2f)
        lineTo(14f, 3f); horizontalLineToRelative(-4f); lineToRelative(-0.5f, 2.5f)
        arcToRelative(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = -2f, dy1 = 1.2f)
        lineToRelative(-2.4f, -0.9f); lineToRelative(-2f, 3.5f); lineToRelative(2f, 1.5f)
        arcToRelative(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = false, dx1 = -0.1f, dy1 = 1.2f)
        curveToRelative(0f, 0.4f, 0f, 0.8f, 0.1f, 1.2f)
        lineToRelative(-2f, 1.5f); lineToRelative(2f, 3.5f); lineToRelative(2.4f, -0.9f)
        curveToRelative(0.6f, 0.5f, 1.3f, 0.9f, 2f, 1.2f)
        lineTo(10f, 21f); horizontalLineToRelative(4f); lineToRelative(0.5f, -2.5f)
        curveToRelative(0.7f, -0.3f, 1.4f, -0.7f, 2f, -1.2f)
        lineToRelative(2.4f, 0.9f); lineToRelative(2f, -3.5f); lineToRelative(-2f, -1.5f)
        curveToRelative(0.1f, -0.4f, 0.1f, -0.8f, 0.1f, -1.2f); close()
    }

    val Close: ImageVector = stroke("Close", sw = 1.8f) {
        moveTo(6f, 6f); lineTo(18f, 18f)
        moveTo(18f, 6f); lineTo(6f, 18f)
    }

    val Check: ImageVector = stroke("Check", sw = 1.8f) {
        moveTo(4f, 12f); lineToRelative(6f, 6f); lineToRelative(10f, -12f)
    }

    val ChevronRight: ImageVector = stroke("ChevronRight", sw = 2f) {
        moveTo(9f, 6f); lineToRelative(6f, 6f); lineToRelative(-6f, 6f)
    }

    val Back: ImageVector = stroke("Back", sw = 1.8f) {
        moveTo(15f, 6f); lineToRelative(-6f, 6f); lineToRelative(6f, 6f)
    }

    val Download: ImageVector = stroke("Download") {
        moveTo(12f, 3f); verticalLineToRelative(13f)
        moveTo(6f, 11f); lineToRelative(6f, 6f); lineToRelative(6f, -6f)
        moveTo(4f, 21f); horizontalLineToRelative(16f)
    }

    val Retake: ImageVector = stroke("Retake") {
        moveTo(3f, 12f)
        arcToRelative(9f, 9f, 0f, isMoreThanHalf = false, isPositiveArc = true, dx1 = 15.5f, dy1 = -6.3f)
        lineTo(21f, 8f)
        moveTo(21f, 3f); verticalLineToRelative(5f); horizontalLineToRelative(-5f)
    }

    val Edit: ImageVector = stroke("Edit") {
        moveTo(12f, 21f); horizontalLineToRelative(9f)
        moveTo(16.5f, 3.5f)
        arcToRelative(2.1f, 2.1f, 0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 3f, dy1 = 3f)
        lineTo(7f, 19f); lineToRelative(-4f, 1f); lineToRelative(1f, -4f); close()
    }

    val Star: ImageVector = filled("Star") {
        moveTo(12f, 2f); lineToRelative(3f, 6.9f); lineToRelative(7.5f, 0.7f)
        lineToRelative(-5.6f, 5f); lineToRelative(1.7f, 7.4f); lineTo(12f, 18f)
        lineToRelative(-6.6f, 4f); lineToRelative(1.7f, -7.4f); lineTo(1.5f, 9.6f)
        lineTo(9f, 8.9f); close()
    }

    val StarOutline: ImageVector = stroke("StarOutline", sw = 1.5f) {
        moveTo(12f, 2f); lineToRelative(3f, 6.9f); lineToRelative(7.5f, 0.7f)
        lineToRelative(-5.6f, 5f); lineToRelative(1.7f, 7.4f); lineTo(12f, 18f)
        lineToRelative(-6.6f, 4f); lineToRelative(1.7f, -7.4f); lineTo(1.5f, 9.6f)
        lineTo(9f, 8.9f); close()
    }

    val Heart: ImageVector = filled("Heart") {
        moveTo(12f, 21f); curveToRelative(0f, 0f, -7f, -4.5f, -9.5f, -9f)
        curveTo(1f, 8.5f, 2.5f, 5f, 6f, 5f); curveToRelative(2f, 0f, 3.5f, 1f, 4f, 2.5f)
        curveTo(10.5f, 6f, 12f, 5f, 14f, 5f); curveToRelative(3.5f, 0f, 5f, 3.5f, 3.5f, 7f)
        curveTo(19f, 16.5f, 12f, 21f, 12f, 21f); close()
    }

    val Search: ImageVector = stroke("Search", sw = 1.7f) {
        moveTo(11f, 18f)
        arcToRelative(7f, 7f, 0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 0.001f, dy1 = -14f)
        arcToRelative(7f, 7f, 0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = -0.001f, dy1 = 14f)
        close()
        moveTo(21f, 21f); lineToRelative(-4.5f, -4.5f)
    }
}

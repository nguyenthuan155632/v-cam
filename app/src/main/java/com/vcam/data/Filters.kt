package com.vcam.data

import androidx.compose.ui.graphics.Color

private fun argb(a: Float, r: Int, g: Int, b: Int): Color =
    Color(red = r / 255f, green = g / 255f, blue = b / 255f, alpha = a)

val Filters: List<Filter> = listOf(
    Filter("fd01", "Food", "Crisp 01", "F·01", "luts/fd01.cube",
        argb(0.18f, 255, 210, 150), argb(0.12f, 255, 140, 90),
        saturate = 1.20f, contrast = 1.05f, brightness = 1.03f),
    Filter("fd02", "Food", "Bake", "F·02", "luts/fd02.cube",
        argb(0.22f, 255, 180, 120), argb(0.14f, 190, 90, 40),
        saturate = 1.10f, contrast = 1.08f, brightness = 0.98f, sepia = 0.05f),
    Filter("fd03", "Food", "Honey", "F·03", "luts/fd03.cube",
        argb(0.22f, 255, 200, 80), argb(0.16f, 220, 140, 40),
        saturate = 1.18f, contrast = 1.04f, brightness = 1.05f, sepia = 0.08f),

    Filter("cf01", "Cafe", "Latte", "C·01", "luts/cf01.cube",
        argb(0.25f, 220, 190, 150), argb(0.18f, 140, 100, 70),
        saturate = 0.95f, contrast = 1.03f, brightness = 1.02f, sepia = 0.10f),
    Filter("cf02", "Cafe", "Roast", "C·02", "luts/cf02.cube",
        argb(0.28f, 180, 140, 100), argb(0.18f, 80, 50, 30),
        saturate = 0.90f, contrast = 1.12f, brightness = 0.92f, sepia = 0.18f),
    Filter("cf03", "Cafe", "Mocha", "C·03", "luts/cf03.cube",
        argb(0.30f, 160, 110, 70), argb(0.20f, 70, 40, 20),
        saturate = 0.92f, contrast = 1.10f, brightness = 0.95f, sepia = 0.15f),

    Filter("pr01", "Portrait", "Glow", "P·01", "luts/pr01.cube",
        argb(0.20f, 255, 200, 180), argb(0.12f, 255, 160, 140),
        saturate = 1.05f, contrast = 0.98f, brightness = 1.05f),
    Filter("pr02", "Portrait", "Soft", "P·02", "luts/pr02.cube",
        argb(0.22f, 255, 220, 210), argb(0.14f, 240, 180, 170),
        saturate = 0.95f, contrast = 0.95f, brightness = 1.06f),
    Filter("pr03", "Portrait", "Skin Tone", "P·03", "luts/pr03.cube",
        argb(0.20f, 255, 210, 190), argb(0.14f, 220, 160, 140),
        saturate = 1.02f, contrast = 1.02f, brightness = 1.04f),

    Filter("tr01", "Travel", "Coast", "T·01", "luts/tr01.cube",
        argb(0.20f, 170, 210, 220), argb(0.14f, 255, 210, 150),
        saturate = 1.15f, contrast = 1.06f, brightness = 1.04f),
    Filter("tr02", "Travel", "Dune", "T·02", "luts/tr02.cube",
        argb(0.25f, 240, 200, 140), argb(0.16f, 200, 130, 80),
        saturate = 1.10f, contrast = 1.08f, brightness = 1.03f, sepia = 0.08f),
    Filter("tr03", "Travel", "Kyoto", "T·03", "luts/tr03.cube",
        argb(0.22f, 230, 170, 160), argb(0.14f, 180, 140, 170),
        saturate = 1.04f, contrast = 1.05f, brightness = 1.00f),

    Filter("vt01", "Vintage", "1978", "V·01", "luts/vt01.cube",
        argb(0.28f, 220, 170, 90), argb(0.20f, 160, 90, 60),
        saturate = 0.85f, contrast = 1.08f, brightness = 0.98f, sepia = 0.25f),
    Filter("vt02", "Vintage", "Polaroid", "V·02", "luts/vt02.cube",
        argb(0.22f, 255, 230, 180), argb(0.14f, 190, 150, 110),
        saturate = 0.92f, contrast = 0.94f, brightness = 1.06f, sepia = 0.18f),
    Filter("vt03", "Vintage", "Faded", "V·03", "luts/vt03.cube",
        argb(0.25f, 200, 180, 160), argb(0.16f, 160, 140, 130),
        saturate = 0.70f, contrast = 0.92f, brightness = 1.04f),

    Filter("nt01", "Night", "Neon", "N·01", "luts/nt01.cube",
        argb(0.25f, 120, 90, 200), argb(0.18f, 220, 80, 120),
        saturate = 1.30f, contrast = 1.18f, brightness = 0.95f),
    Filter("nt02", "Night", "Moody", "N·02", "luts/nt02.cube",
        argb(0.32f, 60, 50, 80), argb(0.20f, 140, 90, 90),
        saturate = 1.05f, contrast = 1.22f, brightness = 0.85f),
    Filter("nt03", "Night", "Tungsten", "N·03", "luts/nt03.cube",
        argb(0.25f, 255, 160, 80), argb(0.20f, 120, 60, 40),
        saturate = 1.15f, contrast = 1.12f, brightness = 0.92f, sepia = 0.12f),

    Filter("cl01", "Clean", "Bright", "L·01", "luts/cl01.cube",
        argb(0.10f, 255, 255, 255), argb(0.06f, 240, 240, 240),
        saturate = 1.05f, contrast = 1.04f, brightness = 1.08f),
    Filter("cl02", "Clean", "Pure", "L·02", "luts/cl02.cube",
        argb(0.08f, 250, 250, 250), argb(0.04f, 230, 230, 230),
        saturate = 1.02f, contrast = 1.02f, brightness = 1.04f),

    Filter("wm01", "Warm", "Amber", "W·01", "luts/wm01.cube",
        argb(0.22f, 255, 180, 90), argb(0.14f, 230, 130, 60),
        saturate = 1.15f, contrast = 1.05f, brightness = 1.03f, sepia = 0.10f),
    Filter("wm02", "Warm", "Toast", "W·02", "luts/wm02.cube",
        argb(0.25f, 240, 170, 100), argb(0.16f, 200, 120, 70),
        saturate = 1.08f, contrast = 1.06f, brightness = 1.00f, sepia = 0.14f),

    Filter("co01", "Cool", "Mist", "X·01", "luts/co01.cube",
        argb(0.22f, 170, 200, 220), argb(0.14f, 130, 170, 200),
        saturate = 0.95f, contrast = 1.04f, brightness = 1.04f, hueRotateDeg = -6f),
    Filter("co02", "Cool", "Frost", "X·02", "luts/co02.cube",
        argb(0.25f, 180, 210, 230), argb(0.16f, 140, 180, 210),
        saturate = 0.90f, contrast = 1.06f, brightness = 1.06f, hueRotateDeg = -10f),
)

fun filtersInCategory(cat: String): List<Filter> = Filters.filter { it.category == cat }

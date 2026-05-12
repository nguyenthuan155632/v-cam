package com.vcam.color

import android.graphics.Bitmap
import com.vcam.camera.CubeLut

fun applyLutCpuTrilinear(src: Bitmap, lut: CubeLut, intensity: Float): Bitmap {
    val w = src.width
    val h = src.height
    val pixels = IntArray(w * h)
    src.getPixels(pixels, 0, w, 0, 0, w, h)
    val out = IntArray(w * h)
    val nMinus1 = (lut.size - 1).toFloat()
    val cap = intensity.coerceIn(0f, 1f)

    for (i in pixels.indices) {
        val p = pixels[i]
        val a = (p ushr 24) and 0xff
        val r = ((p ushr 16) and 0xff) / 255f
        val g = ((p ushr 8) and 0xff) / 255f
        val b = (p and 0xff) / 255f
        val (gr, gg, gb) = sample(lut, r, g, b, nMinus1)
        val outR = (r + (gr - r) * cap).coerceIn(0f, 1f)
        val outG = (g + (gg - g) * cap).coerceIn(0f, 1f)
        val outB = (b + (gb - b) * cap).coerceIn(0f, 1f)
        out[i] = (a shl 24) or
            ((outR * 255f + 0.5f).toInt() shl 16) or
            ((outG * 255f + 0.5f).toInt() shl 8) or
            (outB * 255f + 0.5f).toInt()
    }

    val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    result.setPixels(out, 0, w, 0, 0, w, h)
    return result
}

private fun sample(lut: CubeLut, r: Float, g: Float, b: Float, nMinus1: Float): Triple<Float, Float, Float> {
    val n = lut.size
    val rf = r * nMinus1
    val gf = g * nMinus1
    val bf = b * nMinus1
    val r0 = rf.toInt().coerceIn(0, n - 1)
    val g0 = gf.toInt().coerceIn(0, n - 1)
    val b0 = bf.toInt().coerceIn(0, n - 1)
    val r1 = (r0 + 1).coerceAtMost(n - 1)
    val g1 = (g0 + 1).coerceAtMost(n - 1)
    val b1 = (b0 + 1).coerceAtMost(n - 1)
    val dr = rf - r0
    val dg = gf - g0
    val db = bf - b0

    fun fetch(ri: Int, gi: Int, bi: Int, ch: Int): Float {
        val idx = ((bi * n + gi) * n + ri) * 3 + ch
        return lut.data[idx]
    }

    fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t

    fun trilinear(ch: Int): Float {
        val c000 = fetch(r0, g0, b0, ch)
        val c100 = fetch(r1, g0, b0, ch)
        val c010 = fetch(r0, g1, b0, ch)
        val c110 = fetch(r1, g1, b0, ch)
        val c001 = fetch(r0, g0, b1, ch)
        val c101 = fetch(r1, g0, b1, ch)
        val c011 = fetch(r0, g1, b1, ch)
        val c111 = fetch(r1, g1, b1, ch)
        val c00 = lerp(c000, c100, dr)
        val c10 = lerp(c010, c110, dr)
        val c01 = lerp(c001, c101, dr)
        val c11 = lerp(c011, c111, dr)
        val c0 = lerp(c00, c10, dg)
        val c1 = lerp(c01, c11, dg)
        return lerp(c0, c1, db)
    }

    return Triple(trilinear(0), trilinear(1), trilinear(2))
}

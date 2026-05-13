package com.vcam.baker

import kotlin.math.abs

object ColorPipeline {

    // ─── sRGB ↔ Linear ─────────────────────────────────────────────

    fun srgbToLinearRgb(c: Rgb): Rgb = Rgb(srgbToLinear(c.r), srgbToLinear(c.g), srgbToLinear(c.b))

    fun linearToSrgbRgb(c: Rgb): Rgb = Rgb(linearToSrgb(c.r), linearToSrgb(c.g), linearToSrgb(c.b))

    private fun srgbToLinear(v: Float): Float =
        if (v <= 0.04045f) v / 12.92f
        else Math.pow(((v + 0.055f) / 1.055f).toDouble(), 2.4).toFloat()

    private fun linearToSrgb(v: Float): Float =
        if (v <= 0.0031308f) v * 12.92f
        else (1.055f * Math.pow(v.toDouble(), 1.0 / 2.4).toFloat() - 0.055f)

    // ─── White Balance (multiplicative, linear space) ──────────────

    fun whiteBalance(c: Rgb, wb: WhiteBalance): Rgb {
        val tempR = 1f + 0.15f * wb.tempShift
        val tempB = 1f - 0.15f * wb.tempShift
        val tintG = 1f - 0.10f * wb.tintShift
        val tintRB = 1f + 0.05f * wb.tintShift
        return Rgb(
            (c.r * tempR * tintRB).coerceAtLeast(0f),
            (c.g * tintG).coerceAtLeast(0f),
            (c.b * tempB * tintRB).coerceAtLeast(0f),
        )
    }

    // ─── Lift / Gamma / Gain ───────────────────────────────────────

    fun liftGammaGain(c: Rgb, lift: Rgb, gamma: Rgb, gain: Rgb): Rgb {
        require(gamma.r > 0f && gamma.g > 0f && gamma.b > 0f) { "gamma must be > 0" }
        fun apply(v: Float, lift: Float, gamma: Float, gain: Float): Float {
            val lifted = v + (1f - v) * lift
            val gammaed = if (lifted <= 0f) 0f else Math.pow(lifted.toDouble(), 1.0 / gamma.toDouble()).toFloat()
            return gammaed * gain
        }
        return Rgb(
            apply(c.r, lift.r, gamma.r, gain.r),
            apply(c.g, lift.g, gamma.g, gain.g),
            apply(c.b, lift.b, gamma.b, gain.b),
        )
    }

    // ─── Brightness ─────────────────────────────────────────────────

    fun brightness(c: Rgb, amount: Float): Rgb =
        Rgb(c.r + amount, c.g + amount, c.b + amount)

    // ─── Contrast (smooth S-curve with toe and shoulder) ───────────

    fun contrast(c: Rgb, amount: Float): Rgb {
        if (abs(amount - 1f) < 1e-4f) return c
        fun f(v: Float): Float {
            val toe = 0.02f * (amount - 1f).coerceAtLeast(-1f)
            val shoulder = 0.98f - 0.02f * (amount - 1f).coerceAtMost(1f)
            val edge0 = toe.coerceIn(0f, 1f)
            val edge1 = shoulder.coerceIn(edge0, 1f)
            if (edge1 - edge0 < 1e-4f) return v
            val t = ((v - edge0) / (edge1 - edge0)).coerceIn(0f, 1f)
            return t * t * (3f - 2f * t)
        }
        return Rgb(f(c.r), f(c.g), f(c.b))
    }

    // ─── Channel Mixer ──────────────────────────────────────────────

    fun channelMix(c: Rgb, m: ChannelMixer): Rgb = Rgb(
        c.r * m.rr + c.g * m.rg + c.b * m.rb,
        c.r * m.gr + c.g * m.gg + c.b * m.gb,
        c.r * m.br + c.g * m.bg + c.b * m.bb,
    )

    // ─── Luma ───────────────────────────────────────────────────────

    fun luma(c: Rgb): Float = 0.299f * c.r + 0.587f * c.g + 0.114f * c.b

    // ─── Vibrance (skin-tone-aware saturation) ──────────────────────

    fun vibrance(c: Rgb, amount: Float): Rgb {
        val l = luma(c)
        val maxChroma = maxOf(c.r, c.g, c.b) - minOf(c.r, c.g, c.b)
        val skinProximity = 1f - (abs(c.r - c.g) + abs(c.r - c.b)).coerceIn(0f, 1f)
        val skinMask = (1f - abs(l - 0.5f) * 2f).coerceIn(0f, 1f) * skinProximity
        val saturationMask = maxChroma.coerceIn(0f, 1f)
        val weight = (saturationMask * (1f - skinMask * 0.6f)).coerceIn(0f, 1f)
        val boost = 1f + (amount - 1f) * weight
        return Rgb(
            (l + (c.r - l) * boost).coerceIn(0f, 1f),
            (l + (c.g - l) * boost).coerceIn(0f, 1f),
            (l + (c.b - l) * boost).coerceIn(0f, 1f),
        )
    }

    // ─── Hue Rotation ───────────────────────────────────────────────

    fun rotateHue(c: Rgb, degrees: Float): Rgb {
        if (abs(degrees) < 0.01f) return c
        val (h, s, v) = rgbToHsv(c)
        var newH = (h + degrees / 360f) % 1f
        if (newH < 0f) newH += 1f
        return hsvToRgb(newH, s, v)
    }

    private fun rgbToHsv(c: Rgb): Triple<Float, Float, Float> {
        val max = maxOf(c.r, c.g, c.b)
        val min = minOf(c.r, c.g, c.b)
        val delta = max - min
        val s = if (max == 0f) 0f else delta / max
        val h = when {
            delta == 0f -> 0f
            max == c.r -> ((c.g - c.b) / delta + 6f) % 6f
            max == c.g -> ((c.b - c.r) / delta + 2f)
            else -> ((c.r - c.g) / delta + 4f)
        } / 6f
        return Triple(h, s, max)
    }

    private fun hsvToRgb(h: Float, s: Float, v: Float): Rgb {
        val i = (h * 6f).toInt()
        val f = h * 6f - i
        val p = v * (1f - s)
        val q = v * (1f - f * s)
        val t = v * (1f - (1f - f) * s)
        return when (i % 6) {
            0 -> Rgb(v, t, p)
            1 -> Rgb(q, v, p)
            2 -> Rgb(p, v, t)
            3 -> Rgb(p, q, v)
            4 -> Rgb(t, p, v)
            else -> Rgb(v, p, q)
        }
    }

    // ─── Split Toning ───────────────────────────────────────────────

    fun applySplitToning(c: Rgb, st: SplitToning?): Rgb {
        if (st == null) return c
        val l = luma(c)
        val shadowWeight = (1f - l).coerceIn(0f, 1f)
        val highlightWeight = l.coerceIn(0f, 1f)
        val sw = shadowWeight * (1f - st.balance) * 2f
        val hw = highlightWeight * st.balance * 2f
        return Rgb(
            c.r + (st.shadowTint.r - c.r) * sw * 0.3f + (st.highlightTint.r - c.r) * hw * 0.3f,
            c.g + (st.shadowTint.g - c.g) * sw * 0.3f + (st.highlightTint.g - c.g) * hw * 0.3f,
            c.b + (st.shadowTint.b - c.b) * sw * 0.3f + (st.highlightTint.b - c.b) * hw * 0.3f,
        )
    }

    // ─── Tone Curve (Catmull-Rom interpolation) ─────────────────────

    fun applyToneCurve(c: Rgb, curve: ToneCurve): Rgb {
        val isIdentity = curve.points.all { (x, y) -> abs(x - y) < 1e-4f }
        if (isIdentity) return c
        return Rgb(curveLookup(c.r, curve), curveLookup(c.g, curve), curveLookup(c.b, curve))
    }

    private fun curveLookup(v: Float, curve: ToneCurve): Float {
        val pts = curve.sortedPoints
        if (v <= pts.first().first) return pts.first().second
        if (v >= pts.last().first) return pts.last().second
        for (i in 0 until pts.size - 1) {
            val (x0, y0) = pts[i]
            val (x1, y1) = pts[i + 1]
            if (v in x0..x1) {
                val t = (v - x0) / (x1 - x0)
                val (px, py) = if (i > 0) pts[i - 1] else Pair(2 * x0 - x1, 2 * y0 - y1)
                val (nx, ny) = if (i < pts.size - 2) pts[i + 2] else Pair(2 * x1 - x0, 2 * y1 - y0)
                return catmullRom(t, py, y0, y1, ny)
            }
        }
        return v
    }

    private fun catmullRom(t: Float, p0: Float, p1: Float, p2: Float, p3: Float): Float {
        val t2 = t * t
        val t3 = t2 * t
        return 0.5f * (
            (2f * p1) +
            (-p0 + p2) * t +
            (2f * p0 - 5f * p1 + 4f * p2 - p3) * t2 +
            (-p0 + 3f * p1 - 3f * p2 + p3) * t3
        )
    }

    // ─── Pipeline Orchestration ─────────────────────────────────────

    fun apply(input: Rgb, p: FilterParams): Rgb {
        var c = srgbToLinearRgb(input)
        c = whiteBalance(c, p.whiteBalance).clamp()
        c = liftGammaGain(c, p.lift, p.gamma, p.gain).clamp()
        c = brightness(c, p.brightness).clamp()
        c = contrast(c, p.contrast).clamp()
        c = channelMix(c, p.channelMixer).clamp()
        c = vibrance(c, p.saturation).clamp()
        c = rotateHue(c, p.hueShiftDegrees).clamp()
        c = applySplitToning(c, p.splitToning).clamp()
        c = applyToneCurve(c, p.toneCurve).clamp()
        c = linearToSrgbRgb(c)
        return c.clamp()
    }

    private fun Rgb.clamp() = Rgb(r.coerceIn(0f, 1f), g.coerceIn(0f, 1f), b.coerceIn(0f, 1f))
}

package com.vcam.baker

object ColorPipeline {

    fun whiteBalance(c: Rgb, wb: WhiteBalance): Rgb {
        val r = c.r + 0.5f * wb.tempShift - 0.25f * wb.tintShift
        val g = c.g + 0.5f * wb.tintShift
        val b = c.b - 0.5f * wb.tempShift - 0.25f * wb.tintShift
        return Rgb(r, g, b)
    }

    fun brightness(c: Rgb, amount: Float): Rgb =
        Rgb(c.r + amount, c.g + amount, c.b + amount)

    fun contrast(c: Rgb, amount: Float): Rgb {
        fun f(v: Float) = (v - 0.5f) * amount + 0.5f
        return Rgb(f(c.r), f(c.g), f(c.b))
    }

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

    fun channelMix(c: Rgb, m: ChannelMixer): Rgb = Rgb(
        c.r * m.rr + c.g * m.rg + c.b * m.rb,
        c.r * m.gr + c.g * m.gg + c.b * m.gb,
        c.r * m.br + c.g * m.bg + c.b * m.bb,
    )

    fun luma(c: Rgb): Float = 0.299f * c.r + 0.587f * c.g + 0.114f * c.b

    fun saturate(c: Rgb, amount: Float): Rgb {
        val l = luma(c)
        return Rgb(
            l + (c.r - l) * amount,
            l + (c.g - l) * amount,
            l + (c.b - l) * amount,
        )
    }

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

    fun applyToneCurve(c: Rgb, curve: ToneCurve): Rgb =
        Rgb(curveLookup(c.r, curve), curveLookup(c.g, curve), curveLookup(c.b, curve))

    private fun curveLookup(v: Float, curve: ToneCurve): Float {
        val pts = curve.points.sortedBy { it.first }
        if (v <= pts.first().first) return pts.first().second
        if (v >= pts.last().first) return pts.last().second
        for (i in 0 until pts.size - 1) {
            val (x0, y0) = pts[i]
            val (x1, y1) = pts[i + 1]
            if (v in x0..x1) {
                val t = (v - x0) / (x1 - x0)
                return y0 + (y1 - y0) * t
            }
        }
        return v
    }
}

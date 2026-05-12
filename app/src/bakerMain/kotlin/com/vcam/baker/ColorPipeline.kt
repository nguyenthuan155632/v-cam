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
}

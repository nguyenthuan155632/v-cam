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
}

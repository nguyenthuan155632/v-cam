package com.vcam.baker

import org.junit.Assert.assertEquals
import org.junit.Test

class ColorPipelineTest {

    private fun assertRgbEquals(expected: Rgb, actual: Rgb, eps: Float = 1e-4f) {
        assertEquals("r", expected.r, actual.r, eps)
        assertEquals("g", expected.g, actual.g, eps)
        assertEquals("b", expected.b, actual.b, eps)
    }

    @Test
    fun whiteBalanceZeroShiftIsIdentity() {
        val input = Rgb(0.4f, 0.5f, 0.6f)
        assertRgbEquals(input, ColorPipeline.whiteBalance(input, WhiteBalance(0f, 0f)))
    }

    @Test
    fun whiteBalancePositiveTempWarmsImage() {
        val input = Rgb(0.5f, 0.5f, 0.5f)
        val out = ColorPipeline.whiteBalance(input, WhiteBalance(tempShift = 0.2f, tintShift = 0f))
        assertEquals(true, out.r > input.r)
        assertEquals(true, out.b < input.b)
    }

    @Test
    fun brightnessAdditive() {
        val input = Rgb(0.4f, 0.5f, 0.6f)
        val out = ColorPipeline.brightness(input, 0.1f)
        assertRgbEquals(Rgb(0.5f, 0.6f, 0.7f), out)
    }

    @Test
    fun contrastIdentityAtPivot() {
        val input = Rgb(0.5f, 0.5f, 0.5f)
        assertRgbEquals(input, ColorPipeline.contrast(input, 1.5f))
    }

    @Test
    fun contrastExpandsAroundPivot() {
        val input = Rgb(0.7f, 0.3f, 0.5f)
        val out = ColorPipeline.contrast(input, 2f)
        assertRgbEquals(Rgb(0.9f, 0.1f, 0.5f), out)
    }
}

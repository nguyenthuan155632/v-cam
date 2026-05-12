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

    @Test
    fun liftGammaGainIdentity() {
        val input = Rgb(0.4f, 0.5f, 0.6f)
        val out = ColorPipeline.liftGammaGain(input, Rgb(0f, 0f, 0f), Rgb(1f, 1f, 1f), Rgb(1f, 1f, 1f))
        assertRgbEquals(input, out)
    }

    @Test
    fun gainScalesHighlights() {
        val input = Rgb(0.4f, 0.5f, 0.6f)
        val out = ColorPipeline.liftGammaGain(input, Rgb(0f, 0f, 0f), Rgb(1f, 1f, 1f), Rgb(2f, 1f, 1f))
        assertEquals(0.8f, out.r, 1e-4f)
    }

    @Test
    fun gammaAboveOneLightensMidtones() {
        val input = Rgb(0.25f, 0.25f, 0.25f)
        val out = ColorPipeline.liftGammaGain(input, Rgb(0f, 0f, 0f), Rgb(2f, 1f, 1f), Rgb(1f, 1f, 1f))
        assertEquals(0.5f, out.r, 1e-4f)
    }

    @Test
    fun gammaBelowOneDarkensMidtones() {
        val input = Rgb(0.25f, 0.25f, 0.25f)
        val out = ColorPipeline.liftGammaGain(input, Rgb(0f, 0f, 0f), Rgb(0.5f, 1f, 1f), Rgb(1f, 1f, 1f))
        assertEquals(0.0625f, out.r, 1e-4f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun gammaMustBePositive() {
        ColorPipeline.liftGammaGain(Rgb(0.25f, 0.25f, 0.25f), Rgb(0f, 0f, 0f), Rgb(0f, 1f, 1f), Rgb(1f, 1f, 1f))
    }

    @Test
    fun liftRaisesShadows() {
        val input = Rgb(0f, 0f, 0f)
        val out = ColorPipeline.liftGammaGain(input, Rgb(0.2f, 0f, 0f), Rgb(1f, 1f, 1f), Rgb(1f, 1f, 1f))
        assertEquals(0.2f, out.r, 1e-4f)
    }

    @Test
    fun channelMixerIdentity() {
        val input = Rgb(0.3f, 0.6f, 0.9f)
        assertRgbEquals(input, ColorPipeline.channelMix(input, ChannelMixer.identity()))
    }

    @Test
    fun channelMixerSwapsRedAndBlue() {
        val input = Rgb(0.2f, 0.5f, 0.8f)
        val swap = ChannelMixer(0f, 0f, 1f, 0f, 1f, 0f, 1f, 0f, 0f)
        assertRgbEquals(Rgb(0.8f, 0.5f, 0.2f), ColorPipeline.channelMix(input, swap))
    }

    @Test
    fun lumaRec601Coefficients() {
        assertEquals(0.299f, ColorPipeline.luma(Rgb(1f, 0f, 0f)), 1e-4f)
        assertEquals(0.587f, ColorPipeline.luma(Rgb(0f, 1f, 0f)), 1e-4f)
        assertEquals(0.114f, ColorPipeline.luma(Rgb(0f, 0f, 1f)), 1e-4f)
    }

    @Test
    fun saturationOneIsIdentity() {
        val input = Rgb(0.7f, 0.3f, 0.1f)
        assertRgbEquals(input, ColorPipeline.saturate(input, 1f))
    }

    @Test
    fun saturationZeroProducesGrayscale() {
        val input = Rgb(0.7f, 0.3f, 0.1f)
        val out = ColorPipeline.saturate(input, 0f)
        val l = ColorPipeline.luma(input)
        assertRgbEquals(Rgb(l, l, l), out)
    }

    @Test
    fun toneCurveLinearIsIdentity() {
        val input = Rgb(0.2f, 0.5f, 0.8f)
        assertRgbEquals(input, ColorPipeline.applyToneCurve(input, ToneCurve.linear()))
    }

    @Test
    fun toneCurveSCurveLowersShadowsRaisesHighlights() {
        val curve = ToneCurve(listOf(0f to 0f, 0.25f to 0.18f, 0.5f to 0.5f, 0.75f to 0.82f, 1f to 1f))
        val shadow = ColorPipeline.applyToneCurve(Rgb(0.25f, 0.25f, 0.25f), curve)
        val highlight = ColorPipeline.applyToneCurve(Rgb(0.75f, 0.75f, 0.75f), curve)
        assertEquals(0.18f, shadow.r, 1e-3f)
        assertEquals(0.82f, highlight.r, 1e-3f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun toneCurveRejectsDuplicateXValues() {
        ToneCurve(listOf(0f to 0f, 0.5f to 0.4f, 0.5f to 0.6f, 1f to 1f))
    }

    @Test
    fun splitToningNullIsIdentity() {
        val input = Rgb(0.4f, 0.5f, 0.6f)
        assertRgbEquals(input, ColorPipeline.applySplitToning(input, null))
    }

    @Test
    fun splitToningTintsShadowsAndHighlights() {
        val st = SplitToning(
            shadowTint = Rgb(0.0f, 0.0f, 1.0f),
            highlightTint = Rgb(1.0f, 0.5f, 0.0f),
            balance = 0.5f,
        )
        val shadow = ColorPipeline.applySplitToning(Rgb(0.1f, 0.1f, 0.1f), st)
        val highlight = ColorPipeline.applySplitToning(Rgb(0.9f, 0.9f, 0.9f), st)
        assertEquals(true, shadow.b > 0.1f)
        assertEquals(true, highlight.r > 0.9f - 1e-3f && highlight.b < 0.9f)
    }

    @Test
    fun applyDefaultParamsIsIdentity() {
        val input = Rgb(0.4f, 0.5f, 0.6f)
        assertRgbEquals(input, ColorPipeline.apply(input, FilterParams()))
    }

    @Test
    fun applyClampsTo0_1() {
        val input = Rgb(0.5f, 0.5f, 0.5f)
        val out = ColorPipeline.apply(input, FilterParams(brightness = 2f))
        assertRgbEquals(Rgb(1f, 1f, 1f), out)
        val out2 = ColorPipeline.apply(input, FilterParams(brightness = -2f))
        assertRgbEquals(Rgb(0f, 0f, 0f), out2)
    }

    @Test
    fun applyClampsAfterToneCurve() {
        val input = Rgb(0f, 1f, 1f)
        val out = ColorPipeline.apply(input, FilterParams(toneCurve = ToneCurve(listOf(0f to -0.5f, 1f to 1.5f))))
        assertRgbEquals(Rgb(0f, 1f, 1f), out)
    }

    @Test
    fun applyOrderMatches_WB_then_LGG_then_brightness_then_contrast_then_mixer_then_sat_then_tone() {
        val input = Rgb(0.25f, 0.5f, 0.75f)
        val params = FilterParams(
            whiteBalance = WhiteBalance(tempShift = 0.1f, tintShift = -0.1f),
            lift = Rgb(0.05f, 0.02f, 0.01f),
            gamma = Rgb(1.2f, 0.9f, 1.1f),
            gain = Rgb(1.1f, 0.95f, 1.05f),
            brightness = 0.03f,
            contrast = 1.1f,
            channelMixer = ChannelMixer(1f, 0.05f, 0f, 0f, 1f, 0.04f, 0.03f, 0f, 1f),
            saturation = 0.8f,
            splitToning = SplitToning(Rgb(0f, 0f, 1f), Rgb(1f, 0.8f, 0f), balance = 0.6f),
            toneCurve = ToneCurve(listOf(0f to 0f, 0.5f to 0.45f, 1f to 1f)),
        )
        var expected = input
        expected = ColorPipeline.whiteBalance(expected, params.whiteBalance)
        expected = ColorPipeline.liftGammaGain(expected, params.lift, params.gamma, params.gain)
        expected = ColorPipeline.brightness(expected, params.brightness)
        expected = ColorPipeline.contrast(expected, params.contrast)
        expected = ColorPipeline.channelMix(expected, params.channelMixer)
        expected = ColorPipeline.saturate(expected, params.saturation)
        expected = ColorPipeline.applySplitToning(expected, params.splitToning)
        expected = ColorPipeline.applyToneCurve(expected, params.toneCurve)
        expected = Rgb(expected.r.coerceIn(0f, 1f), expected.g.coerceIn(0f, 1f), expected.b.coerceIn(0f, 1f))
        assertRgbEquals(expected, ColorPipeline.apply(input, params))
    }
}

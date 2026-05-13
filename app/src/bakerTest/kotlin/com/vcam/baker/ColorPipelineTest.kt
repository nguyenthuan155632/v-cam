package com.vcam.baker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorPipelineTest {

    private fun assertRgbEquals(expected: Rgb, actual: Rgb, eps: Float = 1e-4f) {
        assertEquals("r", expected.r, actual.r, eps)
        assertEquals("g", expected.g, actual.g, eps)
        assertEquals("b", expected.b, actual.b, eps)
    }

    // ─── sRGB ↔ Linear ─────────────────────────────────────────────

    @Test
    fun srgbToLinearRoundtrip() {
        val values = floatArrayOf(0f, 0.1f, 0.5f, 0.75f, 1f)
        for (v in values) {
            val lin = ColorPipeline.srgbToLinearRgb(Rgb(v, v, v))
            val back = ColorPipeline.linearToSrgbRgb(lin)
            assertRgbEquals(Rgb(v, v, v), back, eps = 1e-3f)
        }
    }

    @Test
    fun srgbToLinearDarkensMidtones() {
        val mid = ColorPipeline.srgbToLinearRgb(Rgb(0.5f, 0.5f, 0.5f))
        assertTrue(mid.r < 0.5f)
    }

    // ─── White Balance (multiplicative) ────────────────────────────

    @Test
    fun whiteBalanceZeroShiftIsIdentity() {
        val input = Rgb(0.4f, 0.5f, 0.6f)
        assertRgbEquals(input, ColorPipeline.whiteBalance(input, WhiteBalance(0f, 0f)))
    }

    @Test
    fun whiteBalanceZeroShiftPreservesNeutrals() {
        val gray = Rgb(0.5f, 0.5f, 0.5f)
        val out = ColorPipeline.whiteBalance(gray, WhiteBalance(tempShift = 0f, tintShift = 0f))
        assertEquals(out.r, out.g, 1e-4f)
        assertEquals(out.g, out.b, 1e-4f)
    }

    @Test
    fun whiteBalancePositiveTempWarmsImage() {
        val input = Rgb(0.5f, 0.5f, 0.5f)
        val out = ColorPipeline.whiteBalance(input, WhiteBalance(tempShift = 0.2f, tintShift = 0f))
        assertTrue(out.r > input.r)
        assertTrue(out.b < input.b)
    }

    // ─── Lift / Gamma / Gain ───────────────────────────────────────

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

    // ─── Brightness ─────────────────────────────────────────────────

    @Test
    fun brightnessAdditive() {
        val input = Rgb(0.4f, 0.5f, 0.6f)
        val out = ColorPipeline.brightness(input, 0.1f)
        assertRgbEquals(Rgb(0.5f, 0.6f, 0.7f), out)
    }

    // ─── Contrast (smooth S-curve) ──────────────────────────────────

    @Test
    fun contrastIdentityAtOne() {
        val input = Rgb(0.2f, 0.5f, 0.8f)
        val out = ColorPipeline.contrast(input, 1f)
        assertRgbEquals(input, out, eps = 1e-3f)
    }

    @Test
    fun contrastMonotonic() {
        val values = floatArrayOf(0f, 0.1f, 0.3f, 0.5f, 0.7f, 0.9f, 1f)
        var prev = -1f
        for (v in values) {
            val out = ColorPipeline.contrast(Rgb(v, v, v), 1.3f).r
            assertTrue(out >= prev)
            prev = out
        }
    }

    @Test
    fun contrastOutputIn01() {
        val values = floatArrayOf(0f, 0.25f, 0.5f, 0.75f, 1f)
        for (v in values) {
            val out = ColorPipeline.contrast(Rgb(v, v, v), 1.5f).r
            assertTrue(out >= 0f && out <= 1f)
        }
    }

    @Test
    fun contrastMoreContrastiveExpandsMidtones() {
        val low = ColorPipeline.contrast(Rgb(0.3f, 0.3f, 0.3f), 1.5f).r
        val high = ColorPipeline.contrast(Rgb(0.7f, 0.7f, 0.7f), 1.5f).r
        assertTrue(low < 0.3f)
        assertTrue(high > 0.7f)
    }

    // ─── Channel Mixer ──────────────────────────────────────────────

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

    // ─── Luma ───────────────────────────────────────────────────────

    @Test
    fun lumaRec601Coefficients() {
        assertEquals(0.299f, ColorPipeline.luma(Rgb(1f, 0f, 0f)), 1e-4f)
        assertEquals(0.587f, ColorPipeline.luma(Rgb(0f, 1f, 0f)), 1e-4f)
        assertEquals(0.114f, ColorPipeline.luma(Rgb(0f, 0f, 1f)), 1e-4f)
    }

    // ─── Vibrance ───────────────────────────────────────────────────

    @Test
    fun vibranceOneIsIdentity() {
        val input = Rgb(0.7f, 0.3f, 0.1f)
        assertRgbEquals(input, ColorPipeline.vibrance(input, 1f), eps = 1e-4f)
    }

    @Test
    fun vibranceZeroStronglyDesaturates() {
        val input = Rgb(0.8f, 0.2f, 0.1f)
        val out = ColorPipeline.vibrance(input, 0f)
        val outLuma = ColorPipeline.luma(out)
        // At vibrance=0, the result should be much closer to grayscale than the input
        val inputChroma = maxOf(input.r, input.g, input.b) - minOf(input.r, input.g, input.b)
        val outChroma = maxOf(out.r, out.g, out.b) - minOf(out.r, out.g, out.b)
        assertTrue(outChroma < inputChroma * 0.5f)
    }

    @Test
    fun vibranceProtectsSkinTones() {
        val skin = Rgb(0.7f, 0.55f, 0.45f)
        val vibranced = ColorPipeline.vibrance(skin, 1.5f)
        val globalSat = saturate(skin, 1.5f)
        val satChange = colorDistance(skin, vibranced)
        val globalChange = colorDistance(skin, globalSat)
        assertTrue("vibrance should change skin less than global saturation", satChange < globalChange)
    }

    // ─── Hue Rotation ───────────────────────────────────────────────

    @Test
    fun hueRotationRoundtrip() {
        val input = Rgb(0.7f, 0.3f, 0.1f)
        val rotated = ColorPipeline.rotateHue(input, 30f)
        val back = ColorPipeline.rotateHue(rotated, -30f)
        assertRgbEquals(input, back, eps = 1e-3f)
    }

    @Test
    fun hueRotationZeroIsIdentity() {
        val input = Rgb(0.7f, 0.3f, 0.1f)
        assertRgbEquals(input, ColorPipeline.rotateHue(input, 0f))
    }

    // ─── Tone Curve ─────────────────────────────────────────────────

    @Test
    fun toneCurveLinearIsIdentity() {
        val input = Rgb(0.2f, 0.5f, 0.8f)
        assertRgbEquals(input, ColorPipeline.applyToneCurve(input, ToneCurve.linear()), eps = 1e-4f)
    }

    @Test
    fun toneCurveSCurveLowersShadowsRaisesHighlights() {
        val curve = ToneCurve(listOf(0f to 0f, 0.25f to 0.18f, 0.5f to 0.5f, 0.75f to 0.82f, 1f to 1f))
        val shadow = ColorPipeline.applyToneCurve(Rgb(0.25f, 0.25f, 0.25f), curve)
        val highlight = ColorPipeline.applyToneCurve(Rgb(0.75f, 0.75f, 0.75f), curve)
        assertEquals(0.18f, shadow.r, 1e-3f)
        assertEquals(0.82f, highlight.r, 1e-3f)
    }

    @Test
    fun toneCurveCatmullRomIsSmooth() {
        val curve = ToneCurve(listOf(0f to 0f, 0.5f to 0.6f, 1f to 1f))
        val mid = ColorPipeline.applyToneCurve(Rgb(0.5f, 0.5f, 0.5f), curve)
        assertEquals(0.6f, mid.r, 1e-4f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun toneCurveRejectsDuplicateXValues() {
        ToneCurve(listOf(0f to 0f, 0.5f to 0.4f, 0.5f to 0.6f, 1f to 1f))
    }

    // ─── Split Toning ───────────────────────────────────────────────

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
        assertTrue(shadow.b > 0.1f)
        assertTrue(highlight.r > 0.9f - 1e-3f && highlight.b < 0.9f)
    }

    // ─── Pipeline Orchestration ─────────────────────────────────────

    @Test
    fun applyDefaultParamsIsIdentity() {
        val input = Rgb(0.4f, 0.5f, 0.6f)
        val out = ColorPipeline.apply(input, FilterParams())
        assertRgbEquals(input, out, eps = 1e-3f)
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
    fun applyOrderMatchesExpected() {
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
            hueShiftDegrees = 10f,
        )
        var expected = ColorPipeline.srgbToLinearRgb(input)
        expected = ColorPipeline.whiteBalance(expected, params.whiteBalance).clamp()
        expected = ColorPipeline.liftGammaGain(expected, params.lift, params.gamma, params.gain).clamp()
        expected = ColorPipeline.brightness(expected, params.brightness).clamp()
        expected = ColorPipeline.contrast(expected, params.contrast).clamp()
        expected = ColorPipeline.channelMix(expected, params.channelMixer).clamp()
        expected = ColorPipeline.vibrance(expected, params.saturation).clamp()
        expected = ColorPipeline.rotateHue(expected, params.hueShiftDegrees).clamp()
        expected = ColorPipeline.applySplitToning(expected, params.splitToning).clamp()
        expected = ColorPipeline.applyToneCurve(expected, params.toneCurve).clamp()
        expected = ColorPipeline.linearToSrgbRgb(expected).clamp()
        assertRgbEquals(expected, ColorPipeline.apply(input, params), eps = 1e-4f)
    }

    // Helpers
    private fun lerp(a: Rgb, b: Rgb, t: Float): Rgb {
        val ct = t.coerceIn(0f, 1f)
        return Rgb(a.r + (b.r - a.r) * ct, a.g + (b.g - a.g) * ct, a.b + (b.b - a.b) * ct)
    }

    private fun colorDistance(a: Rgb, b: Rgb): Float =
        kotlin.math.sqrt((a.r - b.r) * (a.r - b.r) + (a.g - b.g) * (a.g - b.g) + (a.b - b.b) * (a.b - b.b))

    private fun saturate(c: Rgb, amount: Float): Rgb {
        val l = ColorPipeline.luma(c)
        return Rgb(l + (c.r - l) * amount, l + (c.g - l) * amount, l + (c.b - l) * amount)
    }

    private fun Rgb.clamp() = Rgb(r.coerceIn(0f, 1f), g.coerceIn(0f, 1f), b.coerceIn(0f, 1f))
}

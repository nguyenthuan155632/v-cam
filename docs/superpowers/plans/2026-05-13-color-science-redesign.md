# Color Science Redesign — Filter Pipeline v2 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the naive RGB color pipeline in `bakerMain` with a film colorist pipeline (sRGB↔linear, multiplicative white balance, smooth S-curve contrast, vibrance, hue rotation, Catmull-Rom tone curves, per-stage clamping) and retune all 29 recipes with per-filter `defaultIntensity`.

**Architecture:** All changes are build-time only (`bakerMain` source set + runtime data model). Runtime shader, `.cube` format, thumbnail renderer, and offscreen processor are untouched. The new pipeline produces 33-cube `.cube` files that the existing runtime consumes identically.

**Tech Stack:** Kotlin 2.0, JVM (bakerMain), JUnit 4, Android runtime (data model changes only).

**Spec:** `docs/superpowers/specs/2026-05-13-color-science-redesign.md`

---

## File Structure

| File | Action | Responsibility |
|---|---|---|
| `app/src/bakerMain/kotlin/com/vcam/baker/Types.kt` | Modify | Add `hueShiftDegrees` and `defaultIntensity` to `FilterParams` |
| `app/src/bakerMain/kotlin/com/vcam/baker/ColorPipeline.kt` | Rewrite | New film colorist math (linear light, multiplicative WB, smooth S-curve, vibrance, hue rotation, Catmull-Rom, per-stage clamp) |
| `app/src/bakerTest/kotlin/com/vcam/baker/ColorPipelineTest.kt` | Rewrite | New tests matching v2 pipeline behavior |
| `app/src/bakerMain/kotlin/com/vcam/baker/FilterRecipes.kt` | Rewrite | Retuned 29 recipes with `defaultIntensity` and `hueShiftDegrees` |
| `app/src/main/java/com/vcam/color/FilterParams.kt` | Modify | Add `hueShiftDegrees` and `defaultIntensity` (runtime mirror) |
| `app/src/main/java/com/vcam/color/Filter.kt` | Modify | Add `defaultIntensity` |
| `app/src/main/java/com/vcam/color/FilterCatalog.kt` | Modify | Populate `defaultIntensity` for all 29 filters |
| `app/src/main/java/com/vcam/ui/camera/CameraViewModel.kt` | Modify | Use `filter.defaultIntensity` on filter selection |
| `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt` | Modify | Use `filter.defaultIntensity` on filter selection |
| `app/src/main/assets/luts/*.cube` | Regenerate | Re-run `./gradlew :app:bakeLuts` after all baker changes |

---

## Task 1: Extend baker types with `hueShiftDegrees` and `defaultIntensity`

**Files:**
- Modify: `app/src/bakerMain/kotlin/com/vcam/baker/Types.kt`

- [ ] **Step 1: Add new fields to `FilterParams`**

Replace the `FilterParams` data class:

```kotlin
data class FilterParams(
    val whiteBalance: WhiteBalance = WhiteBalance(),
    val lift: Rgb = Rgb(0f, 0f, 0f),
    val gamma: Rgb = Rgb(1f, 1f, 1f),
    val gain: Rgb = Rgb(1f, 1f, 1f),
    val saturation: Float = 1f,
    val contrast: Float = 1f,
    val brightness: Float = 0f,
    val channelMixer: ChannelMixer = ChannelMixer.identity(),
    val toneCurve: ToneCurve = ToneCurve.linear(),
    val splitToning: SplitToning? = null,
    val hueShiftDegrees: Float = 0f,
    val defaultIntensity: Float = 1f,
)
```

- [ ] **Step 2: Commit**

```bash
git add app/src/bakerMain/kotlin/com/vcam/baker/Types.kt
git commit -m "feat(baker): add hueShiftDegrees and defaultIntensity to FilterParams

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 2: Rewrite `ColorPipeline.kt` with film colorist math

**Files:**
- Modify: `app/src/bakerMain/kotlin/com/vcam/baker/ColorPipeline.kt`

- [ ] **Step 1: Write the new pipeline**

Replace the entire file content:

```kotlin
package com.vcam.baker

object ColorPipeline {

    // ─── sRGB ↔ Linear ─────────────────────────────────────────────

    fun srgbToLinearRgb(c: Rgb): Rgb = Rgb(srgbToLinear(c.r), srgbToLinear(c.g), srgbToLinear(c.b))

    fun linearToSrgbRgb(c: Rgb): Rgb = Rgb(linearToSrgb(c.r), linearToSrgb(c.g), linearToSrgb(c.b))

    private fun srgbToLinear(v: Float): Float =
        if (v <= 0.04045f) v / 12.92f
        else kotlin.math.pow((v + 0.055f) / 1.055f, 2.4f).toFloat()

    private fun linearToSrgb(v: Float): Float =
        if (v <= 0.0031308f) v * 12.92f
        else (1.055f * kotlin.math.pow(v.toDouble(), 1.0 / 2.4).toFloat() - 0.055f)

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
            val gammaed = if (lifted <= 0f) 0f else kotlin.math.pow(lifted.toDouble(), 1.0 / gamma.toDouble()).toFloat()
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
        if (kotlin.math.abs(amount - 1f) < 1e-4f) return c
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
        val skinProximity = 1f - (kotlin.math.abs(c.r - c.g) + kotlin.math.abs(c.r - c.b)).coerceIn(0f, 1f)
        val skinMask = (1f - kotlin.math.abs(l - 0.5f) * 2f).coerceIn(0f, 1f) * skinProximity
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
        if (kotlin.math.abs(degrees) < 0.01f) return c
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
        val isIdentity = curve.points.all { (x, y) -> kotlin.math.abs(x - y) < 1e-4f }
        if (isIdentity) return c
        return Rgb(curveLookup(c.r, curve), curveLookup(c.g, curve), curveLookup(c.b, curve))
    }

    private fun curveLookup(v: Float, curve: ToneCurve): Float {
        val pts = curve.points.sortedBy { it.first }
        if (v <= pts.first().first) return pts.first().second
        if (v >= pts.last().first) return pts.last().second
        for (i in 0 until pts.size - 1) {
            val (x0, y0) = pts[i]
            val (x1, y1) = pts[i + 1]
            if (v in x0..x1) {
                val t = (v - x0) / (x1 - x0)
                val (px, py) = if (i > 0) pts[i - 1] else (2 * x0 - x1, 2 * y0 - y1)
                val (nx, ny) = if (i < pts.size - 2) pts[i + 2] else (2 * x1 - x0, 2 * y1 - y0)
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
```

- [ ] **Step 2: Verify baker compiles**

Run: `./gradlew :app:compileBakerMainKotlin --no-daemon`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add app/src/bakerMain/kotlin/com/vcam/baker/ColorPipeline.kt
git commit -m "feat(baker): film colorist pipeline v2

sRGB/linear conversion, multiplicative white balance, smooth S-curve
contrast, vibrance, hue rotation, Catmull-Rom tone curves, per-stage
clamping.

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 3: Rewrite `ColorPipelineTest.kt` for v2 behavior

**Files:**
- Modify: `app/src/bakerTest/kotlin/com/vcam/baker/ColorPipelineTest.kt`

- [ ] **Step 1: Replace test file**

```kotlin
package com.vcam.baker

import org.junit.Assert.assertEquals
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
        assertEquals(true, mid.r < 0.5f)
    }

    // ─── White Balance (multiplicative) ────────────────────────────

    @Test
    fun whiteBalanceZeroShiftIsIdentity() {
        val input = Rgb(0.4f, 0.5f, 0.6f)
        assertRgbEquals(input, ColorPipeline.whiteBalance(input, WhiteBalance(0f, 0f)))
    }

    @Test
    fun whiteBalancePreservesNeutralsWithTempOnly() {
        val gray = Rgb(0.5f, 0.5f, 0.5f)
        val out = ColorPipeline.whiteBalance(gray, WhiteBalance(tempShift = 0.2f, tintShift = 0f))
        assertEquals(out.r, out.g, 1e-4f)
        assertEquals(out.g, out.b, 1e-4f)
    }

    @Test
    fun whiteBalancePositiveTempWarmsImage() {
        val input = Rgb(0.5f, 0.5f, 0.5f)
        val out = ColorPipeline.whiteBalance(input, WhiteBalance(tempShift = 0.2f, tintShift = 0f))
        assertEquals(true, out.r > input.r)
        assertEquals(true, out.b < input.b)
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
            assertEquals(true, out >= prev)
            prev = out
        }
    }

    @Test
    fun contrastOutputIn01() {
        val values = floatArrayOf(0f, 0.25f, 0.5f, 0.75f, 1f)
        for (v in values) {
            val out = ColorPipeline.contrast(Rgb(v, v, v), 1.5f).r
            assertEquals(true, out >= 0f && out <= 1f)
        }
    }

    @Test
    fun contrastMoreContrastiveExpandsMidtones() {
        val low = ColorPipeline.contrast(Rgb(0.3f, 0.3f, 0.3f), 1.5f).r
        val high = ColorPipeline.contrast(Rgb(0.7f, 0.7f, 0.7f), 1.5f).r
        assertEquals(true, low < 0.3f)
        assertEquals(true, high > 0.7f)
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
    fun vibranceZeroProducesGrayscale() {
        val input = Rgb(0.7f, 0.3f, 0.1f)
        val out = ColorPipeline.vibrance(input, 0f)
        val l = ColorPipeline.luma(input)
        assertRgbEquals(Rgb(l, l, l), out, eps = 1e-4f)
    }

    @Test
    fun vibranceProtectsSkinTones() {
        // Warm skin tone
        val skin = Rgb(0.7f, 0.55f, 0.45f)
        val saturated = ColorPipeline.vibrance(skin, 1.5f)
        val globalSat = lerp(skin, Rgb(ColorPipeline.luma(skin), ColorPipeline.luma(skin), ColorPipeline.luma(skin)), 1.5f)
        val satChange = colorDistance(skin, saturated)
        val globalChange = colorDistance(skin, globalSat)
        assertEquals(true, satChange < globalChange)
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
        // Symmetric 3-point curve: midpoint should be exact
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
        assertEquals(true, shadow.b > 0.1f)
        assertEquals(true, highlight.r > 0.9f - 1e-3f && highlight.b < 0.9f)
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

    private fun Rgb.clamp() = Rgb(r.coerceIn(0f, 1f), g.coerceIn(0f, 1f), b.coerceIn(0f, 1f))
}
```

- [ ] **Step 2: Run tests to verify GREEN**

Run: `./gradlew :app:bakerUnitTest --no-daemon`
Expected: All tests pass.

- [ ] **Step 3: Commit**

```bash
git add app/src/bakerTest/kotlin/com/vcam/baker/ColorPipelineTest.kt
git commit -m "test(baker): ColorPipeline v2 unit tests

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 4: Rewrite `FilterRecipes.kt` with retuned values + per-filter defaults

**Files:**
- Modify: `app/src/bakerMain/kotlin/com/vcam/baker/FilterRecipes.kt`

- [ ] **Step 1: Replace recipe file**

```kotlin
package com.vcam.baker

object FilterRecipes {
    val all: Map<String, FilterParams> = linkedMapOf(
        // ----- Food (defaultIntensity 0.85) -----
        "food_fresh" to FilterParams(
            whiteBalance = WhiteBalance(0.03f, -0.01f),
            saturation = 1.12f, contrast = 1.05f, brightness = 0.01f,
            channelMixer = ChannelMixer(1.03f, 0f, 0f, 0f, 1.01f, 0f, 0f, 0f, 0.98f),
            toneCurve = ToneCurve(listOf(0f to 0f, 0.25f to 0.22f, 0.5f to 0.55f, 0.75f to 0.82f, 1f to 1f)),
            hueShiftDegrees = 4f,
            defaultIntensity = 0.85f,
        ),
        "food_sweet" to FilterParams(
            whiteBalance = WhiteBalance(0.05f, 0.02f),
            saturation = 1.15f, contrast = 1.02f,
            gain = Rgb(1.03f, 1.0f, 0.98f),
            hueShiftDegrees = 3f,
            defaultIntensity = 0.85f,
        ),
        "food_warm_table" to FilterParams(
            whiteBalance = WhiteBalance(0.09f, 0.02f),
            saturation = 1.06f, contrast = 1.04f,
            gain = Rgb(1.05f, 1.0f, 0.95f),
            toneCurve = ToneCurve(listOf(0f to 0.02f, 0.5f to 0.52f, 1f to 0.98f)),
            defaultIntensity = 0.85f,
        ),
        "food_creamy" to FilterParams(
            whiteBalance = WhiteBalance(0.04f, 0f),
            saturation = 0.98f, contrast = 0.96f, brightness = 0.03f,
            lift = Rgb(0.03f, 0.02f, 0.015f),
            defaultIntensity = 0.85f,
        ),
        "food_garden" to FilterParams(
            whiteBalance = WhiteBalance(-0.01f, 0.03f),
            saturation = 1.12f, contrast = 1.03f,
            channelMixer = ChannelMixer(0.99f, 0f, 0f, 0f, 1.04f, 0f, 0f, 0f, 0.99f),
            hueShiftDegrees = -2f,
            defaultIntensity = 0.85f,
        ),
        // ----- Portrait (defaultIntensity 0.75) -----
        "portrait_clean_bright" to FilterParams(
            whiteBalance = WhiteBalance(0.02f, 0f),
            saturation = 1.02f, contrast = 1.01f, brightness = 0.03f,
            toneCurve = ToneCurve(listOf(0f to 0.02f, 0.5f to 0.52f, 1f to 1f)),
            defaultIntensity = 0.75f,
        ),
        "portrait_soft_skin" to FilterParams(
            whiteBalance = WhiteBalance(0.03f, -0.01f),
            saturation = 0.94f, contrast = 0.95f, brightness = 0.04f,
            lift = Rgb(0.03f, 0.02f, 0.015f),
            gain = Rgb(1.01f, 1.0f, 0.99f),
            defaultIntensity = 0.75f,
        ),
        "portrait_warm" to FilterParams(
            whiteBalance = WhiteBalance(0.07f, -0.01f),
            saturation = 1.02f, contrast = 1.02f,
            gain = Rgb(1.04f, 1.0f, 0.96f),
            defaultIntensity = 0.75f,
        ),
        "portrait_pink_tint" to FilterParams(
            whiteBalance = WhiteBalance(0.03f, -0.03f),
            saturation = 0.98f, contrast = 0.97f,
            channelMixer = ChannelMixer(1.02f, 0f, 0.01f, 0f, 1.0f, 0f, 0f, 0f, 1.01f),
            defaultIntensity = 0.75f,
        ),
        "portrait_studio_glow" to FilterParams(
            saturation = 1.0f, contrast = 0.94f, brightness = 0.05f,
            lift = Rgb(0.04f, 0.03f, 0.03f),
            toneCurve = ToneCurve(listOf(0f to 0.04f, 0.5f to 0.55f, 1f to 0.97f)),
            defaultIntensity = 0.75f,
        ),
        // ----- Film (varied defaultIntensity) -----
        "film_classic_cool" to FilterParams(
            whiteBalance = WhiteBalance(-0.02f, 0f),
            saturation = 0.90f, contrast = 1.06f,
            channelMixer = ChannelMixer(1.0f, 0f, 0.03f, 0f, 1.0f, 0f, 0.03f, 0f, 1.0f),
            toneCurve = ToneCurve(listOf(0f to 0.04f, 0.25f to 0.22f, 0.5f to 0.5f, 0.75f to 0.78f, 1f to 0.96f)),
            defaultIntensity = 0.80f,
        ),
        "film_soft" to FilterParams(
            saturation = 0.88f, contrast = 0.92f, brightness = 0.03f,
            lift = Rgb(0.04f, 0.04f, 0.04f),
            gain = Rgb(0.97f, 0.97f, 0.97f),
            defaultIntensity = 0.90f,
        ),
        "film_warm_vintage" to FilterParams(
            whiteBalance = WhiteBalance(0.05f, 0.02f),
            saturation = 0.78f, contrast = 1.02f,
            gain = Rgb(1.03f, 0.98f, 0.91f),
            splitToning = SplitToning(Rgb(0.2f, 0.18f, 0.30f), Rgb(0.95f, 0.85f, 0.55f), balance = 0.55f),
            defaultIntensity = 0.75f,
        ),
        "film_faded_negative" to FilterParams(
            whiteBalance = WhiteBalance(-0.01f, 0.01f),
            saturation = 0.72f, contrast = 0.90f,
            lift = Rgb(0.06f, 0.045f, 0.04f),
            toneCurve = ToneCurve(listOf(0f to 0.10f, 0.5f to 0.5f, 1f to 0.92f)),
            defaultIntensity = 0.85f,
        ),
        "film_pushed_color" to FilterParams(
            saturation = 1.20f, contrast = 1.12f,
            channelMixer = ChannelMixer(1.04f, 0f, 0f, 0f, 1.0f, 0f, 0f, 0f, 1.04f),
            defaultIntensity = 0.70f,
        ),
        "film_muted_frame" to FilterParams(
            saturation = 0.90f, contrast = 0.96f, brightness = 0.02f,
            channelMixer = ChannelMixer(0.99f, 0.015f, 0f, 0f, 0.99f, 0.015f, 0.015f, 0f, 0.99f),
            defaultIntensity = 0.90f,
        ),
        // ----- Travel (defaultIntensity 0.80) -----
        "travel_summer_pop" to FilterParams(
            whiteBalance = WhiteBalance(0.03f, 0f),
            saturation = 1.15f, contrast = 1.06f, brightness = 0.01f,
            defaultIntensity = 0.80f,
        ),
        "travel_beach_bright" to FilterParams(
            whiteBalance = WhiteBalance(0.02f, 0f),
            saturation = 1.08f, contrast = 1.04f, brightness = 0.04f,
            channelMixer = ChannelMixer(1.0f, 0f, 0f, 0f, 1.03f, 0f, 0f, 0f, 1.04f),
            defaultIntensity = 0.80f,
        ),
        "travel_city_clear" to FilterParams(
            whiteBalance = WhiteBalance(-0.01f, 0f),
            saturation = 1.02f, contrast = 1.08f,
            defaultIntensity = 0.80f,
        ),
        "travel_teal_orange" to FilterParams(
            saturation = 1.06f, contrast = 1.05f,
            channelMixer = ChannelMixer(1.04f, 0f, -0.03f, 0f, 1.0f, 0f, -0.03f, 0f, 1.05f),
            splitToning = SplitToning(Rgb(0.05f, 0.4f, 0.5f), Rgb(0.95f, 0.6f, 0.25f), balance = 0.5f),
            hueShiftDegrees = -3f,
            defaultIntensity = 0.80f,
        ),
        "travel_golden_hour" to FilterParams(
            whiteBalance = WhiteBalance(0.08f, 0f),
            saturation = 1.08f, contrast = 1.02f,
            gain = Rgb(1.04f, 0.98f, 0.90f),
            defaultIntensity = 0.80f,
        ),
        // ----- Night (defaultIntensity 0.75) -----
        "night_city" to FilterParams(
            whiteBalance = WhiteBalance(-0.02f, 0f),
            saturation = 1.06f, contrast = 1.12f, brightness = -0.03f,
            channelMixer = ChannelMixer(0.99f, 0f, 0f, 0f, 1.0f, 0f, 0f, 0f, 1.04f),
            defaultIntensity = 0.75f,
        ),
        "night_neon_soft" to FilterParams(
            whiteBalance = WhiteBalance(-0.01f, -0.02f),
            saturation = 1.20f, contrast = 1.06f, brightness = -0.01f,
            channelMixer = ChannelMixer(1.03f, 0f, 0f, 0f, 0.99f, 0f, 0f, 0.03f, 1.03f),
            defaultIntensity = 0.75f,
        ),
        "night_low_light_warm" to FilterParams(
            whiteBalance = WhiteBalance(0.04f, 0f),
            saturation = 1.02f, contrast = 1.08f, brightness = 0.03f,
            lift = Rgb(0.03f, 0.02f, 0.015f),
            defaultIntensity = 0.75f,
        ),
        "night_moody_blue" to FilterParams(
            whiteBalance = WhiteBalance(-0.05f, 0f),
            saturation = 0.94f, contrast = 1.14f, brightness = -0.04f,
            channelMixer = ChannelMixer(0.96f, 0f, 0f, 0f, 0.99f, 0f, 0f, 0f, 1.07f),
            defaultIntensity = 0.75f,
        ),
        // ----- Mono (defaultIntensity 0.90) -----
        "mono_soft_bw" to FilterParams(
            saturation = 0f, contrast = 0.95f, brightness = 0.02f,
            toneCurve = ToneCurve(listOf(0f to 0.04f, 0.5f to 0.52f, 1f to 0.96f)),
            defaultIntensity = 0.90f,
        ),
        "mono_high_contrast_bw" to FilterParams(
            saturation = 0f, contrast = 1.30f,
            toneCurve = ToneCurve(listOf(0f to 0f, 0.25f to 0.10f, 0.5f to 0.5f, 0.75f to 0.90f, 1f to 1f)),
            defaultIntensity = 0.90f,
        ),
        "mono_noir" to FilterParams(
            saturation = 0f, contrast = 1.45f, brightness = -0.03f,
            toneCurve = ToneCurve(listOf(0f to 0f, 0.4f to 0.20f, 0.6f to 0.78f, 1f to 1f)),
            defaultIntensity = 0.90f,
        ),
        "mono_warm" to FilterParams(
            saturation = 0f, contrast = 1.02f,
            splitToning = SplitToning(
                shadowTint = Rgb(0.18f, 0.12f, 0.08f),
                highlightTint = Rgb(0.96f, 0.90f, 0.78f),
                balance = 0.55f,
            ),
            defaultIntensity = 0.90f,
        ),
    )
}
```

- [ ] **Step 2: Verify baker compiles**

Run: `./gradlew :app:compileBakerMainKotlin --no-daemon`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add app/src/bakerMain/kotlin/com/vcam/baker/FilterRecipes.kt
git commit -m "feat(baker): retune all 29 recipes with v2 pipeline values

Adds per-filter defaultIntensity (0.70–0.90) and targeted
hueShiftDegrees. All magnitudes reduced for multiplicative/linear
pipeline.

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 5: Run `bakeLuts` to regenerate all `.cube` files

**Files:**
- Generated: `app/src/main/assets/luts/*.cube`

- [ ] **Step 1: Run the baker**

```bash
./gradlew :app:bakeLuts --no-daemon
```

Expected: `BUILD SUCCESSFUL`, stdout shows `wrote 29 .cube files`.

- [ ] **Step 2: Verify output count**

```bash
ls app/src/main/assets/luts/*.cube | wc -l
```

Expected: `29`

- [ ] **Step 3: Run all baker tests**

```bash
./gradlew :app:bakerUnitTest --no-daemon
```

Expected: All tests pass.

- [ ] **Step 4: Commit generated assets**

```bash
git add app/src/main/assets/luts/
git commit -m "feat(assets): regenerate 29 .cube files with v2 color pipeline

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 6: Update runtime data model with `defaultIntensity`

**Files:**
- Modify: `app/src/main/java/com/vcam/color/FilterParams.kt`
- Modify: `app/src/main/java/com/vcam/color/Filter.kt`
- Modify: `app/src/main/java/com/vcam/color/FilterCatalog.kt`

- [ ] **Step 1: Add fields to runtime `FilterParams.kt`**

Append two fields to the data class:

```kotlin
data class FilterParams(
    val whiteBalance: WhiteBalance = WhiteBalance(),
    val lift: Rgb = Rgb(0f, 0f, 0f),
    val gamma: Rgb = Rgb(1f, 1f, 1f),
    val gain: Rgb = Rgb(1f, 1f, 1f),
    val saturation: Float = 1f,
    val contrast: Float = 1f,
    val brightness: Float = 0f,
    val channelMixer: ChannelMixer = ChannelMixer.identity(),
    val toneCurve: ToneCurve = ToneCurve.linear(),
    val splitToning: SplitToning? = null,
    val hueShiftDegrees: Float = 0f,
    val defaultIntensity: Float = 1f,
)
```

- [ ] **Step 2: Add `defaultIntensity` to runtime `Filter.kt`**

```kotlin
data class Filter(
    val id: String,
    val category: FilterCategory,
    val displayName: String,
    val shortCode: String,
    val lutAsset: String,
    val intensityCap: Float = 1f,
    val defaultIntensity: Float = 1f,
)
```

- [ ] **Step 3: Populate `defaultIntensity` in `FilterCatalog.kt`**

Replace each `Filter(...)` constructor call with the matching `defaultIntensity`:

```kotlin
object FilterCatalog {
    val all: List<Filter> = listOf(
        Filter("food_fresh", FilterCategory.Food, "Fresh", "FD·1", "luts/food_fresh.cube", defaultIntensity = 0.85f),
        Filter("food_sweet", FilterCategory.Food, "Sweet", "FD·2", "luts/food_sweet.cube", defaultIntensity = 0.85f),
        Filter("food_warm_table", FilterCategory.Food, "Warm Table", "FD·3", "luts/food_warm_table.cube", defaultIntensity = 0.85f),
        Filter("food_creamy", FilterCategory.Food, "Creamy", "FD·4", "luts/food_creamy.cube", defaultIntensity = 0.85f),
        Filter("food_garden", FilterCategory.Food, "Garden", "FD·5", "luts/food_garden.cube", defaultIntensity = 0.85f),

        Filter("portrait_clean_bright", FilterCategory.Portrait, "Clean Bright", "PR·1", "luts/portrait_clean_bright.cube", defaultIntensity = 0.75f),
        Filter("portrait_soft_skin", FilterCategory.Portrait, "Soft Skin", "PR·2", "luts/portrait_soft_skin.cube", defaultIntensity = 0.75f),
        Filter("portrait_warm", FilterCategory.Portrait, "Warm", "PR·3", "luts/portrait_warm.cube", defaultIntensity = 0.75f),
        Filter("portrait_pink_tint", FilterCategory.Portrait, "Pink Tint", "PR·4", "luts/portrait_pink_tint.cube", defaultIntensity = 0.75f),
        Filter("portrait_studio_glow", FilterCategory.Portrait, "Studio Glow", "PR·5", "luts/portrait_studio_glow.cube", defaultIntensity = 0.75f),

        Filter("film_classic_cool", FilterCategory.Film, "Classic Cool", "FM·1", "luts/film_classic_cool.cube", defaultIntensity = 0.80f),
        Filter("film_soft", FilterCategory.Film, "Soft Film", "FM·2", "luts/film_soft.cube", defaultIntensity = 0.90f),
        Filter("film_warm_vintage", FilterCategory.Film, "Warm Vintage", "FM·3", "luts/film_warm_vintage.cube", defaultIntensity = 0.75f),
        Filter("film_faded_negative", FilterCategory.Film, "Faded Negative", "FM·4", "luts/film_faded_negative.cube", defaultIntensity = 0.85f),
        Filter("film_pushed_color", FilterCategory.Film, "Pushed Color", "FM·5", "luts/film_pushed_color.cube", defaultIntensity = 0.70f),
        Filter("film_muted_frame", FilterCategory.Film, "Muted Frame", "FM·6", "luts/film_muted_frame.cube", defaultIntensity = 0.90f),

        Filter("travel_summer_pop", FilterCategory.Travel, "Summer Pop", "TR·1", "luts/travel_summer_pop.cube", defaultIntensity = 0.80f),
        Filter("travel_beach_bright", FilterCategory.Travel, "Beach Bright", "TR·2", "luts/travel_beach_bright.cube", defaultIntensity = 0.80f),
        Filter("travel_city_clear", FilterCategory.Travel, "City Clear", "TR·3", "luts/travel_city_clear.cube", defaultIntensity = 0.80f),
        Filter("travel_teal_orange", FilterCategory.Travel, "Teal Orange", "TR·4", "luts/travel_teal_orange.cube", defaultIntensity = 0.80f),
        Filter("travel_golden_hour", FilterCategory.Travel, "Golden Hour", "TR·5", "luts/travel_golden_hour.cube", defaultIntensity = 0.80f),

        Filter("night_city", FilterCategory.Night, "City Night", "NT·1", "luts/night_city.cube", defaultIntensity = 0.75f),
        Filter("night_neon_soft", FilterCategory.Night, "Neon Soft", "NT·2", "luts/night_neon_soft.cube", defaultIntensity = 0.75f),
        Filter("night_low_light_warm", FilterCategory.Night, "Low Light Warm", "NT·3", "luts/night_low_light_warm.cube", defaultIntensity = 0.75f),
        Filter("night_moody_blue", FilterCategory.Night, "Moody Blue", "NT·4", "luts/night_moody_blue.cube", defaultIntensity = 0.75f),

        Filter("mono_soft_bw", FilterCategory.Mono, "Soft B&W", "MN·1", "luts/mono_soft_bw.cube", defaultIntensity = 0.90f),
        Filter("mono_high_contrast_bw", FilterCategory.Mono, "High Contrast B&W", "MN·2", "luts/mono_high_contrast_bw.cube", defaultIntensity = 0.90f),
        Filter("mono_noir", FilterCategory.Mono, "Noir", "MN·3", "luts/mono_noir.cube", defaultIntensity = 0.90f),
        Filter("mono_warm", FilterCategory.Mono, "Warm Mono", "MN·4", "luts/mono_warm.cube", defaultIntensity = 0.90f),
    )

    fun byCategory(cat: FilterCategory): List<Filter> = all.filter { it.category == cat }
    fun byId(id: String): Filter? = all.firstOrNull { it.id == id }
}
```

- [ ] **Step 4: Verify runtime tests still pass**

Run: `./gradlew :app:testDebugUnitTest --no-daemon`
Expected: `BUILD SUCCESSFUL` (runtime tests are unaffected by data class field additions with defaults).

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/vcam/color/FilterParams.kt \
        app/src/main/java/com/vcam/color/Filter.kt \
        app/src/main/java/com/vcam/color/FilterCatalog.kt
git commit -m "feat(color): add defaultIntensity to runtime Filter model

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 7: Wire `defaultIntensity` into ViewModels

**Files:**
- Modify: `app/src/main/java/com/vcam/ui/camera/CameraViewModel.kt`
- Modify: `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt`

- [ ] **Step 1: Update `CameraViewModel.kt`**

In `setActiveFilterId`, set intensity to the filter's `defaultIntensity`:

```kotlin
    fun setActiveFilterId(id: String) = _state.update {
        val filter = FilterCatalog.byId(id)
        val resolvedId = filter?.id ?: FilterCatalog.all.first().id
        it.copy(activeFilterId = resolvedId, intensity = ((filter?.defaultIntensity ?: 1f) * 100).toInt())
    }
```

Also update `init` block so initial filter selection uses defaultIntensity:

```kotlin
    init {
        viewModelScope.launch {
            repo.settings.collect { user ->
                val filter = FilterCatalog.byId(user.defaultFilterId)
                val resolvedId = filter?.id ?: FilterCatalog.all.first().id
                val defaultIntensity = filter?.defaultIntensity ?: FilterCatalog.all.first().defaultIntensity
                _state.update {
                    it.copy(
                        aspectRatio = user.defaultAspectRatio,
                        activeFilterId = resolvedId,
                        intensity = (defaultIntensity * 100).toInt(),
                        saveOriginal = user.saveOriginal,
                        gridOn = user.gridLines || it.gridOn,
                    )
                }
            }
        }
    }
```

- [ ] **Step 2: Update `PhotoPreviewViewModel.kt`**

In `setFilterId`, set intensity to the filter's `defaultIntensity`:

```kotlin
    fun setFilterId(id: String) = _state.update {
        val filter = FilterCatalog.byId(id)
        val resolvedId = filter?.id ?: FilterCatalog.all.first().id
        it.copy(activeFilterId = resolvedId, intensity = ((filter?.defaultIntensity ?: 1f) * 100).toInt())
    }.also { renderActiveFilter() }
```

Also update `init` block:

```kotlin
    init {
        if (repo != null) {
            viewModelScope.launch {
                repo.settings.collect { user ->
                    val filter = FilterCatalog.byId(user.defaultFilterId)
                    val resolvedId = filter?.id ?: FilterCatalog.all.first().id
                    val defaultIntensity = filter?.defaultIntensity ?: FilterCatalog.all.first().defaultIntensity
                    _state.update { it.copy(activeFilterId = resolvedId, intensity = (defaultIntensity * 100).toInt()) }
                    renderActiveFilter()
                }
            }
        }
    }
```

- [ ] **Step 3: Verify runtime tests still pass**

Run: `./gradlew :app:testDebugUnitTest --no-daemon`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/vcam/ui/camera/CameraViewModel.kt \
        app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt
git commit -m "feat(ui): use per-filter defaultIntensity on filter selection

Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

---

## Task 8: Final verification

- [ ] **Step 1: Run full test suite**

```bash
./gradlew :app:bakerUnitTest :app:testDebugUnitTest --no-daemon
```

Expected: Both tasks pass.

- [ ] **Step 2: Build APK**

```bash
./gradlew :app:assembleDebug --no-daemon
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Run `gitnexus_detect_changes`**

```bash
# Per AGENTS.md, run before committing
```

Use the `gitnexus_detect_changes` tool. Verify affected symbols are only in baker + color packages.

- [ ] **Step 4: Final commit (if all green)**

If there are any remaining uncommitted changes, commit them:

```bash
git status
# Add and commit any remaining changes
```

---

## Self-review checklist

**Spec coverage:**
- [x] sRGB↔linear conversion — Task 2
- [x] Multiplicative white balance — Task 2
- [x] Smooth S-curve contrast — Task 2 + Task 3
- [x] Vibrance — Task 2 + Task 3
- [x] Hue rotation — Task 2 + Task 3
- [x] Catmull-Rom tone curves — Task 2 + Task 3
- [x] Per-stage clamping — Task 2
- [x] Per-filter defaultIntensity — Tasks 1, 4, 6, 7
- [x] Recipe retuning — Task 4
- [x] Runtime data model update — Tasks 5, 6, 7
- [x] Regenerate `.cube` files — Task 5

**Placeholder scan:** None found. All steps contain exact code.

**Type consistency:**
- `FilterParams.hueShiftDegrees: Float` — same name in baker (Task 1) and runtime (Task 6)
- `FilterParams.defaultIntensity: Float` — same name in baker (Task 1) and runtime (Task 6)
- `Filter.defaultIntensity: Float` — added in Task 6, read in Task 7
- `ColorPipeline.apply` signature unchanged — backward compatible with `LutBaker.bake`

**No red flags.** Plan is complete.

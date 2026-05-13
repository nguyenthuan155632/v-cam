# V-Cam Color Science Redesign — Filter Pipeline v2

**Date:** 2026-05-13
**Status:** Approved, ready for implementation plan
**Depends on:** `docs/superpowers/specs/2026-05-12-filter-system-design.md`

## Problem

The existing `ColorPipeline` (built in Task 2–5 of the 2026-05-12 plan) uses a naive "digital filter" math model that produces artificial, harsh, or muddy results on real photographs. All 29 procedural recipes share the same underlying quality ceiling because the math itself is wrong for photographic grading. Specifically:

1. **Additive white balance** pushes pure whites into magenta/orange instead of scaling channels multiplicatively.
2. **No linear-light conversion** — contrast, saturation, and lift/gamma/gain operate on gamma-encoded sRGB values. A "2× contrast" on sRGB data is not the same as 2× on linear light; shadows crush asymmetrically.
3. **Harsh linear contrast stretch** around pivot 0.5 with no rolloff. Film-like apps use smooth S-curves with a "toe" (gentle shadow rolloff) and "shoulder" (gentle highlight compression).
4. **Global saturation** oversaturates skin tones along with everything else. Foodie and similar apps use **vibrance** (skin-tone-aware saturation).
5. **No hue-specific adjustments** — reds cannot be shifted toward orange (appetizing) without distorting blues.
6. **Linear-interpolated tone curves** between sparse control points produce abrupt gradient transitions.
7. **Single end-of-pipeline clamp** — intermediate channel values fly far outside [0,1], causing asymmetric distortion before the final `coerceIn`.
8. **All filters default to 100% intensity** — heavy recipes (`travel_teal_orange`, `night_moody_blue`) look cartoonish at full strength.

## Goal

Replace the naive RGB pipeline with a **film colorist pipeline** that produces natural, appetizing, and photographically correct grades. All improvements are build-time only (`bakerMain`); runtime shader, `.cube` format, and APK size are unchanged.

1. **sRGB ↔ linear conversion** before/after physically meaningful operations.
2. **Multiplicative white balance** in linear space.
3. **Smooth parametric S-curve** for contrast (toe + shoulder + parametric midpoint).
4. **Vibrance** instead of global saturation (protects skin tones / reds).
5. **Hue rotation** for targeted color shifts per recipe.
6. **Smooth tone curve interpolation** (smoothstep or Catmull-Rom) between control points.
7. **Clamp between stages** to prevent gamut excursions.
8. **Per-filter `defaultIntensity`** so heavy filters default to 60–80% instead of 100%.
9. Retune all 29 recipes with restrained values; replace aggressive shifts with subtle, intentional grades.

## Non-goals

- Change runtime shader code (`LutShaders.kt`) — the LUT format and lookup math stay identical.
- Change `.cube` file format or cube size (33).
- Change thumbnail renderer, offscreen processor, or any UI code.
- Add user-adjustable hue/vibrance sliders — these are recipe-authoring controls only.
- Replace the entire recipe set with new categories — keep the 6 categories and 29 IDs.

## Architecture

Only the `bakerMain` source set changes. Runtime is unaffected.

```
┌────────────────────────────────────────┐
│  bakerMain / ColorPipeline.kt (v2)     │
│  ├─ sRGB ↔ Linear conversion           │
│  ├─ multiplicative white balance       │
│  ├─ lift / gamma / gain (in linear)    │
│  ├─ brightness (additive in linear)    │
│  ├─ smooth S-curve contrast            │
│  ├─ channel mixer (linear)             │
│  ├─ vibrance (skin-aware saturation)   │
│  ├─ hue rotation (per-recipe targets)  │
│  ├─ split toning (linear)              │
│  ├─ smooth tone curve                  │
│  └─ clamp [0,1] at each boundary       │
└────────────────────────────────────────┘
                   │
                   ▼
┌────────────────────────────────────────┐
│  bakerMain / FilterRecipes.kt (v2)     │
│  └─ per-filter defaultIntensity        │
│  └─ retuned param values (much         │
│      more restrained)                  │
└────────────────────────────────────────┘
                   │
                   ▼
┌────────────────────────────────────────┐
│  bakerMain / Main.kt (unchanged)       │
│  └─ runs baker → assets/luts/*.cube    │
└────────────────────────────────────────┘
```

## Detailed pipeline changes

### 1. sRGB ↔ Linear conversion

Add two pure functions:

```kotlin
fun srgbToLinear(v: Float): Float =
    if (v <= 0.04045f) v / 12.92f
    else Math.pow((v + 0.055) / 1.055, 2.4).toFloat()

fun linearToSrgb(v: Float): Float =
    if (v <= 0.0031308f) v * 12.92f
    else (1.055f * Math.pow(v.toDouble(), 1.0 / 2.4).toFloat() - 0.055f)
```

Pipeline order becomes:

```
input sRGB [0,1]
  → convert to linear
  1. white balance (multiplicative, linear)
  2. lift / gamma / gain (linear)
  3. brightness (additive, linear)
  4. smooth S-curve contrast (linear)
  5. channel mixer (linear)
  6. vibrance (linear)
  7. hue rotation (linear)
  8. split toning (linear)
  9. smooth tone curve (linear)
  → convert back to sRGB
  10. clamp [0,1]
output sRGB → cube entry
```

### 2. Multiplicative white balance

Replace additive shift with multiplicative scaling derived from correlated color temperature (CCT) approximation:

```kotlin
fun whiteBalance(c: Rgb, wb: WhiteBalance): Rgb {
    // tempShift > 0 → warm (boost R, attenuate B)
    // tempShift < 0 → cool (attenuate R, boost B)
    // tintShift > 0 → magenta (boost R+B, attenuate G)
    // tintShift < 0 → green (boost G, attenuate R+B)
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
```

Neutrals (R=G=B) stay neutral. Scaling factors are clamped to ≥0 to prevent inversion.

### 3. Smooth parametric S-curve for contrast

Replace linear stretch `(v - 0.5) * amount + 0.5` with a smooth S-curve that has configurable toe (shadow rolloff), shoulder (highlight compression), and pivot.

The curve is a piecewise smooth function defined over [0,1]:

```
For a given amount (1.0 = neutral, >1 = more contrast, <1 = less):
  toe = 0.02 * (amount - 1)       // gentle shadow compression
  shoulder = 0.98 - 0.02 * (amount - 1)  // gentle highlight compression
  pivot = 0.5

// Smoothstep-based S-curve
f(v) = smoothstep(toe, shoulder, v)

where smoothstep(edge0, edge1, x):
  t = clamp((x - edge0) / (edge1 - edge0), 0, 1)
  return t * t * (3 - 2t)
```

This gives:
- **Neutral (`amount = 1`)**: `toe = 0.02`, `shoulder = 0.98`, curve is nearly identity with very gentle rolloff at extremes.
- **More contrast (`amount = 1.2`)**: `toe = 0.06`, `shoulder = 0.94`, shadows compressed more, highlights compressed more, midtones stretched.
- **Less contrast (`amount = 0.8`)**: `toe = -0.02` (clamped to 0), `shoulder = 1.02` (clamped to 1), flatter curve.

Result is always monotonic and stays within [0,1] (no need for post-clamp after contrast alone).

### 4. Vibrance instead of global saturation

Global saturation (`l + (c - l) * amount`) oversaturates skin tones. Vibrance preserves low-saturation pixels (skin, neutrals) while boosting already-saturated pixels (sky, foliage, food).

```kotlin
fun vibrance(c: Rgb, amount: Float): Rgb {
    val l = luma(c)
    val maxChroma = maxOf(c.r, c.g, c.b) - minOf(c.r, c.g, c.b)
    // Skin-tone protection: reduce vibrance effect on warm colors near skin luma
    val skinProximity = 1f - (abs(c.r - c.g) + abs(c.r - c.b)).coerceIn(0f, 1f)
    val skinMask = (1f - abs(l - 0.5f) * 2f).coerceIn(0f, 1f) * skinProximity
    // Low-saturation pixels get less boost
    val saturationMask = maxChroma.coerceIn(0f, 1f)
    // Final weight: high for saturated non-skin, low for skin/neutrals
    val weight = (saturationMask * (1f - skinMask * 0.6f)).coerceIn(0f, 1f)
    val boost = 1f + (amount - 1f) * weight
    return Rgb(
        (l + (c.r - l) * boost).coerceIn(0f, 1f),
        (l + (c.g - l) * boost).coerceIn(0f, 1f),
        (l + (c.b - l) * boost).coerceIn(0f, 1f),
    )
}
```

- `amount = 1` → identity.
- `amount > 1` → boosts saturated colors more than skin tones.
- `amount < 1` → desaturates, but skin tones desaturate less than surroundings.

### 5. Hue rotation for targeted color shifts

Some recipes need to push reds toward orange (appetizing food), greens toward teal (fresh), or blues toward cyan (clean sky). This is done with a simple HSV-style hue rotation.

```kotlin
fun rotateHue(c: Rgb, shiftDegrees: Float): Rgb {
    // shiftDegrees: positive = clockwise in HSV hue wheel
    // Only applied when shiftDegrees != 0
    val (h, s, v) = rgbToHsv(c)
    val newH = (h + shiftDegrees / 360f) % 1f
    return hsvToRgb(newH, s, v)
}
```

`rgbToHsv` and `hsvToRgb` are standard well-known conversions. This is applied **after** vibrance so that color relationships are stable before hue shifting.

Note: hue rotation is added as a new field on `FilterParams`:

```kotlin
data class FilterParams(
    // ... existing fields ...
    val hueShiftDegrees: Float = 0f,  // -180 .. 180
)
```

### 6. Smooth tone curve interpolation

Replace linear interpolation between control points with **Catmull-Rom spline** interpolation, which produces C1-continuous curves with natural tangent continuity.

```kotlin
private fun curveLookup(v: Float, curve: ToneCurve): Float {
    val pts = curve.points.sortedBy { it.first }
    if (v <= pts.first().first) return pts.first().second
    if (v >= pts.last().first) return pts.last().second

    // Find segment
    for (i in 0 until pts.size - 1) {
        val (x0, y0) = pts[i]
        val (x1, y1) = pts[i + 1]
        if (v in x0..x1) {
            val t = (v - x0) / (x1 - x0)
            // Catmull-Rom with natural boundary (linear extrapolation for tangents)
            val (t0, yPrev) = if (i > 0) pts[i - 1] else (2 * x0 - x1, 2 * y0 - y1)
            val (t2, yNext) = if (i < pts.size - 2) pts[i + 2] else (2 * x1 - x0, 2 * y1 - y0)
            return catmullRom(t, yPrev, y0, y1, yNext)
        }
    }
    return v
}

private fun catmullRom(t: Float, p0: Float, p1: Float, p2: Float, p3: Float): Float {
    val t2 = t * t
    val t3 = t2 * t
    return 0.5f * (
        (2 * p1) +
        (-p0 + p2) * t +
        (2 * p0 - 5 * p1 + 4 * p2 - p3) * t2 +
        (-p0 + 3 * p1 - 3 * p2 + p3) * t3
    )
}
```

Catmull-Rom produces smooth, natural curves that don't have the "kinks" of linear interpolation.

### 7. Per-stage clamping

Instead of one clamp at the end, clamp after every stage that can produce out-of-gamut values:

```kotlin
fun apply(input: Rgb, p: FilterParams): Rgb {
    var c = input
    c = srgbToLinearRgb(c)
    c = whiteBalance(c, p.whiteBalance).clamp()
    c = liftGammaGain(c, p.lift, p.gamma, p.gain).clamp()
    c = brightness(c, p.brightness).clamp()
    c = smoothContrast(c, p.contrast).clamp()
    c = channelMix(c, p.channelMixer).clamp()
    c = vibrance(c, p.saturation).clamp()
    c = rotateHue(c, p.hueShiftDegrees).clamp()
    c = applySplitToning(c, p.splitToning).clamp()
    c = applyToneCurve(c, p.toneCurve).clamp()
    c = linearToSrgbRgb(c)
    return c.clamp()
}

private fun Rgb.clamp() = Rgb(r.coerceIn(0f, 1f), g.coerceIn(0f, 1f), b.coerceIn(0f, 1f))
```

This prevents asymmetric channel distortion from intermediate overflow.

### 8. Per-filter default intensity

Add `defaultIntensity: Float = 1f` to `FilterParams` (baker) and `Filter` (runtime). Update `FilterCatalog` entries with appropriate defaults:

| Filter | Recommended defaultIntensity |
|---|---|
| All Food filters | 0.85 |
| All Portrait filters | 0.75 |
| `film_classic_cool` | 0.80 |
| `film_soft` | 0.90 |
| `film_warm_vintage` | 0.75 |
| `film_faded_negative` | 0.85 |
| `film_pushed_color` | 0.70 |
| `film_muted_frame` | 0.90 |
| All Travel filters | 0.80 |
| All Night filters | 0.75 |
| All Mono filters | 0.90 |

Rationale: subtle filters (`film_soft`, `mono_soft_bw`) can stay near 100%. Heavy stylized filters (`travel_teal_orange`, `night_moody_blue`) need to start gentler so the user dials up if they want drama.

## Recipe retuning strategy

The existing recipes were authored with the naive pipeline; their values are too aggressive for the new pipeline. General retuning rules:

1. **White balance**: Cut tempShift magnitudes by ~50%. Old `0.18f` → new `0.09f`. The multiplicative model is stronger.
2. **Saturation / vibrance**: Reduce by ~30%. Old `1.25f` → new `1.15f`. Vibrance preserves punch without oversaturating skin.
3. **Contrast**: Old values (`1.10f`, `1.18f`) assumed linear stretch; smooth S-curve at `1.08f` gives equivalent perceived pop. Reduce all by ~20%.
4. **Channel mixer**: Keep small adjustments (`±0.04f`) but avoid large shifts (`0.96f` → `0.98f`). The linear pipeline is more sensitive.
5. **Tone curves**: Simplify control points. Old recipes with 5 points can often achieve the same look with 3 well-placed points because Catmull-Rom is smoother.
6. **Hue shifts**: Add targeted shifts only where needed:
   - Food reds → orange: `hueShiftDegrees = 5f`
   - Travel greens → teal: `hueShiftDegrees = -3f` on some
   - Portrait pinks → subtle: `hueShiftDegrees = 2f`
7. **Lift / gain**: Reduce lift values by ~30% (`0.08f` → `0.05f`). Multiplicative lift in linear space has stronger shadow-tinting effect.

The exact tuned values will be determined during implementation (Task-level recipe authoring), guided by these rules and visual smoke-testing.

## Testing plan

### Unit tests (JVM, bakerTest)

1. **sRGB/linear roundtrip**: `linearToSrgb(srgbToLinear(v)) ≈ v` for sampled values.
2. **Multiplicative white balance preserves neutrals**: `whiteBalance(Rgb(0.5,0.5,0.5), wb)` → R≈G≈B.
3. **Smooth S-curve properties**: identity at `amount=1` (within eps); monotonic; output ∈ [0,1] for input ∈ [0,1].
4. **Vibrance identity**: `vibrance(c, 1f) == c`.
5. **Vibrance protects skin tones**: for a skin-tone RGB, `vibrance(c, 1.3f)` changes less than `saturate(c, 1.3f)`.
6. **Hue rotation roundtrip**: `rotateHue(rotateHue(c, 30f), -30f) ≈ c`.
7. **Catmull-Rom smoothness**: for a 3-point symmetric curve, midpoint matches exactly.
8. **Pipeline clamp invariants**: `apply(Rgb(0.5,0.5,0.5), FilterParams(brightness=2f)) == Rgb(1,1,1)`.
9. **Golden value test**: known input + known params → known output (precomputed with reference implementation).

### Regression tests

1. Run `./gradlew :app:bakeLuts` → all 29 `.cube` files generated without error.
2. `./gradlew :app:testDebugUnitTest` → all runtime tests pass (runtime code is unchanged).
3. `./gradlew :app:bakerUnitTest` → all baker tests pass.

### Manual smoke test

1. Build and install.
2. Open camera → cycle through all 29 filters in ribbon.
3. Verify: no filter looks "broken" (clipped channels, inverted colors, pure gray).
4. Capture with 3 filters (light, medium, heavy) → verify saved JPEG matches preview.
5. Open PhotoPreview → change filter/intensity → verify WYSIWYG.

## File changes

### Modify

- `app/src/bakerMain/kotlin/com/vcam/baker/Types.kt` — add `hueShiftDegrees` to `FilterParams`, add `defaultIntensity`.
- `app/src/bakerMain/kotlin/com/vcam/baker/ColorPipeline.kt` — replace entire pipeline with v2 math.
- `app/src/bakerMain/kotlin/com/vcam/baker/FilterRecipes.kt` — retune all 29 recipes, add `defaultIntensity` and `hueShiftDegrees` where appropriate.
- `app/src/bakerTest/kotlin/com/vcam/baker/ColorPipelineTest.kt` — expand tests for new pipeline.
- `app/src/main/java/com/vcam/color/FilterParams.kt` — add `hueShiftDegrees`, `defaultIntensity`.
- `app/src/main/java/com/vcam/color/Filter.kt` — add `defaultIntensity`.
- `app/src/main/java/com/vcam/color/FilterCatalog.kt` — populate `defaultIntensity` for each filter.
- `app/src/main/java/com/vcam/ui/camera/CameraViewModel.kt` — use `filter.defaultIntensity` on filter change.
- `app/src/main/java/com/vcam/ui/preview/PhotoPreviewViewModel.kt` — use `filter.defaultIntensity` on filter change.

### Generated (re-run `bakeLuts`)

- `app/src/main/assets/luts/*.cube` — all 29 files regenerated with new math.

## Performance impact

Build-time only. No runtime impact. `bakeLuts` may take ~10% longer due to additional math per pixel (sRGB↔linear, Catmull-Rom, HSV conversion). Still <2 s for 29×35937 pixels on a modern laptop.

## Migration notes

- Old `.cube` files are overwritten on `bakeLuts`. No migration needed.
- `FilterParams` gains new optional fields with defaults (`hueShiftDegrees = 0f`, `defaultIntensity = 1f`) — backward compatible.
- Runtime `FilterCatalog` gains `defaultIntensity` — existing callers that don't read it will just get the default 1f (unchanged behavior until UI is updated).

## Open questions

- Exact recipe values require visual tuning. The rules above are starting points; final values settled during implementation.
- Whether to expose `hueShiftDegrees` in the runtime `FilterParams` data class (yes, for future proofing even if UI doesn't expose it).
- Whether vibrance skin-mask formula needs calibration against real skin-tone photos — implemented as described; can be tuned later without changing architecture.

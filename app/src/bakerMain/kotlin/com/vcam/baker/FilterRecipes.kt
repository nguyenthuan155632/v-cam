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

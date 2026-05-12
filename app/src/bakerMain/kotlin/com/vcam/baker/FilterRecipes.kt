package com.vcam.baker

object FilterRecipes {
    val all: Map<String, FilterParams> = linkedMapOf(
        "food_fresh" to FilterParams(
            whiteBalance = WhiteBalance(0.05f, -0.02f),
            saturation = 1.18f, contrast = 1.08f, brightness = 0.02f,
            channelMixer = ChannelMixer(1.05f, 0f, 0f, 0f, 1.02f, 0f, 0f, 0f, 0.96f),
            toneCurve = ToneCurve(listOf(0f to 0f, 0.25f to 0.22f, 0.5f to 0.55f, 0.75f to 0.82f, 1f to 1f)),
        ),
        "food_sweet" to FilterParams(
            whiteBalance = WhiteBalance(0.10f, 0.04f),
            saturation = 1.22f, contrast = 1.04f,
            gain = Rgb(1.05f, 1.0f, 0.97f),
        ),
        "food_warm_table" to FilterParams(
            whiteBalance = WhiteBalance(0.18f, 0.04f),
            saturation = 1.10f, contrast = 1.06f,
            gain = Rgb(1.08f, 1.0f, 0.92f),
            toneCurve = ToneCurve(listOf(0f to 0.02f, 0.5f to 0.52f, 1f to 0.98f)),
        ),
        "food_creamy" to FilterParams(
            whiteBalance = WhiteBalance(0.08f, 0f),
            saturation = 0.98f, contrast = 0.95f, brightness = 0.04f,
            lift = Rgb(0.04f, 0.03f, 0.02f),
        ),
        "food_garden" to FilterParams(
            whiteBalance = WhiteBalance(-0.02f, 0.05f),
            saturation = 1.20f, contrast = 1.05f,
            channelMixer = ChannelMixer(0.98f, 0f, 0f, 0f, 1.06f, 0f, 0f, 0f, 0.98f),
        ),
        "portrait_clean_bright" to FilterParams(
            whiteBalance = WhiteBalance(0.04f, 0f),
            saturation = 1.05f, contrast = 1.02f, brightness = 0.04f,
            toneCurve = ToneCurve(listOf(0f to 0.02f, 0.5f to 0.52f, 1f to 1f)),
        ),
        "portrait_soft_skin" to FilterParams(
            whiteBalance = WhiteBalance(0.06f, -0.02f),
            saturation = 0.95f, contrast = 0.96f, brightness = 0.05f,
            lift = Rgb(0.04f, 0.03f, 0.02f),
            gain = Rgb(1.02f, 1.0f, 0.99f),
        ),
        "portrait_warm" to FilterParams(
            whiteBalance = WhiteBalance(0.14f, -0.02f),
            saturation = 1.05f, contrast = 1.04f,
            gain = Rgb(1.06f, 1.0f, 0.94f),
        ),
        "portrait_pink_tint" to FilterParams(
            whiteBalance = WhiteBalance(0.06f, -0.06f),
            saturation = 1.0f, contrast = 0.98f,
            channelMixer = ChannelMixer(1.04f, 0f, 0.02f, 0f, 1.0f, 0f, 0f, 0f, 1.02f),
        ),
        "portrait_studio_glow" to FilterParams(
            saturation = 1.02f, contrast = 0.94f, brightness = 0.06f,
            lift = Rgb(0.05f, 0.04f, 0.04f),
            toneCurve = ToneCurve(listOf(0f to 0.04f, 0.5f to 0.55f, 1f to 0.97f)),
        ),
        "film_classic_cool" to FilterParams(
            whiteBalance = WhiteBalance(-0.04f, 0f),
            saturation = 0.92f, contrast = 1.10f,
            channelMixer = ChannelMixer(1.0f, 0f, 0.04f, 0f, 1.0f, 0f, 0.04f, 0f, 1.0f),
            toneCurve = ToneCurve(listOf(0f to 0.04f, 0.25f to 0.22f, 0.5f to 0.5f, 0.75f to 0.78f, 1f to 0.96f)),
        ),
        "film_soft" to FilterParams(
            saturation = 0.85f, contrast = 0.92f, brightness = 0.04f,
            lift = Rgb(0.06f, 0.05f, 0.05f),
            gain = Rgb(0.96f, 0.96f, 0.96f),
        ),
        "film_warm_vintage" to FilterParams(
            whiteBalance = WhiteBalance(0.10f, 0.04f),
            saturation = 0.78f, contrast = 1.04f,
            gain = Rgb(1.04f, 0.98f, 0.88f),
            splitToning = SplitToning(Rgb(0.2f, 0.18f, 0.30f), Rgb(0.95f, 0.85f, 0.55f), balance = 0.55f),
        ),
        "film_faded_negative" to FilterParams(
            whiteBalance = WhiteBalance(-0.02f, 0.02f),
            saturation = 0.70f, contrast = 0.88f,
            lift = Rgb(0.08f, 0.06f, 0.05f),
            toneCurve = ToneCurve(listOf(0f to 0.10f, 0.5f to 0.5f, 1f to 0.92f)),
        ),
        "film_pushed_color" to FilterParams(
            saturation = 1.30f, contrast = 1.18f,
            channelMixer = ChannelMixer(1.05f, 0f, 0f, 0f, 1.0f, 0f, 0f, 0f, 1.05f),
        ),
        "film_muted_frame" to FilterParams(
            saturation = 0.88f, contrast = 0.96f, brightness = 0.02f,
            channelMixer = ChannelMixer(0.98f, 0.02f, 0f, 0f, 0.98f, 0.02f, 0.02f, 0f, 0.98f),
        ),
        "travel_summer_pop" to FilterParams(
            whiteBalance = WhiteBalance(0.06f, 0f),
            saturation = 1.25f, contrast = 1.10f, brightness = 0.02f,
        ),
        "travel_beach_bright" to FilterParams(
            whiteBalance = WhiteBalance(0.04f, 0f),
            saturation = 1.15f, contrast = 1.06f, brightness = 0.05f,
            channelMixer = ChannelMixer(1.0f, 0f, 0f, 0f, 1.04f, 0f, 0f, 0f, 1.06f),
        ),
        "travel_city_clear" to FilterParams(
            whiteBalance = WhiteBalance(-0.02f, 0f),
            saturation = 1.05f, contrast = 1.12f,
        ),
        "travel_teal_orange" to FilterParams(
            saturation = 1.10f, contrast = 1.08f,
            channelMixer = ChannelMixer(1.05f, 0f, -0.04f, 0f, 1.0f, 0f, -0.04f, 0f, 1.06f),
            splitToning = SplitToning(Rgb(0.05f, 0.4f, 0.5f), Rgb(0.95f, 0.6f, 0.25f), balance = 0.5f),
        ),
        "travel_golden_hour" to FilterParams(
            whiteBalance = WhiteBalance(0.16f, 0f),
            saturation = 1.12f, contrast = 1.04f,
            gain = Rgb(1.06f, 0.98f, 0.86f),
        ),
        "night_city" to FilterParams(
            whiteBalance = WhiteBalance(-0.04f, 0f),
            saturation = 1.10f, contrast = 1.18f, brightness = -0.04f,
            channelMixer = ChannelMixer(0.98f, 0f, 0f, 0f, 1.0f, 0f, 0f, 0f, 1.06f),
        ),
        "night_neon_soft" to FilterParams(
            whiteBalance = WhiteBalance(-0.02f, -0.04f),
            saturation = 1.30f, contrast = 1.10f, brightness = -0.02f,
            channelMixer = ChannelMixer(1.04f, 0f, 0f, 0f, 0.98f, 0f, 0f, 0.04f, 1.04f),
        ),
        "night_low_light_warm" to FilterParams(
            whiteBalance = WhiteBalance(0.08f, 0f),
            saturation = 1.05f, contrast = 1.10f, brightness = 0.04f,
            lift = Rgb(0.04f, 0.03f, 0.02f),
        ),
        "night_moody_blue" to FilterParams(
            whiteBalance = WhiteBalance(-0.10f, 0f),
            saturation = 0.95f, contrast = 1.20f, brightness = -0.06f,
            channelMixer = ChannelMixer(0.94f, 0f, 0f, 0f, 0.98f, 0f, 0f, 0f, 1.10f),
        ),
        "mono_soft_bw" to FilterParams(
            saturation = 0f, contrast = 0.95f, brightness = 0.02f,
            toneCurve = ToneCurve(listOf(0f to 0.04f, 0.5f to 0.52f, 1f to 0.96f)),
        ),
        "mono_high_contrast_bw" to FilterParams(
            saturation = 0f, contrast = 1.40f,
            toneCurve = ToneCurve(listOf(0f to 0f, 0.25f to 0.10f, 0.5f to 0.5f, 0.75f to 0.90f, 1f to 1f)),
        ),
        "mono_noir" to FilterParams(
            saturation = 0f, contrast = 1.55f, brightness = -0.04f,
            toneCurve = ToneCurve(listOf(0f to 0f, 0.4f to 0.20f, 0.6f to 0.78f, 1f to 1f)),
        ),
        "mono_warm" to FilterParams(
            saturation = 0f, contrast = 1.05f,
            splitToning = SplitToning(Rgb(0.18f, 0.12f, 0.08f), Rgb(0.96f, 0.90f, 0.78f), balance = 0.55f),
        ),
    )
}

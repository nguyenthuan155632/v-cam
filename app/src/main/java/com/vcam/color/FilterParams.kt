package com.vcam.color

data class Rgb(val r: Float, val g: Float, val b: Float)

data class WhiteBalance(val tempShift: Float = 0f, val tintShift: Float = 0f)

data class ChannelMixer(
    val rr: Float, val rg: Float, val rb: Float,
    val gr: Float, val gg: Float, val gb: Float,
    val br: Float, val bg: Float, val bb: Float,
) {
    companion object {
        fun identity() = ChannelMixer(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
    }
}

data class ToneCurve(val points: List<Pair<Float, Float>>) {
    companion object {
        fun linear() = ToneCurve(listOf(0f to 0f, 1f to 1f))
    }
}

data class SplitToning(val shadowTint: Rgb, val highlightTint: Rgb, val balance: Float = 0.5f)

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
)

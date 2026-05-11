package com.vcam.data.settings

enum class AppTheme { Light, Dark, System }

enum class AspectRatio(val label: String) {
    Ratio1x1("1:1"),
    Ratio4x3("4:3"),
    Ratio16x9("16:9"),
    Full("FULL");

    companion object {
        fun fromLabel(label: String): AspectRatio =
            entries.firstOrNull { it.label == label } ?: Ratio4x3
    }
}

data class UserSettings(
    val saveOriginal: Boolean = true,
    val autoSaveToGallery: Boolean = true,
    val gridLines: Boolean = false,
    val cameraSound: Boolean = false,
    val defaultAspectRatio: AspectRatio = AspectRatio.Ratio4x3,
    val defaultFilterId: String = "fd01",
    val defaultIntensity: Int = 80,
    val theme: AppTheme = AppTheme.Light,
    val version: String = "2.4.1",
)

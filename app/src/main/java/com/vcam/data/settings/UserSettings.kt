package com.vcam.data.settings

import androidx.camera.core.AspectRatio.RATIO_16_9
import androidx.camera.core.AspectRatio.RATIO_4_3

enum class AppTheme { Light, Dark, System }

enum class AspectRatio(val label: String, val previewAspect: Float?, val cameraAspectRatio: Int?) {
    Ratio1x1("1:1", 1f, null),
    Ratio4x3("4:3", 3f / 4f, RATIO_4_3),
    Ratio16x9("16:9", 9f / 16f, RATIO_16_9),
    Full("FULL", null, null);

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

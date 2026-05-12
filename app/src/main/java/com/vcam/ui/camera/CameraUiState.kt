package com.vcam.ui.camera

import androidx.camera.core.ImageCapture
import com.vcam.data.settings.AspectRatio

enum class FlashSetting(val label: String, val cxFlash: Int) {
    Auto("AUTO", ImageCapture.FLASH_MODE_AUTO),
    On("ON", ImageCapture.FLASH_MODE_ON),
    Off("OFF", ImageCapture.FLASH_MODE_OFF);

    fun next(): FlashSetting = entries[(ordinal + 1) % entries.size]
}

enum class TimerSetting(val seconds: Int, val label: String) {
    Off(0, "OFF"),
    Three(3, "3s"),
    Ten(10, "10s");

    fun next(): TimerSetting = entries[(ordinal + 1) % entries.size]
}

data class CameraUiState(
    val flash: FlashSetting = FlashSetting.Auto,
    val timer: TimerSetting = TimerSetting.Off,
    val aspectRatio: AspectRatio = AspectRatio.Ratio4x3,
    val gridOn: Boolean = true,
    val activeFilterIndex: Int = 0,
    val intensity: Int = 80,
    val saveOriginal: Boolean = true,
    val frontFacing: Boolean = false,
    val intensitySheetOpen: Boolean = false,
)

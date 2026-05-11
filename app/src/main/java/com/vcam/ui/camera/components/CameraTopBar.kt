package com.vcam.ui.camera.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vcam.data.settings.AspectRatio
import com.vcam.theme.VColors
import com.vcam.ui.camera.FlashSetting
import com.vcam.ui.camera.TimerSetting
import com.vcam.ui.icons.VIcons

@Composable
fun CameraTopBar(
    flash: FlashSetting,
    timer: TimerSetting,
    ratio: AspectRatio,
    accent: Color = VColors.Coral,
    onSettings: () -> Unit,
    onGrid: () -> Unit,
    onFlashClick: () -> Unit,
    onTimerClick: () -> Unit,
    onRatioClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CameraIconButton(
            icon = VIcons.Settings,
            contentDescription = "Settings",
            onClick = onSettings,
            accent = accent,
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CameraChip(text = flash.label, onClick = onFlashClick)
            CameraChip(text = timer.label, onClick = onTimerClick)
            CameraChip(text = ratio.label, onClick = onRatioClick)
        }
        CameraIconButton(
            icon = VIcons.Grid,
            contentDescription = "Grid",
            onClick = onGrid,
            accent = accent,
        )
    }
}

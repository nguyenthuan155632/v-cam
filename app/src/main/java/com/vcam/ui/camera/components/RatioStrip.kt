package com.vcam.ui.camera.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vcam.data.settings.AspectRatio
import com.vcam.theme.VColors
import com.vcam.theme.VType

@Composable
fun RatioStrip(
    value: AspectRatio,
    accent: Color,
    onSelect: (AspectRatio) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally),
    ) {
        AspectRatio.entries.forEach { r ->
            val color = if (r == value) accent else VColors.White65
            Text(
                text = r.label,
                style = VType.MonoLarge,
                color = color,
                modifier = Modifier.clickable { onSelect(r) },
            )
        }
    }
}

package com.vcam.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vcam.theme.VColors
import com.vcam.theme.VType
import com.vcam.ui.icons.VIcons

@Composable
fun ValueChev(value: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(value, style = VType.SecondaryLarge, color = VColors.Ink50)
        Icon(
            VIcons.ChevronRight,
            contentDescription = null,
            tint = VColors.Ink30,
            modifier = Modifier.size(14.dp),
        )
    }
}

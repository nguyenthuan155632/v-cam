package com.vcam.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vcam.data.settings.AppTheme
import com.vcam.theme.VColors
import com.vcam.theme.VType

@Composable
fun ThemeSegmented(
    value: AppTheme,
    onValueChange: (AppTheme) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(VColors.Ink06)
            .padding(2.dp),
    ) {
        AppTheme.entries.forEach { t ->
            val active = t == value
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .then(
                        if (active) Modifier
                            .shadow(1.dp, RoundedCornerShape(8.dp))
                            .background(VColors.Paper)
                        else Modifier
                    )
                    .clickable { onValueChange(t) }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = t.name,
                    style = VType.SecondarySmall.copy(
                        fontSize = 11.5.sp,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                    ),
                    color = if (active) VColors.Ink else VColors.Ink70,
                )
            }
        }
    }
}

package com.vcam.ui.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vcam.theme.DmSans
import com.vcam.theme.VColors
import com.vcam.theme.VType

private val SubStyle = TextStyle(
    fontFamily = DmSans,
    fontWeight = FontWeight.Medium,
    fontSize = 11.5.sp,
)

@Composable
fun SettingsRow(
    label: String,
    sub: String? = null,
    last: Boolean = false,
    control: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(label, style = VType.Body, color = VColors.Ink)
                if (sub != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(sub, style = SubStyle, color = VColors.Ink50)
                }
            }
            control()
        }
        if (!last) {
            HorizontalDivider(thickness = 0.5.dp, color = VColors.Divider)
        }
    }
}

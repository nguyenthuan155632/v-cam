package com.vcam.ui.settings.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vcam.theme.VColors

/** Material-style toggle: 38×22 track, 18×18 thumb, accent fill when on. */
@Composable
fun VToggle(
    on: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = VColors.Coral,
) {
    val trackColor by animateColorAsState(
        targetValue = if (on) accent else VColors.Ink12,
        label = "track",
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (on) 18.dp else 2.dp,
        label = "thumb",
    )
    Box(
        modifier
            .size(width = 38.dp, height = 22.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(trackColor)
            .clickable { onCheckedChange(!on) }
    ) {
        Box(
            Modifier
                .offset(x = thumbOffset, y = 2.dp)
                .size(18.dp)
                .shadow(2.dp, RoundedCornerShape(9.dp))
                .clip(RoundedCornerShape(9.dp))
                .background(Color.White)
        )
    }
}

package com.vcam.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcam.VCamApplication
import com.vcam.data.Filters
import com.vcam.theme.VColors
import com.vcam.theme.VType
import com.vcam.ui.components.IntensitySlider
import com.vcam.ui.components.PhotoKind
import com.vcam.ui.components.photoBrush
import com.vcam.ui.components.photoKindAt
import com.vcam.ui.icons.VIcons

object PhotoPreviewActions {
    fun save(onSave: () -> Unit) = onSave()
}

@Composable
fun PhotoPreviewScreen(
    photoId: String,
    onClose: () -> Unit,
    onRetake: () -> Unit,
    onSave: () -> Unit = onClose,
) {
    val context = LocalContext.current
    val app = context.applicationContext as VCamApplication
    val vm: PhotoPreviewViewModel = viewModel(
        factory = PhotoPreviewViewModel.Factory(context, app.settingsRepo)
    )
    val state by vm.state.collectAsState()
    val activeFilter = Filters.getOrElse(state.activeFilterIndex) { Filters.first() }

    LaunchedEffect(photoId) { vm.loadPhoto(photoId) }

    Column(
        Modifier
            .fillMaxSize()
            .background(VColors.Paper)
            .statusBarsPadding()
    ) {
        // Top bar
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.clickable { onClose() }) {
                Icon(VIcons.Close, contentDescription = "Close", tint = VColors.Ink, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("JUST CAPTURED", style = VType.MonoLarge, color = VColors.Ink50)
                Text("${state.timestamp} · ${state.dimensions}", style = VType.SecondarySmall, color = VColors.Ink70)
            }
            Spacer(Modifier.weight(1f))
            Box(Modifier.clickable { vm.toggleStar() }) {
                Icon(
                    imageVector = if (state.starred) VIcons.Star else VIcons.StarOutline,
                    contentDescription = "Favorite",
                    tint = if (state.starred) VColors.Coral else VColors.Ink30,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        // Photo (4:3) — render captured bitmap when available, fall back to gradient.
        Box(
            Modifier
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(photoBrush(PhotoKind.Pancakes))
        ) {
            val bmp = state.bitmap
            if (bmp != null) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Captured photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(activeFilter.tintTop, activeFilter.tintBottom)))
                    .graphicsLayer { alpha = state.intensity / 100f }
            )
            // Code + intensity pill, top-left
            Box(
                Modifier
                    .padding(12.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(VColors.DarkGlass55)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    "${activeFilter.code} · ${state.intensity}%",
                    style = VType.Mono,
                    color = Color.White,
                )
            }
        }

        // Edit filter row
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text("Edit filter", style = VType.SecondarySmall, color = VColors.Ink70)
                Text(activeFilter.category.uppercase(), style = VType.MonoLarge, color = VColors.Ink50)
            }
            Spacer(Modifier.height(8.dp))
            LazyRow {
                itemsIndexed(Filters.take(8)) { i, f ->
                    val active = i == state.activeFilterIndex
                    Column(
                        Modifier
                            .padding(end = 10.dp)
                            .clickable { vm.setFilter(i) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(photoBrush(photoKindAt(i)))
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(Brush.linearGradient(listOf(f.tintTop, f.tintBottom)))
                                    .graphicsLayer { alpha = 0.85f }
                            )
                            if (active) {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .border(2.dp, VColors.Coral, RoundedCornerShape(12.dp))
                                )
                            }
                        }
                        Spacer(Modifier.height(5.dp))
                        Text(
                            f.name,
                            style = VType.Caption.copy(
                                fontSize = 10.sp,
                                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                            ),
                            color = if (active) VColors.Ink else VColors.Ink50,
                        )
                    }
                }
            }
        }

        // Intensity slider
        Column(Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text("Intensity", style = VType.SecondarySmall, color = VColors.Ink70)
                Text(state.intensity.toString(), style = VType.MonoValue, color = VColors.Ink)
            }
            Spacer(Modifier.height(8.dp))
            IntensitySlider(
                value = state.intensity,
                onValueChange = vm::setIntensity,
                accent = VColors.Coral,
            )
        }

        Spacer(Modifier.weight(1f))

        // Action row: Retake + Save photo (1.4× wider)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .systemBarsPadding()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, VColors.Ink12, RoundedCornerShape(14.dp))
                    .background(VColors.Paper)
                    .clickable { onRetake() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(VIcons.Retake, contentDescription = null, tint = VColors.Ink70, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(8.dp))
                Text("Retake", style = VType.BodySemi, color = VColors.Ink70)
            }
            Row(
                modifier = Modifier
                    .weight(1.4f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(VColors.Ink)
                    .clickable { PhotoPreviewActions.save(onSave) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(VIcons.Download, contentDescription = null, tint = VColors.Paper, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(8.dp))
                Text("Save photo", style = VType.BodySemi, color = VColors.Paper)
            }
        }
    }
}

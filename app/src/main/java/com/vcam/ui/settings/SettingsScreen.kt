package com.vcam.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vcam.VCamApplication
import com.vcam.data.Filters
import com.vcam.theme.VColors
import com.vcam.theme.VType
import com.vcam.ui.icons.VIcons
import com.vcam.ui.settings.components.SettingsRow
import com.vcam.ui.settings.components.ThemeSegmented
import com.vcam.ui.settings.components.VToggle
import com.vcam.ui.settings.components.ValueChev

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as VCamApplication
    val vm: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(app.settingsRepo))
    val state by vm.settings.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(VColors.Paper)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Top bar
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.clickable { onBack() }) {
                Icon(VIcons.Back, contentDescription = "Back", tint = VColors.Ink, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.weight(1f))
            Text("Settings", style = VType.BodySemi, color = VColors.Ink)
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(22.dp))
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // CAPTURE
            SectionLabel("CAPTURE")
            GroupedCard {
                SettingsRow(
                    label = "Save original photo",
                    sub = "Keep an unfiltered copy alongside",
                    control = { VToggle(on = state.saveOriginal, onCheckedChange = vm::toggleSaveOriginal) },
                )
                SettingsRow(
                    label = "Auto-save to gallery",
                    control = { VToggle(on = state.autoSaveToGallery, onCheckedChange = vm::toggleAutoSave) },
                )
                SettingsRow(
                    label = "Grid lines",
                    sub = "Rule-of-thirds overlay",
                    control = { VToggle(on = state.gridLines, onCheckedChange = vm::toggleGridLines) },
                )
                SettingsRow(
                    label = "Camera sound",
                    last = true,
                    control = { VToggle(on = state.cameraSound, onCheckedChange = vm::toggleCameraSound) },
                )
            }

            // DEFAULTS
            SectionLabel("DEFAULTS")
            GroupedCard {
                SettingsRow(
                    label = "Default aspect ratio",
                    control = { ValueChev(state.defaultAspectRatio.label) },
                )
                val defaultFilterName = Filters.firstOrNull { it.id == state.defaultFilterId }?.name ?: "Crisp 01"
                SettingsRow(
                    label = "Default filter",
                    control = { ValueChev(defaultFilterName) },
                )
                SettingsRow(
                    label = "Default intensity",
                    last = true,
                    control = { ValueChev(state.defaultIntensity.toString()) },
                )
            }

            // APPEARANCE
            SectionLabel("APPEARANCE")
            GroupedCard {
                SettingsRow(
                    label = "App theme",
                    last = true,
                    control = { ThemeSegmented(value = state.theme, onValueChange = vm::setTheme) },
                )
            }

            // ABOUT
            SectionLabel("ABOUT", topPadding = 22.dp)
            GroupedCard {
                SettingsRow(
                    label = "Version",
                    last = true,
                    control = { Text(state.version, style = VType.MonoValue, color = VColors.Ink50) },
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String, topPadding: androidx.compose.ui.unit.Dp = 18.dp) {
    Text(
        text = text,
        style = VType.Mono,
        color = VColors.Ink50,
        modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = topPadding, bottom = 6.dp),
    )
}

@Composable
private fun GroupedCard(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(VColors.PaperWarm),
    ) {
        HorizontalDivider(thickness = 0.5.dp, color = VColors.Divider)
        content()
        HorizontalDivider(thickness = 0.5.dp, color = VColors.Divider)
    }
}

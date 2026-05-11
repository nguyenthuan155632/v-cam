package com.vcam.ui.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
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
import com.vcam.data.Categories
import com.vcam.data.filtersInCategory
import com.vcam.theme.VColors
import com.vcam.theme.VType
import com.vcam.ui.components.IntensitySlider
import com.vcam.ui.filters.components.CategoryPills
import com.vcam.ui.filters.components.FilterGrid
import com.vcam.ui.filters.components.HeroPreview
import com.vcam.ui.icons.VIcons

@Composable
fun FilterBrowserScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as VCamApplication
    val vm: FilterBrowserViewModel = viewModel(factory = FilterBrowserViewModel.Factory(app.settingsRepo))
    val state by vm.state.collectAsState()
    val filters = filtersInCategory(state.activeCategory)
    val activeFilter = filters.getOrElse(state.activeFilterIndexInCat) { filters.firstOrNull() }

    Column(
        Modifier
            .fillMaxSize()
            .background(VColors.Paper)
            .statusBarsPadding()
    ) {
        // Header
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("FILTERS", style = VType.MonoLarge, color = VColors.Ink50)
                Text("Library", style = VType.SectionHeader, color = VColors.Ink)
            }
            Spacer(Modifier.weight(1f))
            Box {
                Icon(VIcons.Search, contentDescription = "Search", tint = VColors.Ink, modifier = Modifier.size(18.dp))
            }
        }

        CategoryPills(
            categories = Categories,
            active = state.activeCategory,
            onSelect = vm::setCategory,
        )
        Spacer(Modifier.height(12.dp))

        // Hero
        if (activeFilter != null) {
            Box(Modifier.padding(horizontal = 14.dp)) {
                HeroPreview(filter = activeFilter, intensity = state.intensity)
            }
        }

        // Intensity slider
        Column(Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
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

        // Grid
        FilterGrid(
            filters = filters,
            activeIndex = state.activeFilterIndexInCat,
            accent = VColors.Coral,
            onSelect = vm::setActiveFilterInCat,
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
        )
    }
}

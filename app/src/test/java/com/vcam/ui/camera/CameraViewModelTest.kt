package com.vcam.ui.camera

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.vcam.color.FilterCatalog
import com.vcam.data.settings.AspectRatio
import com.vcam.data.settings.SettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CameraViewModelTest {
    @Test
    fun setActiveFilterByIdClampsUnknownToFirstCatalogEntry() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val vm = CameraViewModel(SettingsRepository(context))
        vm.setActiveFilterId("bogus_filter")
        val first = FilterCatalog.all.first().id
        assertEquals(first, vm.state.value.activeFilterId)
    }

    @Test
    fun directAspectSelectionUsesTappedRatioInsteadOfCycling() {
        assertEquals(
            AspectRatio.Ratio1x1,
            CameraViewModel.selectAspectRatio(
                current = AspectRatio.Ratio4x3,
                selected = AspectRatio.Ratio1x1,
            ),
        )
    }
}

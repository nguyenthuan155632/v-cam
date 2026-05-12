package com.vcam.ui.camera

import com.vcam.data.settings.AspectRatio
import org.junit.Assert.assertEquals
import org.junit.Test

class CameraViewModelTest {
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

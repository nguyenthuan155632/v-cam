package com.vcam.data.settings

import androidx.camera.core.AspectRatio.RATIO_16_9
import androidx.camera.core.AspectRatio.RATIO_4_3
import org.junit.Assert.assertEquals
import org.junit.Test

class AspectRatioTest {
    @Test
    fun exposesPreviewAspectValuesForCameraViewfinder() {
        assertEquals(1f, AspectRatio.Ratio1x1.previewAspect)
        assertEquals(3f / 4f, AspectRatio.Ratio4x3.previewAspect)
        assertEquals(9f / 16f, AspectRatio.Ratio16x9.previewAspect)
        assertEquals(null, AspectRatio.Full.previewAspect)
    }

    @Test
    fun includesDesignAspectRatioLabelsInSelectionOrder() {
        assertEquals(
            listOf("1:1", "4:3", "16:9", "FULL"),
            AspectRatio.entries.map { it.label },
        )
    }

    @Test
    fun mapsCameraNativeRatiosOnlyToCameraXTargets() {
        assertEquals(null, AspectRatio.Ratio1x1.cameraAspectRatio)
        assertEquals(RATIO_4_3, AspectRatio.Ratio4x3.cameraAspectRatio)
        assertEquals(RATIO_16_9, AspectRatio.Ratio16x9.cameraAspectRatio)
        assertEquals(null, AspectRatio.Full.cameraAspectRatio)
    }
}

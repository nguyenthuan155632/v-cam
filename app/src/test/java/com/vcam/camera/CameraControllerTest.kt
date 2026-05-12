package com.vcam.camera

import android.content.Context
import androidx.camera.core.AspectRatio
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CameraControllerTest {
    @Test
    fun imageCaptureJpegQualityIs100() {
        assertEquals(100, CameraController.imageCaptureJpegQuality)
    }

    @Test
    fun resolutionSelectorUsesPreferredAspectRatioWithAutoFallback() {
        val ratio4x3 = CameraController.resolutionSelectorFor(AspectRatio.RATIO_4_3)
        val ratio16x9 = CameraController.resolutionSelectorFor(AspectRatio.RATIO_16_9)

        assertEquals(AspectRatio.RATIO_4_3, ratio4x3?.aspectRatioStrategy?.preferredAspectRatio)
        assertEquals(AspectRatioStrategy.FALLBACK_RULE_AUTO, ratio4x3?.aspectRatioStrategy?.fallbackRule)
        assertEquals(AspectRatio.RATIO_16_9, ratio16x9?.aspectRatioStrategy?.preferredAspectRatio)
        assertEquals(AspectRatioStrategy.FALLBACK_RULE_AUTO, ratio16x9?.aspectRatioStrategy?.fallbackRule)
        assertNull(CameraController.resolutionSelectorFor(null))
    }

    @Test
    fun canFocusAndMeterAtAcceptsCoordinatesInsidePositiveBounds() {
        assertEquals(true, CameraController.canFocusAndMeterAt(0f, 0f, 1080, 1440))
        assertEquals(true, CameraController.canFocusAndMeterAt(1079.9f, 1439.9f, 1080, 1440))
        assertEquals(true, CameraController.canFocusAndMeterAt(540f, 720f, 1080, 1440))
    }

    @Test
    fun canFocusAndMeterAtRejectsInvalidCoordinatesAndBounds() {
        assertEquals(false, CameraController.canFocusAndMeterAt(-0.1f, 10f, 1080, 1440))
        assertEquals(false, CameraController.canFocusAndMeterAt(10f, -0.1f, 1080, 1440))
        assertEquals(false, CameraController.canFocusAndMeterAt(1080f, 10f, 1080, 1440))
        assertEquals(false, CameraController.canFocusAndMeterAt(10f, 1440f, 1080, 1440))
        assertEquals(false, CameraController.canFocusAndMeterAt(10f, 10f, 0, 1440))
        assertEquals(false, CameraController.canFocusAndMeterAt(10f, 10f, 1080, 0))
    }

    @Test
    fun focusAndMeterAtReturnsFalseBeforeCameraIsBound() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val owner = TestLifecycleOwner()
        val controller = CameraController(context, owner)

        assertEquals(false, controller.focusAndMeterAt(10f, 10f, 100, 100))

        controller.release()
    }

    @Test
    fun clampExposureIndexKeepsRequestedIndexInsideCameraRange() {
        assertEquals(-2, CameraController.clampExposureIndex(-5, -2, 3))
        assertEquals(3, CameraController.clampExposureIndex(5, -2, 3))
        assertEquals(1, CameraController.clampExposureIndex(1, -2, 3))
    }

    @Test
    fun exposureCompensationStateReturnsUnsupportedBeforeCameraIsBound() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val owner = TestLifecycleOwner()
        val controller = CameraController(context, owner)

        assertEquals(CameraController.ExposureCompensationState.Unsupported, controller.exposureCompensationState())

        controller.release()
    }

    @Test
    fun setExposureCompensationIndexReturnsFalseBeforeCameraIsBound() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val owner = TestLifecycleOwner()
        val controller = CameraController(context, owner)

        assertEquals(false, controller.setExposureCompensationIndex(1))

        controller.release()
    }
}

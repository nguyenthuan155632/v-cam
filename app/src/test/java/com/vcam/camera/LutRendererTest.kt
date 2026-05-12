package com.vcam.camera

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LutRendererTest {
    @Test
    fun surfaceReadyGateIgnoresPlaceholderSizeUntilRealSurfaceSizeArrives() {
        val gate = SurfaceReadyGate()

        assertFalse(gate.markSurfaceTextureReady(1, 1))
        assertFalse(gate.markSurfaceSizeChanged(1, 1))
        assertTrue(gate.markSurfaceSizeChanged(1080, 1440))
        assertFalse(gate.markSurfaceSizeChanged(1440, 1080))
        assertEquals(1080 to 1440, gate.readySize)
    }

    @Test
    fun displayedSourceAspectSwapsLandscapeBufferForPortraitViewport() {
        assertEquals(
            9f / 16f,
            TextureCrop.displayedSourceAspect(
                sourceWidth = 1920,
                sourceHeight = 1080,
                viewportWidth = 1080,
                viewportHeight = 1920,
            ),
            0.0001f,
        )
    }

    @Test
    fun centerCropNarrowsWideDisplayedSourceForSquareViewport() {
        val crop = TextureCrop.centerCrop(
            sourceAspect = 16f / 9f,
            viewportWidth = 1000,
            viewportHeight = 1000,
        )

        assertEquals(0.21875f, crop.uMin, 0.0001f)
        assertEquals(0.78125f, crop.uMax, 0.0001f)
        assertEquals(0f, crop.vMin, 0.0001f)
        assertEquals(1f, crop.vMax, 0.0001f)
    }

    @Test
    fun centerCropKeepsFullTextureWhenDisplayedSourceMatchesViewport() {
        val crop = TextureCrop.centerCrop(
            sourceAspect = 9f / 16f,
            viewportWidth = 1080,
            viewportHeight = 1920,
        )

        assertEquals(0f, crop.uMin, 0.0001f)
        assertEquals(1f, crop.uMax, 0.0001f)
        assertEquals(0f, crop.vMin, 0.0001f)
        assertEquals(1f, crop.vMax, 0.0001f)
    }
}

package com.vcam.baker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import javax.imageio.ImageIO

class ReferenceImageBakerTest {
    @get:Rule val tmp = TemporaryFolder()

    @Test
    fun bakesA256x256Png() {
        val out = tmp.newFile("reference.png")
        ReferenceImageBaker.bake(out)
        assertTrue(out.exists() && out.length() > 0)
        val img = ImageIO.read(out)
        assertEquals(256, img.width)
        assertEquals(256, img.height)
    }

    @Test
    fun firstRowContainsGrayscaleRamp() {
        val out = tmp.newFile("reference.png")
        ReferenceImageBaker.bake(out)
        val img = ImageIO.read(out)
        val left = img.getRGB(10, 0) and 0xFF
        val right = img.getRGB(245, 0) and 0xFF
        assertTrue("left=$left right=$right", right > left + 100)
    }
}

package com.vcam.color

import android.graphics.Bitmap
import android.graphics.Color
import com.vcam.camera.CubeLut
import com.vcam.camera.identityCubeLut
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CpuLutApplyTest {

    private fun bmp(w: Int, h: Int, colors: IntArray): Bitmap {
        val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        b.setPixels(colors, 0, w, 0, 0, w, h)
        return b
    }

    @Test
    fun identityLutPreservesPixels() {
        val src = bmp(2, 1, intArrayOf(Color.rgb(120, 30, 220), Color.rgb(10, 250, 60)))
        val out = applyLutCpuTrilinear(src, identityCubeLut(33), intensity = 1f)
        assertNear(Color.rgb(120, 30, 220), out.getPixel(0, 0), 1)
        assertNear(Color.rgb(10, 250, 60), out.getPixel(1, 0), 1)
    }

    @Test
    fun intensityZeroReturnsSource() {
        val src = bmp(1, 1, intArrayOf(Color.rgb(200, 100, 50)))
        val invert = CubeLut(2, floatArrayOf(
            1f, 1f, 1f, 0f, 1f, 1f, 1f, 0f, 1f, 0f, 0f, 1f,
            1f, 1f, 0f, 0f, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 0f,
        ))
        val out = applyLutCpuTrilinear(src, invert, intensity = 0f)
        assertEquals(Color.rgb(200, 100, 50), out.getPixel(0, 0))
    }

    private fun assertNear(expected: Int, actual: Int, tol: Int) {
        val er = (expected shr 16) and 0xff
        val eg = (expected shr 8) and 0xff
        val eb = expected and 0xff
        val ar = (actual shr 16) and 0xff
        val ag = (actual shr 8) and 0xff
        val ab = actual and 0xff
        assertEquals("R", er.toFloat(), ar.toFloat(), tol.toFloat())
        assertEquals("G", eg.toFloat(), ag.toFloat(), tol.toFloat())
        assertEquals("B", eb.toFloat(), ab.toFloat(), tol.toFloat())
    }
}

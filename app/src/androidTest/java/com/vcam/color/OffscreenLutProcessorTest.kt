package com.vcam.color

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vcam.camera.CubeLut
import com.vcam.camera.identityCubeLut
import kotlin.coroutines.resume
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OffscreenLutProcessorTest {
    private lateinit var processor: OffscreenLutProcessor

    @Before fun setUp() {
        processor = OffscreenLutProcessor()
    }

    @After fun tearDown() {
        processor.release()
    }

    private suspend fun process(bitmap: Bitmap, lut: CubeLut, intensity: Float): Bitmap? =
        suspendCancellableCoroutine { cont ->
            processor.process(bitmap, lut, intensity) { cont.resume(it) }
        }

    @Test fun identityLutRoundTripsPixels() = runBlocking {
        val src = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888).apply {
            for (y in 0 until 64) {
                for (x in 0 until 64) {
                    setPixel(x, y, Color.rgb(x * 4, y * 4, (x + y) and 0xff))
                }
            }
        }

        val out = process(src, identityCubeLut(33), 1f)

        assertNotNull(out)
        for (y in intArrayOf(0, 32, 63)) {
            for (x in intArrayOf(0, 32, 63)) {
                val expected = src.getPixel(x, y)
                val actual = out!!.getPixel(x, y)
                assertNear("R($x,$y)", Color.red(expected), Color.red(actual))
                assertNear("G($x,$y)", Color.green(expected), Color.green(actual))
                assertNear("B($x,$y)", Color.blue(expected), Color.blue(actual))
            }
        }
    }

    private fun assertNear(label: String, expected: Int, actual: Int) {
        assertTrue("$label expected=$expected actual=$actual", kotlin.math.abs(expected - actual) <= 2)
    }
}

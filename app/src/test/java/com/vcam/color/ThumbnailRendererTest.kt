package com.vcam.color

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ThumbnailRendererTest {
    private val ctx: Context = ApplicationProvider.getApplicationContext()

    @Test fun firstCallProducesBitmap() = runBlocking {
        val r = ThumbnailRenderer(ctx, LutCache())
        val filter = FilterCatalog.byId("food_fresh")!!
        val bmp = r.thumbnailFor(filter)
        assertNotNull(bmp)
        assertEquals(256, bmp.width)
        assertEquals(256, bmp.height)
    }

    @Test fun secondCallHitsCache() = runBlocking {
        val r = ThumbnailRenderer(ctx, LutCache())
        val filter = FilterCatalog.byId("mono_noir")!!
        val a = r.thumbnailFor(filter)
        val b = r.thumbnailFor(filter)
        assertSame(a, b)
    }
}

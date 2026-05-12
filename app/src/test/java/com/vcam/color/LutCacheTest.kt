package com.vcam.color

import com.vcam.camera.CubeLut
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class LutCacheTest {
    @Test
    fun evictsLeastRecentlyUsed() {
        val cache = LutCache(maxEntries = 2)
        val a = CubeLut(2, FloatArray(2 * 2 * 2 * 3))
        val b = CubeLut(2, FloatArray(2 * 2 * 2 * 3))
        val c = CubeLut(2, FloatArray(2 * 2 * 2 * 3))
        cache.put("a", a)
        cache.put("b", b)
        cache.put("c", c)
        assertEquals(null, cache.get("a"))
        assertSame(b, cache.get("b"))
        assertSame(c, cache.get("c"))
    }

    @Test
    fun hitMovesToFront() {
        val cache = LutCache(maxEntries = 2)
        val a = CubeLut(2, FloatArray(24))
        val b = CubeLut(2, FloatArray(24))
        val c = CubeLut(2, FloatArray(24))
        cache.put("a", a)
        cache.put("b", b)
        cache.get("a")
        cache.put("c", c)
        assertSame(a, cache.get("a"))
        assertEquals(null, cache.get("b"))
    }
}

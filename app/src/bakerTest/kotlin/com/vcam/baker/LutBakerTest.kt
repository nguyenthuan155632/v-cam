package com.vcam.baker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class LutBakerTest {

    @get:Rule val tmp = TemporaryFolder()

    @Test
    fun bakeIdentityProducesIdentityCube() {
        val lut = LutBaker.bake(FilterParams())
        assertEquals(LutBaker.LUT_SIZE, lut.size)
        val n = lut.size
        assertEquals(0f, lut.data[0], 1e-4f)
        assertEquals(0f, lut.data[1], 1e-4f)
        assertEquals(0f, lut.data[2], 1e-4f)
        val last = (n * n * n - 1) * 3
        assertEquals(1f, lut.data[last], 1e-4f)
        assertEquals(1f, lut.data[last + 1], 1e-4f)
        assertEquals(1f, lut.data[last + 2], 1e-4f)
    }

    @Test
    fun writeCubeFileProducesParseableHeader() {
        val lut = LutBaker.bake(FilterParams())
        val out = tmp.newFile("identity.cube")
        LutBaker.writeCubeFile("identity", lut, out)
        val text = out.readText()
        assertTrue(text.contains("LUT_3D_SIZE 33"))
        assertTrue(text.contains("DOMAIN_MIN 0.0 0.0 0.0"))
        assertTrue(text.contains("DOMAIN_MAX 1.0 1.0 1.0"))
        val numericLines = text.lines().filter { line ->
            line.split(Regex("\\s+")).size == 3 && line.firstOrNull()?.isDigit() == true
        }
        assertEquals(35937, numericLines.size)
    }

    @Test
    fun writeCubeFileOrderingMatchesRfastestThenGthenB() {
        val lut = LutBaker.bake(FilterParams())
        val out = tmp.newFile("order.cube")
        LutBaker.writeCubeFile("order", lut, out)
        val numeric = out.readLines().filter { it.firstOrNull()?.isDigit() == true }
        assertEquals("0.000000 0.000000 0.000000", numeric[0])
        assertEquals("0.031250 0.000000 0.000000", numeric[1])
    }
}

package com.vcam.color

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterCatalogTest {
    @Test fun catalogContainsAllSixCategories() {
        val cats = FilterCatalog.all.map { it.category }.toSet()
        assertEquals(FilterCategory.entries.toSet(), cats)
    }

    @Test fun everyFilterHasUniqueId() {
        val ids = FilterCatalog.all.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test fun everyFilterPointsToALutAsset() {
        FilterCatalog.all.forEach {
            assertTrue("${it.id} lut path", it.lutAsset.startsWith("luts/") && it.lutAsset.endsWith(".cube"))
        }
    }

    @Test fun byIdResolvesKnown() = assertNotNull(FilterCatalog.byId("food_fresh"))

    @Test fun byIdReturnsNullForUnknown() = assertEquals(null, FilterCatalog.byId("nope"))
}

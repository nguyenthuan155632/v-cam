package com.vcam.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import com.vcam.data.settings.UserSettings

class FiltersTest {
    @Test
    fun legacyFiltersPointAtGeneratedLuts() {
        assertEquals(29, Filters.size)
        Filters.forEach { filter ->
            assertEquals("luts/${filter.id}.cube", filter.lutAsset)
        }
    }

    @Test
    fun legacyFiltersUseNewCategorySet() {
        val categories = Filters.map { it.category }.toSet()
        assertEquals(setOf("Food", "Portrait", "Film", "Travel", "Night", "Mono"), categories)
        assertTrue(Filters.any { it.id == "food_fresh" })
        assertTrue(Filters.any { it.id == "mono_warm" })
    }

    @Test
    fun defaultSettingsFilterExists() {
        assertTrue(Filters.any { it.id == UserSettings().defaultFilterId })
    }
}

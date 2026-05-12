package com.vcam.color

import org.junit.Assert.assertEquals
import org.junit.Test

class LegacyFilterIdMapTest {
    @Test fun mapsKnownLegacyId() = assertEquals("food_fresh", LegacyFilterIdMap.migrate("fd01"))

    @Test fun unknownIdFallsBackToFirstCatalogEntry() {
        val fallback = FilterCatalog.all.first().id
        assertEquals(fallback, LegacyFilterIdMap.migrate("bogus"))
    }

    @Test fun newIdPassesThroughUnchanged() = assertEquals("food_fresh", LegacyFilterIdMap.migrate("food_fresh"))
}

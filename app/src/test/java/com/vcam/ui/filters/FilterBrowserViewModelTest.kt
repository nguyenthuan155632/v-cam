package com.vcam.ui.filters

import com.vcam.color.FilterCatalog
import com.vcam.color.FilterCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterBrowserViewModelTest {
    @Test
    fun categoryChangeSelectsFirstFilterInCategory() {
        val vm = FilterBrowserViewModel()

        vm.setCategory(FilterCategory.Portrait)

        assertEquals(FilterCategory.Portrait, vm.state.value.activeCategory)
        assertEquals(FilterCatalog.byCategory(FilterCategory.Portrait).first().id, vm.state.value.activeFilterId)
    }

    @Test
    fun unknownActiveFilterIdFallsBackToFirstFilterInActiveCategory() {
        val vm = FilterBrowserViewModel()

        vm.setActiveFilterId("bogus_filter")

        assertEquals(FilterCatalog.byCategory(FilterCategory.Food).first().id, vm.state.value.activeFilterId)
    }
}

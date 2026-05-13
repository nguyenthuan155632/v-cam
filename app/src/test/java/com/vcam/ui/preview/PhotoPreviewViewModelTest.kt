package com.vcam.ui.preview

import com.vcam.color.FilterCatalog
import org.junit.Assert.assertEquals
import org.junit.Test

class PhotoPreviewViewModelTest {
    @Test
    fun unknownFilterIdFallsBackToFirstCatalogEntry() {
        val vm = PhotoPreviewViewModel()

        vm.setFilterId("bogus_filter")

        assertEquals(FilterCatalog.all.first().id, vm.state.value.activeFilterId)
    }
}

package com.vcam.ui.preview

import org.junit.Assert.assertEquals
import org.junit.Test

class PhotoPreviewActionsTest {
    @Test
    fun saveClickInvokesSaveCallback() {
        var saveCount = 0

        PhotoPreviewActions.save { saveCount++ }

        assertEquals(1, saveCount)
    }
}

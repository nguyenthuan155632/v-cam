package com.vcam.data.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsRepositoryTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test fun legacyDefaultFilterIdMigratesToCurrentCatalogId() = runBlocking {
        val repo = SettingsRepository(context)
        repo.setDefaultFilter("fd01")

        val settings = repo.settings.first()

        assertEquals("food_fresh", settings.defaultFilterId)
    }
}

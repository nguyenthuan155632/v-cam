package com.vcam

import android.app.Application
import com.vcam.color.LutCache
import com.vcam.color.OffscreenLutProcessor
import com.vcam.color.ThumbnailRenderer
import com.vcam.data.settings.SettingsRepository

class VCamApplication : Application() {
    lateinit var settingsRepo: SettingsRepository
        private set
    lateinit var lutCache: LutCache
        private set
    lateinit var thumbnailRenderer: ThumbnailRenderer
        private set
    val offscreenLutProcessor: OffscreenLutProcessor by lazy { OffscreenLutProcessor() }

    override fun onCreate() {
        super.onCreate()
        settingsRepo = SettingsRepository(applicationContext)
        lutCache = LutCache()
        thumbnailRenderer = ThumbnailRenderer(applicationContext, lutCache)
    }
}

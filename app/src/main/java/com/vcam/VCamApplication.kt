package com.vcam

import android.app.Application
import com.vcam.data.settings.SettingsRepository

class VCamApplication : Application() {
    lateinit var settingsRepo: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        settingsRepo = SettingsRepository(applicationContext)
    }
}

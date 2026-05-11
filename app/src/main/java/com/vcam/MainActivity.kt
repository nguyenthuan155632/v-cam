package com.vcam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.vcam.data.settings.AppTheme
import com.vcam.navigation.VCamNavGraph
import com.vcam.theme.VCamTheme
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as VCamApplication
        setContent {
            val theme by app.settingsRepo.settings
                .map { it.theme }
                .collectAsState(initial = AppTheme.Light)
            val dark = when (theme) {
                AppTheme.Dark -> true
                AppTheme.Light -> false
                AppTheme.System -> resources.configuration.isNightModeActive
            }
            VCamTheme(darkTheme = dark) {
                VCamNavGraph()
            }
        }
    }
}

private val android.content.res.Configuration.isNightModeActive: Boolean
    get() = (uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
        android.content.res.Configuration.UI_MODE_NIGHT_YES

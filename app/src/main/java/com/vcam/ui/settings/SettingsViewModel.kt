package com.vcam.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vcam.data.settings.AppTheme
import com.vcam.data.settings.AspectRatio
import com.vcam.data.settings.SettingsRepository
import com.vcam.data.settings.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repo: SettingsRepository) : ViewModel() {

    val settings: StateFlow<UserSettings> = repo.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserSettings(),
    )

    fun toggleSaveOriginal(v: Boolean) { viewModelScope.launch { repo.setSaveOriginal(v) } }
    fun toggleAutoSave(v: Boolean) { viewModelScope.launch { repo.setAutoSave(v) } }
    fun toggleGridLines(v: Boolean) { viewModelScope.launch { repo.setGridLines(v) } }
    fun toggleCameraSound(v: Boolean) { viewModelScope.launch { repo.setCameraSound(v) } }
    fun setTheme(t: AppTheme) { viewModelScope.launch { repo.setTheme(t) } }
    fun setDefaultRatio(r: AspectRatio) { viewModelScope.launch { repo.setDefaultRatio(r) } }
    fun setDefaultFilter(id: String) { viewModelScope.launch { repo.setDefaultFilter(id) } }
    fun setDefaultIntensity(v: Int) { viewModelScope.launch { repo.setDefaultIntensity(v) } }

    class Factory(private val repo: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SettingsViewModel(repo) as T
    }
}

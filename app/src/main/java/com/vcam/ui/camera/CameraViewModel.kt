package com.vcam.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vcam.data.Filters
import com.vcam.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel(private val repo: SettingsRepository) : ViewModel() {

    private val _state = MutableStateFlow(CameraUiState())
    val state: StateFlow<CameraUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repo.settings.collect { user ->
                _state.update {
                    val idx = Filters.indexOfFirst { f -> f.id == user.defaultFilterId }.coerceAtLeast(0)
                    it.copy(
                        aspectRatio = user.defaultAspectRatio,
                        activeFilterIndex = idx,
                        intensity = user.defaultIntensity,
                        saveOriginal = user.saveOriginal,
                        gridOn = user.gridLines || it.gridOn,
                    )
                }
            }
        }
    }

    fun cycleFlash() = _state.update { it.copy(flash = it.flash.next()) }
    fun cycleTimer() = _state.update { it.copy(timer = it.timer.next()) }
    fun cycleAspectRatio() = _state.update {
        val order = com.vcam.data.settings.AspectRatio.entries
        val next = order[(order.indexOf(it.aspectRatio) + 1) % order.size]
        it.copy(aspectRatio = next)
    }
    fun setAspectRatio(ratio: com.vcam.data.settings.AspectRatio) = _state.update {
        it.copy(aspectRatio = selectAspectRatio(it.aspectRatio, ratio))
    }
    fun toggleGrid() = _state.update { it.copy(gridOn = !it.gridOn) }
    fun flipCamera() = _state.update { it.copy(frontFacing = !it.frontFacing) }
    fun setActiveFilter(index: Int) = _state.update {
        it.copy(activeFilterIndex = index.coerceIn(0, Filters.size - 1))
    }
    fun setIntensity(v: Int) = _state.update { it.copy(intensity = v.coerceIn(0, 100)) }
    fun toggleIntensitySheet() = _state.update { it.copy(intensitySheetOpen = !it.intensitySheetOpen) }

    companion object {
        fun selectAspectRatio(
            current: com.vcam.data.settings.AspectRatio,
            selected: com.vcam.data.settings.AspectRatio,
        ): com.vcam.data.settings.AspectRatio = selected
    }

    class Factory(private val repo: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CameraViewModel(repo) as T
    }
}

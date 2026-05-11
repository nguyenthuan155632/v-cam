package com.vcam.ui.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vcam.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FilterBrowserState(
    val activeCategory: String = "Food",
    val activeFilterIndexInCat: Int = 0,
    val intensity: Int = 80,
)

class FilterBrowserViewModel(private val repo: SettingsRepository) : ViewModel() {
    private val _state = MutableStateFlow(FilterBrowserState())
    val state: StateFlow<FilterBrowserState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repo.settings.collect { user ->
                _state.update { it.copy(intensity = user.defaultIntensity) }
            }
        }
    }

    fun setCategory(c: String) = _state.update { it.copy(activeCategory = c, activeFilterIndexInCat = 0) }
    fun setActiveFilterInCat(i: Int) = _state.update { it.copy(activeFilterIndexInCat = i) }
    fun setIntensity(v: Int) = _state.update { it.copy(intensity = v.coerceIn(0, 100)) }

    class Factory(private val repo: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FilterBrowserViewModel(repo) as T
    }
}

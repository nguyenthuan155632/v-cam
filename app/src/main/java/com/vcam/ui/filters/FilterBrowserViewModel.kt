package com.vcam.ui.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vcam.color.FilterCatalog
import com.vcam.color.FilterCategory
import com.vcam.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FilterBrowserState(
    val activeCategory: FilterCategory = FilterCategory.Food,
    val activeFilterId: String = FilterCatalog.byCategory(FilterCategory.Food).first().id,
    val intensity: Int = 100,
)

class FilterBrowserViewModel(private val repo: SettingsRepository? = null) : ViewModel() {
    private val _state = MutableStateFlow(FilterBrowserState())
    val state: StateFlow<FilterBrowserState> = _state.asStateFlow()

    init {
        if (repo != null) {
            viewModelScope.launch {
                repo.settings.collect { user ->
                    _state.update { it.copy(intensity = user.defaultIntensity) }
                }
            }
        }
    }

    fun setCategory(category: FilterCategory) = _state.update {
        it.copy(
            activeCategory = category,
            activeFilterId = FilterCatalog.byCategory(category).first().id,
        )
    }

    fun setActiveFilterId(id: String) = _state.update {
        val resolved = FilterCatalog.byId(id)?.takeIf { filter -> filter.category == it.activeCategory }
            ?: FilterCatalog.byCategory(it.activeCategory).first()
        it.copy(activeFilterId = resolved.id)
    }

    fun setIntensity(v: Int) = _state.update { it.copy(intensity = v.coerceIn(0, 100)) }

    class Factory(private val repo: SettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FilterBrowserViewModel(repo) as T
    }
}

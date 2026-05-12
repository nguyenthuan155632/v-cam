package com.vcam.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vcam.color.LegacyFilterIdMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vcam_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val SaveOriginal = booleanPreferencesKey("save_original")
        val AutoSave = booleanPreferencesKey("auto_save")
        val GridLines = booleanPreferencesKey("grid_lines")
        val CameraSound = booleanPreferencesKey("camera_sound")
        val DefaultRatio = stringPreferencesKey("default_ratio")
        val DefaultFilter = stringPreferencesKey("default_filter")
        val DefaultIntensity = intPreferencesKey("default_intensity")
        val Theme = stringPreferencesKey("theme")
    }

    val settings: Flow<UserSettings> = context.dataStore.data.map { p ->
        UserSettings(
            saveOriginal = p[Keys.SaveOriginal] ?: true,
            autoSaveToGallery = p[Keys.AutoSave] ?: true,
            gridLines = p[Keys.GridLines] ?: false,
            cameraSound = p[Keys.CameraSound] ?: false,
            defaultAspectRatio = AspectRatio.fromLabel(p[Keys.DefaultRatio] ?: "4:3"),
            defaultFilterId = LegacyFilterIdMap.migrate(p[Keys.DefaultFilter] ?: "food_fresh"),
            defaultIntensity = p[Keys.DefaultIntensity] ?: 80,
            theme = runCatching { AppTheme.valueOf(p[Keys.Theme] ?: "Light") }.getOrDefault(AppTheme.Light),
        )
    }

    suspend fun setSaveOriginal(v: Boolean) = context.dataStore.edit { it[Keys.SaveOriginal] = v }
    suspend fun setAutoSave(v: Boolean) = context.dataStore.edit { it[Keys.AutoSave] = v }
    suspend fun setGridLines(v: Boolean) = context.dataStore.edit { it[Keys.GridLines] = v }
    suspend fun setCameraSound(v: Boolean) = context.dataStore.edit { it[Keys.CameraSound] = v }
    suspend fun setDefaultRatio(r: AspectRatio) = context.dataStore.edit { it[Keys.DefaultRatio] = r.label }
    suspend fun setDefaultFilter(id: String) = context.dataStore.edit { it[Keys.DefaultFilter] = id }
    suspend fun setDefaultIntensity(v: Int) = context.dataStore.edit { it[Keys.DefaultIntensity] = v }
    suspend fun setTheme(t: AppTheme) = context.dataStore.edit { it[Keys.Theme] = t.name }
}

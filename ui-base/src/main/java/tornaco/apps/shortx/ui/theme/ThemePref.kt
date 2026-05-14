package tornaco.apps.shortx.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

private val UI_THEME_DYNAMIC_COLOR =
    booleanPreferencesKey("ui_theme_dynamic_color")

private val UI_THEME_MODE =
    stringPreferencesKey("ui_theme_mode")

enum class UiThemeMode {
    System,
    Light,
    Dark
}

class ThemePref(val context: Context) {
    val themeDynamicColor: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[UI_THEME_DYNAMIC_COLOR] ?: true
    }

    val themeMode: Flow<UiThemeMode> = context.dataStore.data.map { preferences ->
        preferences[UI_THEME_MODE]?.let { mode ->
            runCatching { UiThemeMode.valueOf(mode) }.getOrNull()
        } ?: UiThemeMode.System
    }

    suspend fun setThemeDynamicColor(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[UI_THEME_DYNAMIC_COLOR] = value
        }
    }

    suspend fun setThemeMode(value: UiThemeMode) {
        context.dataStore.edit { settings ->
            settings[UI_THEME_MODE] = value.name
        }
    }
}

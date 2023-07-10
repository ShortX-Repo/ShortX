package tornaco.apps.shortx.ui.settings.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import tornaco.apps.shortx.core.BuildProp

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")

private val INSTALL_FAKE_SERVICE_KEY = booleanPreferencesKey("install_fake_service")
private val PRIVACY_STATEMENT_ACCEPTED_KEY =
    booleanPreferencesKey("privacy_statement_accepted${BuildProp.APP_VERSION_CODE}")
private val IGNORE_FRAMEWORK_UPDATE = booleanPreferencesKey("ignore_framework_update")

private val RECENT_SELECTED_ACTIONS = stringPreferencesKey("recent_selected_actions")

private val UI_SHOW_DETAILED_DA_ITEM_CONTENT =
    booleanPreferencesKey("ui_show_detailed_da_item_content")

class Pref(val context: Context) {
    val installFakeServiceFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[INSTALL_FAKE_SERVICE_KEY] ?: false
    }

    val privacyStatementAcceptedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PRIVACY_STATEMENT_ACCEPTED_KEY] ?: false
    }

    val ignoreFrameworkUpdate: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IGNORE_FRAMEWORK_UPDATE] ?: false
    }

    val uiShowDetailedDAItemContent: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[UI_SHOW_DETAILED_DA_ITEM_CONTENT] ?: true
    }

    val recentSelectedActions: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[RECENT_SELECTED_ACTIONS]?.split("-") ?: emptyList()
    }

    suspend fun setInstallFakeService(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[INSTALL_FAKE_SERVICE_KEY] = value
        }
    }

    suspend fun setPrivacyStatementAccepted(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[PRIVACY_STATEMENT_ACCEPTED_KEY] = value
        }
    }

    suspend fun setShowDetailedDAItemContent(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[UI_SHOW_DETAILED_DA_ITEM_CONTENT] = value
        }
    }

    suspend fun seyIgnoreFrameworkUpdate(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[IGNORE_FRAMEWORK_UPDATE] = value
        }
    }

    suspend fun addRecentSelectedAction(className: String) {
        val current = recentSelectedActions.firstOrNull()?.toMutableList()?.apply {
            remove(className)
            add(className)
        }
        context.dataStore.edit { settings ->
            settings[RECENT_SELECTED_ACTIONS] = current?.joinToString("-") ?: ""
        }
    }
}

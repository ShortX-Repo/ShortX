package tornaco.apps.shortx.ui.settings.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")

private val INSTALL_FAKE_SERVICE_KEY = booleanPreferencesKey("install_fake_service")
private val PRIVACY_STATEMENT_ACCEPTED_KEY = booleanPreferencesKey("privacy_statement_accepted")

private val UI_RULE_SHOW_DESCRIPTION = booleanPreferencesKey("ui_rule_show_description")
private val UI_RULE_COLUMN_COUNT = intPreferencesKey("ui_rule_column_count")

const val DEFAULT_RULE_COLUMN_COUNT = 2

class Pref(val context: Context) {
    val installFakeServiceFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[INSTALL_FAKE_SERVICE_KEY] ?: false
    }

    val privacyStatementAcceptedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PRIVACY_STATEMENT_ACCEPTED_KEY] ?: false
    }

    val uiRuleColumnCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[UI_RULE_COLUMN_COUNT] ?: DEFAULT_RULE_COLUMN_COUNT
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

    suspend fun setShowRuleDescription(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[UI_RULE_SHOW_DESCRIPTION] = value
        }
    }

    suspend fun setColumnCount(value: Int) {
        context.dataStore.edit { settings ->
            settings[UI_RULE_COLUMN_COUNT] = value
        }
    }

}

package tornaco.apps.shortx.ui.settings.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.common.Author
import tornaco.apps.shortx.core.proto.toJson
import tornaco.apps.shortx.core.proto.toProto
import tornaco.apps.shortx.core.rule.defaultAuthor
import tornaco.apps.shortx.core.util.Logger

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")

private val INSTALL_FAKE_SERVICE_KEY = booleanPreferencesKey("install_fake_service")
private val PRIVACY_STATEMENT_ACCEPTED_KEY =
    booleanPreferencesKey("privacy_statement_accepted${BuildProp.APP_VERSION_CODE}")
private val IGNORE_FRAMEWORK_UPDATE = booleanPreferencesKey("ignore_framework_update")

private val RECENT_SELECTED_ACTIONS = stringPreferencesKey("recent_selected_actions")

private val UI_SHOW_DETAILED_DA_ITEM_CONTENT =
    booleanPreferencesKey("ui_show_detailed_da_item_content")

private val UI_HIDE_SUBSCRIBE_INFO =
    booleanPreferencesKey("ui_hide_subscribe_info")

private val UI_ENABLE_HTTP_CHUCKER =
    booleanPreferencesKey("ui_enable_http_chucker")

private val DISCOVERY_CUSTOM_REPOS =
    stringSetPreferencesKey("discovery_custom_repos")
private val DISCOVERY_SELECTED_REPO_URL =
    stringPreferencesKey("discovery_selected_repo_url")

private val SHARE_AUTHOR_INFO = stringPreferencesKey("share_author_info")

private val USER_TIPS_HAS_READ =
    stringSetPreferencesKey("user_tips_has_read")

private val MISC_UI_TOGGLES =
    stringSetPreferencesKey("misc_ui_toggles")

private val SELECTED_ACTION_FILTER =
    stringPreferencesKey("selected_action_filter")

class Pref(val context: Context) {
    companion object {
        private val gson by lazy { Gson() }
        private val logger = Logger("Pref")
    }

    val userTipsHasRead: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[USER_TIPS_HAS_READ] ?: emptySet()
    }

    val miscUiToggles: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[MISC_UI_TOGGLES] ?: emptySet()
    }

    val hideSubscribeInfoFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[UI_HIDE_SUBSCRIBE_INFO] ?: false
    }

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

    val uiEnableHttpChucker: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[UI_ENABLE_HTTP_CHUCKER] ?: false
    }

    val recentSelectedActions: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[RECENT_SELECTED_ACTIONS]?.split("-") ?: emptyList()
    }

    val discoverySelectedRepoUrl: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DISCOVERY_SELECTED_REPO_URL] ?: ""
    }

    val selectedActionFilter: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_ACTION_FILTER] ?: ""
    }

    val shareAuthorInfo: Flow<Author> = context.dataStore.data.map { preferences ->
        preferences[SHARE_AUTHOR_INFO]?.let {
            Author.parseFrom(Author.newBuilder().toProto(it).toByteArray())
        } ?: defaultAuthor
    }

    val discoveryRepos: Flow<Set<DiscoveryRepoSetting>> =
        context.dataStore.data.map { preferences ->
            val repoJson = preferences[DISCOVERY_CUSTOM_REPOS] ?: emptySet()
            repoJson.mapNotNull {
                runCatching { gson.fromJson(it, DiscoveryRepoSetting::class.java) }.getOrElse {
                    logger.e(it, "discoveryRepo")
                    null
                }
            }.toSet()
        }

    suspend fun hideSubscribeInfo() {
        context.dataStore.edit {
            it[UI_HIDE_SUBSCRIBE_INFO] = true
        }
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

    suspend fun setIgnoreFrameworkUpdate(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[IGNORE_FRAMEWORK_UPDATE] = value
        }
    }

    suspend fun addDiscoveryRepo(setting: DiscoveryRepoSetting) {
        val current = discoveryRepos.firstOrNull() ?: emptySet()
        val updated = current.toMutableSet().apply {
            add(setting)
        }
        context.dataStore.edit { settings ->
            settings[DISCOVERY_CUSTOM_REPOS] = updated.map {
                gson.toJson(it)
            }.toSet()
        }
    }

    suspend fun removeDiscoveryRepo(setting: DiscoveryRepoSetting) {
        val current = discoveryRepos.firstOrNull() ?: emptySet()
        val updated = current.toMutableSet().apply {
            remove(setting)
        }
        context.dataStore.edit { settings ->
            settings[DISCOVERY_CUSTOM_REPOS] = updated.map {
                gson.toJson(it)
            }.toSet()
        }
    }

    suspend fun selectRepo(url: String) {
        context.dataStore.edit { settings ->
            settings[DISCOVERY_SELECTED_REPO_URL] = url
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

    suspend fun setSelectedActionFilter(filter: String) {
        context.dataStore.edit {
            it[SELECTED_ACTION_FILTER] = filter
        }
    }

    suspend fun setShareAuthor(author: Author) {
        context.dataStore.edit { settings ->
            settings[SHARE_AUTHOR_INFO] = author.toJson()
        }
    }

    suspend fun setHttpChuckerEnabled(enable: Boolean) {
        context.dataStore.edit {
            it[UI_ENABLE_HTTP_CHUCKER] = enable
        }
    }

    suspend fun userReadTip(id: String) {
        val tips = (userTipsHasRead.firstOrNull() ?: emptySet()).toMutableSet().apply {
            add(id)
        }
        context.dataStore.edit {
            it[USER_TIPS_HAS_READ] = tips
        }
    }

    suspend fun miscUiTogglesOn(id: String) {
        val tips = (miscUiToggles.firstOrNull() ?: emptySet()).toMutableSet().apply {
            add(id)
        }
        context.dataStore.edit {
            it[MISC_UI_TOGGLES] = tips
        }
    }

    suspend fun miscUiTogglesOff(id: String) {
        val tips = (miscUiToggles.firstOrNull() ?: emptySet()).toMutableSet().apply {
            remove(id)
        }
        context.dataStore.edit {
            it[MISC_UI_TOGGLES] = tips
        }
    }
}

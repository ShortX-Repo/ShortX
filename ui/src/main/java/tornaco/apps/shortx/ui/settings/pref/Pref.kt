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
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.proto.common.Author
import tornaco.apps.shortx.core.proto.toJson
import tornaco.apps.shortx.core.proto.toProto
import tornaco.apps.shortx.core.rule.defaultAuthor
import tornaco.apps.shortx.core.util.Logger

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")

private val INSTALL_FAKE_SERVICE_KEY = booleanPreferencesKey("install_fake_service")
private val PRIVACY_STATEMENT_ACCEPTED_KEY =
    booleanPreferencesKey("privacy_statement_accepted${BuildPropInternal.APP_VERSION_CODE}")
private val IGNORE_FRAMEWORK_UPDATE = booleanPreferencesKey("ignore_framework_update")
private val WORKDAY_REFRESH_STATUS = stringPreferencesKey("workday_refresh_status")
private val WORKDAY_REFRESH_ERROR = stringPreferencesKey("workday_refresh_error")
private val WORKDAY_REFRESH_AT = stringPreferencesKey("workday_refresh_at")
private val WORKDAY_REFRESH_YEARS = stringSetPreferencesKey("workday_refresh_years")
private val WORKDAY_REFRESH_SOURCE = stringPreferencesKey("workday_refresh_source")
private val LAST_VIEWED_CHANGE_LOG_VERSION_CODE =
    stringPreferencesKey("last_viewed_change_log_version_code")

private val RECENT_SELECTED_ACTIONS = stringPreferencesKey("recent_selected_actions")

private val UI_SHOW_DETAILED_DA_ITEM_CONTENT =
    booleanPreferencesKey("ui_show_detailed_da_item_content")

private val UI_SHOW_RULE_DESCRIPTION =
    booleanPreferencesKey("ui_show_rule_description")

private val UI_COLORFUL_CARD =
    booleanPreferencesKey("ui_colorful_card")

private val UI_EXPERIMENTAL =
    booleanPreferencesKey("ui_experimental")

private val UI_COLLAPSED_SETTINGS_CATEGORIES =
    stringSetPreferencesKey("ui_collapsed_settings_categories")

private val UI_ONLY_SHOW_ENABLED =
    booleanPreferencesKey("ui_only_show_enabled")

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

private val SELECTED_DA_FILTER =
    stringPreferencesKey("selected_da_filter")

private val SELECTED_RULE_FILTER =
    stringPreferencesKey("selected_rule_filter")

private val PINNED_DA_IDS =
    stringSetPreferencesKey("pinned_da_ids")

private val PINNED_RULE_IDS =
    stringSetPreferencesKey("pinned_rule_ids")

// 添加排序设置的偏好键
private val DA_SORT_BY = stringPreferencesKey("da_sort_by")
private val RULE_SORT_BY = stringPreferencesKey("rule_sort_by")

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

    val workdayRefreshStatus: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[WORKDAY_REFRESH_STATUS] ?: ""
    }

    val workdayRefreshError: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[WORKDAY_REFRESH_ERROR] ?: ""
    }

    val workdayRefreshAt: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[WORKDAY_REFRESH_AT] ?: ""
    }

    val workdayRefreshYears: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[WORKDAY_REFRESH_YEARS] ?: emptySet()
    }

    val workdayRefreshSource: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[WORKDAY_REFRESH_SOURCE] ?: ""
    }

    val lastViewedChangeLogVersionCode: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[LAST_VIEWED_CHANGE_LOG_VERSION_CODE]?.toIntOrNull() ?: 0
    }

    val uiShowDetailedDAItemContent: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[UI_SHOW_DETAILED_DA_ITEM_CONTENT] ?: true
    }

    val uiShowRuleDescription: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[UI_SHOW_RULE_DESCRIPTION] ?: true
    }

    val uiColorfulCard: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[UI_COLORFUL_CARD] ?: false
    }

    val uiExperimental: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[UI_EXPERIMENTAL] ?: false
    }

    val collapsedSettingsCategories: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[UI_COLLAPSED_SETTINGS_CATEGORIES] ?: emptySet()
    }

    val uiOnlyShowEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[UI_ONLY_SHOW_ENABLED] ?: false
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
    val selectedDAFilter: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_DA_FILTER] ?: ""
    }
    val selectedRuleFilter: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_RULE_FILTER] ?: ""
    }

    val pinnedDAIds: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[PINNED_DA_IDS] ?: emptySet()
    }

    val pinnedRuleIds: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[PINNED_RULE_IDS] ?: emptySet()
    }

    // 添加排序设置的Flow
    val daSortBy: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DA_SORT_BY] ?: "CreateTime"
    }

    val ruleSortBy: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[RULE_SORT_BY] ?: "CreateTime"
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

    suspend fun setShowRuleDescription(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[UI_SHOW_RULE_DESCRIPTION] = value
        }
    }

    suspend fun enableColorfulCard(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[UI_COLORFUL_CARD] = value
        }
    }

    suspend fun enableUiExperimental(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[UI_EXPERIMENTAL] = value
        }
    }

    suspend fun setCollapsedSettingsCategories(categories: Set<String>) {
        context.dataStore.edit { settings ->
            settings[UI_COLLAPSED_SETTINGS_CATEGORIES] = categories
        }
    }

    suspend fun onlyShowEnabled(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[UI_ONLY_SHOW_ENABLED] = value
        }
    }

    suspend fun setIgnoreFrameworkUpdate(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[IGNORE_FRAMEWORK_UPDATE] = value
        }
    }

    suspend fun setWorkdayRefreshSuccess(
        refreshedAt: String,
        years: Set<String>,
        sourceUrl: String,
        status: String,
    ) {
        context.dataStore.edit { settings ->
            settings[WORKDAY_REFRESH_AT] = refreshedAt
            settings[WORKDAY_REFRESH_YEARS] = years
            settings[WORKDAY_REFRESH_SOURCE] = sourceUrl
            settings[WORKDAY_REFRESH_STATUS] = status
            settings[WORKDAY_REFRESH_ERROR] = ""
        }
    }

    suspend fun setWorkdayRefreshFailure(
        sourceUrl: String,
        status: String,
        error: String,
    ) {
        context.dataStore.edit { settings ->
            settings[WORKDAY_REFRESH_SOURCE] = sourceUrl
            settings[WORKDAY_REFRESH_STATUS] = status
            settings[WORKDAY_REFRESH_ERROR] = error
        }
    }

    suspend fun setLastViewedChangeLogVersionCode(versionCode: Int) {
        context.dataStore.edit { settings ->
            settings[LAST_VIEWED_CHANGE_LOG_VERSION_CODE] = versionCode.toString()
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

    suspend fun setSelectedDAFilter(filter: String) {
        context.dataStore.edit {
            it[SELECTED_DA_FILTER] = filter
        }
    }

    suspend fun setSelectedRuleFilter(filter: String) {
        context.dataStore.edit {
            it[SELECTED_RULE_FILTER] = filter
        }
    }

    suspend fun pinDA(daId: String) {
        val current = pinnedDAIds.firstOrNull() ?: emptySet()
        val updated = current.toMutableSet().apply {
            add(daId)
        }
        context.dataStore.edit { settings ->
            settings[PINNED_DA_IDS] = updated
        }
    }

    suspend fun unpinDA(daId: String) {
        val current = pinnedDAIds.firstOrNull() ?: emptySet()
        val updated = current.toMutableSet().apply {
            remove(daId)
        }
        context.dataStore.edit { settings ->
            settings[PINNED_DA_IDS] = updated
        }
    }

    suspend fun isPinned(daId: String): Boolean {
        return pinnedDAIds.firstOrNull()?.contains(daId) ?: false
    }

    suspend fun pinRule(ruleId: String) {
        val current = pinnedRuleIds.firstOrNull() ?: emptySet()
        val updated = current.toMutableSet().apply {
            add(ruleId)
        }
        context.dataStore.edit { settings ->
            settings[PINNED_RULE_IDS] = updated
        }
    }

    suspend fun unpinRule(ruleId: String) {
        val current = pinnedRuleIds.firstOrNull() ?: emptySet()
        val updated = current.toMutableSet().apply {
            remove(ruleId)
        }
        context.dataStore.edit { settings ->
            settings[PINNED_RULE_IDS] = updated
        }
    }

    suspend fun isPinnedRule(ruleId: String): Boolean {
        return pinnedRuleIds.firstOrNull()?.contains(ruleId) ?: false
    }

    suspend fun setShareAuthor(author: Author) {
        context.dataStore.edit { settings ->
            settings[SHARE_AUTHOR_INFO] = author.toJson()
        }
    }

    suspend fun setHttpChuckerEnabled(enable: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[UI_ENABLE_HTTP_CHUCKER] = enable
        }
    }

    // 添加排序设置的方法
    suspend fun setDASortBy(sortBy: String) {
        context.dataStore.edit { preferences ->
            preferences[DA_SORT_BY] = sortBy
        }
    }

    suspend fun setRuleSortBy(sortBy: String) {
        context.dataStore.edit { preferences ->
            preferences[RULE_SORT_BY] = sortBy
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

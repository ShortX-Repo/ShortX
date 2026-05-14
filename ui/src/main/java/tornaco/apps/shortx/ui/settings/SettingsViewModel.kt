package tornaco.apps.shortx.ui.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.ISensorUsageListener
import tornaco.apps.shortx.core.RuleEngineNames
import tornaco.apps.shortx.core.Version
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.Author
import tornaco.apps.shortx.core.proto.common.OCREngine
import tornaco.apps.shortx.core.proto.common.SensorUsage
import tornaco.apps.shortx.ui.backup.BackupAgent
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.theme.ThemePref
import tornaco.apps.shortx.ui.theme.UiThemeMode
import java.io.File
import java.time.Instant
import java.time.Year
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class SettingsState(
    val isActivated: Boolean = false,
    val coreVersion: Version = Version("N/A", 0),
    val coreFP: String = "N/A",
    val isDebugLogEnabled: Boolean = false,
    val logDir: String = "N/A",
    val installFakeService: Boolean = false,
    val isIgnoreFrameworkUpdate: Boolean = false,
    val hasFatalError: Boolean = false,
    val isKeyEventPromptEnabled: Boolean = false,
    val isInjectedShellEnabled: Boolean = false,
    val isQSTileOptEnabled: Boolean = false,
    val isSettingsCatcherEnabled: Boolean = false,
    val isPointerLocationEnabled: Boolean = false,
    val hasAudioRecord: Boolean = false,
    val shareAuthor: Author? = null,
    val enableHttpChucker: Boolean = false,
    val hasApproximateLocationPermission: Boolean = false,
    val hasPreciseLocationPermission: Boolean = false,
    val hasBackgroundLocationPermission: Boolean = false,
    val shellEngineEnabled: Boolean = false,
    val mvelEngineEnabled: Boolean = false,
    val smsEngineEnabled: Boolean = false,
    val ruleFeatureEnabled: Boolean = false,
    val isPostNByApp: Boolean = false,
    val isForceDisableShortXByKeyPress: Boolean = false,
    val dynamicColor: Boolean = true,
    val themeMode: UiThemeMode = UiThemeMode.System,
    val customSUPath: String? = null,
    val sensorUsages: List<SensorUsage> = emptyList(),
    val ocrEngine: OCREngine = OCREngine.OCREngine_Paddle,
    val extApp: App? = null,
    val collapsedCategories: Set<String> = emptySet(),
    val isWorkdayRefreshing: Boolean = false,
    val workdayRefreshStatus: String = "",
    val workdayRefreshError: String = "",
    val workdayRefreshAt: String = "",
    val workdayRefreshYears: Set<String> = emptySet(),
    val workdayRefreshSource: String = "",
)

sealed interface BackupResult {
    data class Success(val path: String) : BackupResult
    data class Failed(val error: String) : BackupResult
}

sealed interface RestoreResult {
    data object Success : RestoreResult
    data class Failed(val error: String) : RestoreResult
}

sealed interface ExportLogResult {
    data class Success(val path: String) : ExportLogResult
    data class Failed(val error: String) : ExportLogResult
}

sealed interface WorkdayRefreshResult {
    data class Success(
        val refreshedYears: Set<String>,
        val missingYears: Set<String>,
    ) : WorkdayRefreshResult

    data class Failed(val error: String) : WorkdayRefreshResult
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SettingsViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<SettingsState>(context, { SettingsState() }) {
    companion object {
        private const val WORKDAY_SOURCE_URL = "https://raw.githubusercontent.com/lanceliao/china-holiday-calender/master/holidayAPI.json"
        private const val WORKDAY_CACHE_FILE_NAME = "holidayAPI.json"
        private const val WORKDAY_REFRESH_STATUS_SUCCESS = "success"
        private const val WORKDAY_REFRESH_STATUS_FAILED = "failed"
    }

    private val _backupPerformed = MutableSharedFlow<BackupResult>()
    val backupPerformed = _backupPerformed.asSharedFlow()

    private val _restorePerformed = MutableSharedFlow<RestoreResult>()
    val restorePerformed = _restorePerformed.asSharedFlow()

    private val _logExportPerformed = MutableSharedFlow<ExportLogResult>()
    val logExportPerformed = _logExportPerformed.asSharedFlow()

    private val _workdayRefreshPerformed = MutableSharedFlow<WorkdayRefreshResult>()
    val workdayRefreshPerformed = _workdayRefreshPerformed.asSharedFlow()

    private val pref by lazy { Pref(context) }
    private val themePref by lazy { ThemePref(context) }

    private val sensorListener = object : ISensorUsageListener.Stub() {
        override fun onUpdate() {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val usage = shortXManager.querySensorUsage()
                    updateState { copy(sensorUsages = usage) }
                }
            }
        }
    }

    init {
        shortXManager.registerSensorUsageListener(sensorListener)
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterSensorUsageListener(sensorListener)
    }

    fun loadState() {
        updateState {
            copy(
                isActivated = shortXManager.isInstalled,
                coreVersion = shortXManager.version(),
                coreFP = shortXManager.fingerprint(),
                isDebugLogEnabled = shortXManager.isLogDebugEnable(),
                logDir = shortXManager.getLogDir(),
                hasFatalError = shortXManager.hasFatalError(),
                isKeyEventPromptEnabled = shortXManager.isKeyEventPromptEnabled(),
                isInjectedShellEnabled = shortXManager.isInjectedShellEnabled(),
                isQSTileOptEnabled = shortXManager.isQSTileOptEnabled(),
                isSettingsCatcherEnabled = shortXManager.isSettingsCatcherEnabled(),
                isPointerLocationEnabled = shortXManager.isPointerLocationEnabled(),
                shellEngineEnabled = shortXManager.isRuleEngineEnabled(RuleEngineNames.ENGINE_NAME_SHELL),
                mvelEngineEnabled = shortXManager.isRuleEngineEnabled(RuleEngineNames.ENGINE_NAME_MVEL),
                smsEngineEnabled = shortXManager.isRuleEngineEnabled(RuleEngineNames.ENGINE_NAME_SMS),
                hasApproximateLocationPermission = hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION),
                hasPreciseLocationPermission = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION),
                hasBackgroundLocationPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                } else {
                    true
                },
                hasAudioRecord = shortXManager.getAllAudioRecordFiles().isNotEmpty(),
                ruleFeatureEnabled = shortXManager.isRuleFeatureEnabled(),
                isPostNByApp = shortXManager.isPostNByAppEnabled(),
                customSUPath = shortXManager.getCustomSU(),
                isForceDisableShortXByKeyPress = shortXManager.isForceDisableShortXByKeyPressEnabled(),
                sensorUsages = shortXManager.querySensorUsage(),
                ocrEngine = shortXManager.getOCREngine()
            )
        }

        val extAppPkg = AppPkg.newBuilder().setPkgName(BuildPropInternal.SHORTX_EXT_APP_PKG_NAME)
            .setUserId(0)
            .build()
        val extApp = shortXManager.pkgToApp(extAppPkg)
        updateState { copy(extApp = extApp) }

        viewModelScope.launch {
            pref.installFakeServiceFlow.collectLatest {
                logger.d("installFakeServiceFlow changed: $it")
                updateState { copy(installFakeService = it) }
            }
        }

        viewModelScope.launch {
            pref.ignoreFrameworkUpdate.collectLatest {
                logger.d("ignoreFrameworkUpdate changed: $it")
                updateState { copy(isIgnoreFrameworkUpdate = it) }
            }
        }

        viewModelScope.launch {
            pref.shareAuthorInfo.collectLatest {
                logger.d("shareAuthorInfo changed: $it")
                updateState { copy(shareAuthor = it) }
            }
        }
        viewModelScope.launch {
            pref.uiEnableHttpChucker.collectLatest {
                logger.d("uiEnableHttpChucker changed: $it")
                updateState { copy(enableHttpChucker = it) }
            }
        }
        viewModelScope.launch {
            themePref.themeDynamicColor.collectLatest {
                logger.d("themeDynamicColor changed: $it")
                updateState { copy(dynamicColor = it) }
            }
        }
        viewModelScope.launch {
            themePref.themeMode.collectLatest {
                logger.d("themeMode changed: $it")
                updateState { copy(themeMode = it) }
            }
        }
        viewModelScope.launch {
            pref.collapsedSettingsCategories.collectLatest {
                logger.d("collapsedSettingsCategories changed: $it")
                updateState { copy(collapsedCategories = it) }
            }
        }
        viewModelScope.launch {
            pref.workdayRefreshStatus.collectLatest {
                updateState { copy(workdayRefreshStatus = it) }
            }
        }
        viewModelScope.launch {
            pref.workdayRefreshError.collectLatest {
                updateState { copy(workdayRefreshError = it) }
            }
        }
        viewModelScope.launch {
            pref.workdayRefreshAt.collectLatest {
                updateState { copy(workdayRefreshAt = it) }
            }
        }
        viewModelScope.launch {
            pref.workdayRefreshYears.collectLatest {
                updateState { copy(workdayRefreshYears = it) }
            }
        }
        viewModelScope.launch {
            pref.workdayRefreshSource.collectLatest {
                updateState { copy(workdayRefreshSource = it.ifBlank { WORKDAY_SOURCE_URL }) }
            }
        }
    }

    fun refreshWorkdayCalendar() {
        if (state.value.isWorkdayRefreshing) return

        viewModelScope.launch {
            updateState { copy(isWorkdayRefreshing = true) }
            kotlin.runCatching {
                val targetYears = setOf(Year.now().value, Year.now().value + 1)
                val payload = withContext(Dispatchers.IO) { downloadWorkdayPayload() }
                val availableYears = availableWorkdayYears(payload)
                val refreshedYears = targetYears.intersect(availableYears)
                if (refreshedYears.isEmpty()) {
                    error("Source does not contain ${targetYears.joinToString()}")
                }

                withContext(Dispatchers.IO) {
                    shortXManager.updateWorkdayCalendarCache(payload)
                }

                val missingYears = targetYears - refreshedYears
                val refreshedAt = Instant.now().toString()
                pref.setWorkdayRefreshSuccess(
                    refreshedAt = refreshedAt,
                    years = refreshedYears.map(Int::toString).toSet(),
                    sourceUrl = WORKDAY_SOURCE_URL,
                    status = WORKDAY_REFRESH_STATUS_SUCCESS,
                )
                _workdayRefreshPerformed.emit(
                    WorkdayRefreshResult.Success(
                        refreshedYears = refreshedYears.map(Int::toString).toSet(),
                        missingYears = missingYears.map(Int::toString).toSet(),
                    )
                )
            }.onFailure {
                logger.e(it, "refreshWorkdayCalendar")
                val error = it.message ?: "UNKNOWN ERR"
                pref.setWorkdayRefreshFailure(
                    sourceUrl = WORKDAY_SOURCE_URL,
                    status = WORKDAY_REFRESH_STATUS_FAILED,
                    error = error,
                )
                _workdayRefreshPerformed.emit(WorkdayRefreshResult.Failed(error))
            }
            updateState { copy(isWorkdayRefreshing = false) }
        }
    }

    fun setDynamicColor(dynamic: Boolean) {
        viewModelScope.launch {
            themePref.setThemeDynamicColor(dynamic)
        }
    }

    fun setThemeMode(mode: UiThemeMode) {
        viewModelScope.launch {
            themePref.setThemeMode(mode)
        }
    }

    fun setColorfulCard(color: Boolean) {
        viewModelScope.launch {
            pref.enableColorfulCard(color)
        }
    }

    fun toggleSettingsCategoryCollapsed(title: String) {
        viewModelScope.launch {
            val updated = state.value.collapsedCategories.toMutableSet().apply {
                if (!add(title)) remove(title)
            }
            pref.setCollapsedSettingsCategories(updated)
        }
    }

    fun collapseAllSettingsCategories(categories: Set<String>) {
        viewModelScope.launch {
            pref.setCollapsedSettingsCategories(categories)
        }
    }

    fun expandAllSettingsCategories() {
        viewModelScope.launch {
            pref.setCollapsedSettingsCategories(emptySet())
        }
    }

    fun setUiExperimental(exp: Boolean) {
        viewModelScope.launch {
            pref.enableUiExperimental(exp)
        }
    }

    fun setShareAuthor(author: Author) {
        viewModelScope.launch {
            pref.setShareAuthor(author)
        }
    }

    fun setLogDebugEnable(enable: Boolean) {
        logger.d("setLogDebugEnable: $enable")
        viewModelScope.launch {
            shortXManager.setLogDebugEnable(enable)
            updateState {
                copy(isDebugLogEnabled = shortXManager.isLogDebugEnable())
            }
        }
    }

    fun setPostNByAppEnabled(enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setPostNByAppEnabled(enable)
            updateState {
                copy(isPostNByApp = shortXManager.isPostNByAppEnabled())
            }
        }
    }

    fun setForceDisableShortXByKeyPressEnabled(enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setForceDisableShortXByKeyPressEnabled(enable)
            updateState {
                copy(isForceDisableShortXByKeyPress = shortXManager.isForceDisableShortXByKeyPressEnabled())
            }
        }
    }

    fun setSystemSettingsCatcherEnabled(enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setSettingsCatcherEnabled(enable)
            updateState {
                copy(isSettingsCatcherEnabled = shortXManager.isSettingsCatcherEnabled())
            }
        }
    }

    fun setShellEngineEnable(enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setRuleEngineEnabled(RuleEngineNames.ENGINE_NAME_SHELL, enable)
            updateState {
                copy(shellEngineEnabled = shortXManager.isRuleEngineEnabled(RuleEngineNames.ENGINE_NAME_SHELL))
            }

            if (enable) {
                withContext(Dispatchers.Default) {
                    shortXManager.executeShellCommand("whoami") {
                        logger.d("Test shell res: $it")
                    }
                }
            }
        }
    }

    fun setMVELEngineEnable(enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setRuleEngineEnabled(RuleEngineNames.ENGINE_NAME_MVEL, enable)
            updateState {
                copy(mvelEngineEnabled = shortXManager.isRuleEngineEnabled(RuleEngineNames.ENGINE_NAME_MVEL))
            }
        }
    }

    fun setSMSEngineEnable(enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setRuleEngineEnabled(RuleEngineNames.ENGINE_NAME_SMS, enable)
            updateState {
                copy(smsEngineEnabled = shortXManager.isRuleEngineEnabled(RuleEngineNames.ENGINE_NAME_SMS))
            }
        }
    }

    fun setEnableHttpChucker(enable: Boolean) {
        viewModelScope.launch {
            pref.setHttpChuckerEnabled(enable)
        }
    }

    fun refreshLocationPermissionState() {
        updateState {
            copy(
                hasApproximateLocationPermission = hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION),
                hasPreciseLocationPermission = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION),
                hasBackgroundLocationPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                } else {
                    true
                },
            )
        }
    }

    fun backup(fileName: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                BackupAgent(context).backup(fileName).apply {
                    _backupPerformed.emit(BackupResult.Success(Environment.DIRECTORY_DOWNLOADS + File.separator + name))
                }
            }.onFailure {
                logger.e(it, "Backup error")
                _backupPerformed.emit(BackupResult.Failed(it.message ?: "UNKNOWN ERR"))
            }
        }
    }

    fun restoreFrom(fileUri: Uri, token: String?) {
        viewModelScope.launch {
            kotlin.runCatching {
                BackupAgent(context).restore(fileUri, token)
                RestoreEventRegistry.dataHasBeenRestore()
                _restorePerformed.emit(RestoreResult.Success)
            }.onFailure {
                logger.e(it, "Restore error")
                _restorePerformed.emit(RestoreResult.Failed(it.message ?: "UNKNOWN ERR"))
            }
        }
    }

    fun importPrebuilts(token: String?) {
        viewModelScope.launch {
            kotlin.runCatching {
                BackupAgent(context).restore("dev/dev-all.zip", token)
                RestoreEventRegistry.dataHasBeenRestore()
                _restorePerformed.emit(RestoreResult.Success)
            }.onFailure {
                logger.e(it, "Restore error")
                _restorePerformed.emit(RestoreResult.Failed(it.message ?: "UNKNOWN ERR"))
            }
        }
    }

    fun resetData() {
        logger.w("resetData")
        viewModelScope.launch {
            kotlin.runCatching {
                BackupAgent(context).resetAllData()
            }
            ResetEventRegistry.dataHasBeenReset()
        }
    }

    fun setInstallFakeService(value: Boolean) {
        viewModelScope.launch {
            pref.setInstallFakeService(value)
        }
    }

    fun setIgnoreFrameworkUpdate(value: Boolean) {
        viewModelScope.launch {
            pref.setIgnoreFrameworkUpdate(value)
        }
    }

    private fun downloadWorkdayPayload(): String {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder().url(WORKDAY_SOURCE_URL).get().build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                error("HTTP ${response.code}")
            }
            return response.body?.string()?.takeIf { it.isNotBlank() }
                ?: error("Empty response body")
        }
    }

    private fun availableWorkdayYears(payload: String): Set<Int> {
        val root = Json.parseToJsonElement(payload).jsonObject
        val years = root["Years"]?.jsonObject ?: error("Missing Years")
        return years.keys.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun exportLogs() {
        viewModelScope.launch {
            kotlin.runCatching {
                val res = exportLogs(context, shortXManager)
                _logExportPerformed.emit(ExportLogResult.Success(Environment.DIRECTORY_DOWNLOADS + File.separator + res.name))
            }.onFailure {
                logger.e(it, "exportLogs error")
                _logExportPerformed.emit(ExportLogResult.Failed(it.message ?: "UNKNOWN ERR"))
            }
        }
    }

    fun toggleKeyEventPromptEnabled() {
        viewModelScope.launch {
            shortXManager.setKeyEventPromptEnabled(!state.value.isKeyEventPromptEnabled)
            updateState { copy(isKeyEventPromptEnabled = shortXManager.isKeyEventPromptEnabled()) }
        }
    }

    fun toggleInjectedShell() {
        viewModelScope.launch {
            shortXManager.setInjectedShellEnabled(!state.value.isInjectedShellEnabled)
            updateState { copy(isInjectedShellEnabled = shortXManager.isInjectedShellEnabled()) }
        }
    }

    fun toggleQSTileOpt() {
        viewModelScope.launch {
            shortXManager.setQSTileOptEnabled(!state.value.isQSTileOptEnabled)
            updateState { copy(isQSTileOptEnabled = shortXManager.isQSTileOptEnabled()) }
        }
    }

    fun togglePointerLocation() {
        viewModelScope.launch {
            shortXManager.setPointerLocationEnabled(!state.value.isPointerLocationEnabled)
            updateState { copy(isPointerLocationEnabled = shortXManager.isPointerLocationEnabled()) }
        }
    }

    fun toggleRuleFeatureEnable(enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setRuleFeatureEnabled(enable)
            updateState { copy(ruleFeatureEnabled = shortXManager.isRuleFeatureEnabled()) }
        }
    }

    fun setCustomSUPath(path: String?) {
        viewModelScope.launch {
            shortXManager.setCustomSU(path)
            updateState { copy(customSUPath = shortXManager.getCustomSU()) }
        }
    }

    fun setOCREngine(ocrEngine: OCREngine) {
        viewModelScope.launch {
            shortXManager.setOCREngine(ocrEngine)
            updateState { copy(ocrEngine = shortXManager.getOCREngine()) }
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}

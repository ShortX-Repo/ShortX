package tornaco.apps.shortx.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.ISensorUsageListener
import tornaco.apps.shortx.core.RuleEngineNames
import tornaco.apps.shortx.core.Version
import tornaco.apps.shortx.core.proto.common.Author
import tornaco.apps.shortx.core.proto.common.SensorUsage
import tornaco.apps.shortx.ui.backup.BackupAgent
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.theme.ThemePref
import java.io.File
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
    val shellEngineEnabled: Boolean = false,
    val mvelEngineEnabled: Boolean = false,
    val smsEngineEnabled: Boolean = false,
    val ruleFeatureEnabled: Boolean = false,
    val isPostNByApp: Boolean = false,
    val isForceDisableShortXByKeyPress: Boolean = false,
    val dynamicColor: Boolean = true,
    val sensorUsages: List<SensorUsage> = emptyList()
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

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SettingsViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<SettingsState>(context, { SettingsState() }) {

    private val _backupPerformed = MutableSharedFlow<BackupResult>()
    val backupPerformed = _backupPerformed.asSharedFlow()

    private val _restorePerformed = MutableSharedFlow<RestoreResult>()
    val restorePerformed = _restorePerformed.asSharedFlow()

    private val _logExportPerformed = MutableSharedFlow<ExportLogResult>()
    val logExportPerformed = _logExportPerformed.asSharedFlow()

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
                hasAudioRecord = shortXManager.getAllAudioRecordFiles().isNotEmpty(),
                ruleFeatureEnabled = shortXManager.isRuleFeatureEnabled(),
                isPostNByApp = shortXManager.isPostNByAppEnabled(),
                isForceDisableShortXByKeyPress = shortXManager.isForceDisableShortXByKeyPressEnabled(),
                sensorUsages = shortXManager.querySensorUsage()
            )
        }

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
    }

    fun setDynamicColor(dynamic: Boolean) {
        viewModelScope.launch {
            themePref.setThemeDynamicColor(dynamic)
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
}
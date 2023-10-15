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
import tornaco.apps.shortx.core.RuleEngineNames
import tornaco.apps.shortx.core.Version
import tornaco.apps.shortx.core.proto.common.Author
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.ui.backup.BackupAgent
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.settings.pref.Pref
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
    val gestureAreaSize: Int = 0,
    val swipeDistanceThreshold: Int = 0,
    val isInjectedShellEnabled: Boolean = false,
    val isQSTileOptEnabled: Boolean = false,
    val isSettingsCatcherEnabled: Boolean = false,
    val isPointerLocationEnabled: Boolean = false,
    val hasAudioRecord: Boolean = false,
    val shareAuthor: Author? = null,
    val enableHttpChucker: Boolean = false,
    val shellEngineEnabled: Boolean = false,
    val mvelEngineEnabled: Boolean = false,
    val ruleFeatureEnabled: Boolean = false,
)

sealed interface BackupResult {
    data class Success(val path: String) : BackupResult
    data class Failed(val error: String) : BackupResult
}

sealed interface RestoreResult {
    object Success : RestoreResult
    data class Failed(val error: String) : RestoreResult
}

sealed interface ExportLogResult {
    data class Success(val path: String) : ExportLogResult
    data class Failed(val error: String) : ExportLogResult
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SettingsViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<SettingsState>(context, { SettingsState() }) {

    private val _backupPerformed = MutableSharedFlow<BackupResult>()
    val backupPerformed = _backupPerformed.asSharedFlow()

    private val _restorePerformed = MutableSharedFlow<RestoreResult>()
    val restorePerformed = _restorePerformed.asSharedFlow()

    private val _logExportPerformed = MutableSharedFlow<ExportLogResult>()
    val logExportPerformed = _logExportPerformed.asSharedFlow()

    private val pref by lazy { Pref(context) }

    fun loadState() {
        updateState {
            copy(
                isActivated = shortXManager.isInstalled,
                coreVersion = shortXManager.version(),
                coreFP = shortXManager.fingerprint(),
                isDebugLogEnabled = shortXManager.isLogDebugEnable(),
                logDir = shortXManager.getLogDir(),
                hasFatalError = shortXManager.hasFatalError(),
                gestureAreaSize = shortXManager.getGestureAreaSize(),
                swipeDistanceThreshold = shortXManager.getSwipeDistanceThresholdDp(),
                isKeyEventPromptEnabled = shortXManager.isKeyEventPromptEnabled(),
                isInjectedShellEnabled = shortXManager.isInjectedShellEnabled(),
                isQSTileOptEnabled = shortXManager.isQSTileOptEnabled(),
                isSettingsCatcherEnabled = shortXManager.isSettingsCatcherEnabled(),
                isPointerLocationEnabled = shortXManager.isPointerLocationEnabled(),
                shellEngineEnabled = shortXManager.isRuleEngineEnabled(RuleEngineNames.ENGINE_NAME_SHELL),
                mvelEngineEnabled = shortXManager.isRuleEngineEnabled(RuleEngineNames.ENGINE_NAME_MVEL),
                hasAudioRecord = shortXManager.getAllAudioRecordFiles().isNotEmpty(),
                ruleFeatureEnabled = shortXManager.isRuleFeatureEnabled()
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
                    shortXManager.executeShellCommand("whoami")
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

    fun setGestureAreaSize(size: Int) {
        viewModelScope.launch {
            shortXManager.setGestureAreaSize(size)
            updateState { copy(gestureAreaSize = shortXManager.getGestureAreaSize()) }

            shortXManager.showEdgeHint(Edge.RightCenter)
        }
    }

    fun setSwipeDistanceThresold(dp: Int) {
        viewModelScope.launch {
            shortXManager.setSwipeDistanceThresholdDp(dp)
            updateState { copy(swipeDistanceThreshold = shortXManager.getSwipeDistanceThresholdDp()) }
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

    fun toggleRuleFeatureEnable() {
        viewModelScope.launch {
            shortXManager.setRuleFeatureEnabled(!state.value.ruleFeatureEnabled)
            updateState { copy(ruleFeatureEnabled = shortXManager.isRuleFeatureEnabled()) }
        }
    }
}
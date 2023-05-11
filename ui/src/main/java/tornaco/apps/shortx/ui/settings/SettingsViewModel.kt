package tornaco.apps.shortx.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.Version
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
    val installFakeService: Boolean = false,
    val isIgnoreFrameworkUpdate: Boolean = false,
)

sealed interface BackupResult {
    data class Success(val path: String) : BackupResult
    data class Failed(val error: String) : BackupResult
}

sealed interface RestoreResult {
    object Success : RestoreResult
    data class Failed(val error: String) : RestoreResult
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SettingsViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<SettingsState>(context, SettingsState()) {

    private val _backupPerformed = MutableSharedFlow<BackupResult>()
    val backupPerformed = _backupPerformed.asSharedFlow()

    private val _restorePerformed = MutableSharedFlow<RestoreResult>()
    val restorePerformed = _restorePerformed.asSharedFlow()

    private val pref by lazy { Pref(context) }

    fun loadState() {
        updateState {
            copy(
                isActivated = shortXManager.isInstalled,
                coreVersion = shortXManager.version(),
                coreFP = shortXManager.fingerprint(),
                isDebugLogEnabled = shortXManager.isLogDebugEnable()
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

    fun restoreFrom(fileUri: Uri) {
        viewModelScope.launch {
            kotlin.runCatching {
                BackupAgent(context).restore(fileUri)
                _restorePerformed.emit(RestoreResult.Success)
            }.onFailure {
                logger.e(it, "Restore error")
                _restorePerformed.emit(RestoreResult.Failed(it.message ?: "UNKNOWN ERR"))
            }
        }
    }

    fun importPrebuilts() {
        viewModelScope.launch {
            kotlin.runCatching {
                BackupAgent(context).restore("prebuilts/dev-all.zip")
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
        }
    }

    fun setInstallFakeService(value: Boolean) {
        viewModelScope.launch {
            pref.setInstallFakeService(value)
        }
    }

    fun setIgnoreFrameworkUpdate(value: Boolean) {
        viewModelScope.launch {
            pref.seyIgnoreFrameworkUpdate(value)
        }
    }
}
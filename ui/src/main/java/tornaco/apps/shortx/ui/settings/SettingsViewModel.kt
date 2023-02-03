package tornaco.apps.shortx.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.Version
import tornaco.apps.shortx.ui.base.ContextViewModel
import javax.inject.Inject


data class SettingsState(
    val isActivated: Boolean = false,
    val coreVersion: Version = Version("N/A", 0),
    val coreFP: String = "N/A",
    val isDebugLogEnabled: Boolean = false,
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SettingsViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ContextViewModel<SettingsState>(context) {

    override fun initState(): SettingsState {
        return SettingsState()
    }

    fun loadState() {
        updateState {
            copy(
                isActivated = shortXManager.isInstalled(),
                coreVersion = shortXManager.version(),
                coreFP = shortXManager.fingerprint(),
                isDebugLogEnabled = shortXManager.isLogDebugEnable()
            )
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
}
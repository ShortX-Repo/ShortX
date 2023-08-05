package tornaco.apps.shortx.ui.record

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.Logger
import javax.inject.Inject

data class SettingsAccessRecordState(
    val data: List<SettingsAccessRecordUM> = emptyList(),
    val isEnabled: Boolean = false,
    val isLoading: Boolean = false,
)

data class SettingsAccessRecordUM(
    val isRead: Boolean,
    val url: String,
    val value: String,
    val timeMillis: Long,
    val timeString: String,
)

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class SettingsAccessRecordViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ViewModel(),
    ContainerHost<SettingsAccessRecordState, String> {
    private val logger = Logger("SettingsAccessRecordViewModel")

    override val container: Container<SettingsAccessRecordState, String> = container(
        initialState = SettingsAccessRecordState()
    )

    fun loadRecords() = intent {
        reduce {
            state.copy(
                isLoading = true,
                isEnabled = shortXManager.isSettingsCatcherEnabled()
            )
        }
        withContext(Dispatchers.IO) {
            val records =
                (shortXManager.getWriteSettingsRecord().map {
                    SettingsAccessRecordUM(
                        isRead = false,
                        url = it.url,
                        value = it.value,
                        timeMillis = it.timeMillis,
                        timeString = DateUtils.formatLongForMessageTime(it.timeMillis)
                    )
                }
                        + shortXManager.getReadSettingsRecord()
                    .map {
                        SettingsAccessRecordUM(
                            isRead = true,
                            url = it.url,
                            value = it.value,
                            timeMillis = it.timeMillis,
                            timeString = DateUtils.formatLongForMessageTime(it.timeMillis)
                        )
                    })
                    .sortedByDescending { it.timeMillis }

            reduce {
                state.copy(data = records, isLoading = false)
            }
        }
    }

    fun setRecordEnabled(enable: Boolean) = intent {
        shortXManager.setSettingsCatcherEnabled(enable)
        reduce {
            state.copy(isEnabled = shortXManager.isSettingsCatcherEnabled())
        }
    }

    fun clearRecords() = intent {
        shortXManager.clearReadSettingsRecord()
        shortXManager.clearWriteSettingsRecord()
        loadRecords()
    }
}
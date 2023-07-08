package tornaco.apps.shortx.ui.record

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.rule.ActionEvaluateRecord
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.base.ContextViewModel
import javax.inject.Inject

data class ActionEvaRecordState(
    val isLoading: Boolean = false,
    val isEnabled: Boolean = false,
    val showSuccess: Boolean = true,
    val showFail: Boolean = true,
    val records: List<ActionEvaluateRecordUM> = emptyList()
)

data class ActionEvaluateRecordUM(
    val actionIns: Action,
    val formattedTimeStr: String,
    val record: ActionEvaluateRecord
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ActionEvaRecordViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<ActionEvaRecordState>(context, { ActionEvaRecordState() }) {

    fun loadState() {
        viewModelScope.launch {
            updateState {
                copy(
                    isLoading = true,
                    isEnabled = shortXManager.isActionEvaluateRecorderEnabled()
                )
            }
            val records = withContext(Dispatchers.IO) {
                shortXManager.getActionEvaluateRecords()
                    .reversed()
                    .filter {
                        (state.value.showSuccess && it.isSuccess) || (state.value.showFail && !it.isSuccess)
                    }
                    .mapNotNull { record ->
                        val actionIns = kotlin.runCatching {
                            record.action.toAction(shortXManager)
                        }.getOrElse {
                            logger.e(it, "Map Action record error")
                            null
                        }
                        actionIns?.let {
                            ActionEvaluateRecordUM(
                                actionIns = actionIns,
                                formattedTimeStr = DateUtils.formatLongForMessageTime(record.timeStamp),
                                record = record
                            )
                        }
                    }
            }
            updateState { copy(isLoading = false, records = records) }
        }
    }

    fun toggleRecorder() {
        shortXManager.setActionEvaluateRecorderEnabled(!state.value.isEnabled)
        updateState {
            copy(
                isEnabled = shortXManager.isActionEvaluateRecorderEnabled()
            )
        }
    }

    fun toggleShowSuccess() {
        updateState { copy(showSuccess = !showSuccess) }
        loadState()
    }

    fun toggleShowFail() {
        updateState { copy(showFail = !showFail) }
        loadState()
    }

    fun clear() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            shortXManager.clearActionEvaluateRecords()
            delay(666)
            loadState()
        }
    }
}
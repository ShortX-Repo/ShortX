package tornaco.apps.shortx.ui.record

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.protobuf.Any
import com.google.protobuf.Message
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
import util.Reflections
import javax.inject.Inject

data class ActionEvaRecordState(
    val isLoading: Boolean = false,
    val isEnabled: Boolean = false,
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
            delay(1000)
            val records = withContext(Dispatchers.IO) {
                shortXManager.getActionEvaluateRecords()
                    .reversed()
                    .mapNotNull { record ->
                        val actionIns = kotlin.runCatching {
                            val protoClass = Class.forName(record.actionClassName)
                            val defIns =
                                Reflections.callStaticMethod(protoClass, "getDefaultInstance")
                            val packedIns = Any.pack(defIns as Message?)
                            packedIns.toAction(shortXManager)
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

    fun clear() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            shortXManager.clearActionEvaluateRecords()
            delay(666)
            loadState()
        }
    }
}
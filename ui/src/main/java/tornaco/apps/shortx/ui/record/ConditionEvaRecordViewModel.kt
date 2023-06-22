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
import tornaco.apps.shortx.core.proto.rule.ConditionEvaluateRecord
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.base.ContextViewModel
import util.Reflections
import javax.inject.Inject

data class ConditionEvaRecordState(
    val isLoading: Boolean = false,
    val isEnabled: Boolean = false,
    val records: List<ConditionEvaluateRecordUM> = emptyList()
)

data class ConditionEvaluateRecordUM(
    val actionIns: Condition,
    val formattedTimeStr: String,
    val record: ConditionEvaluateRecord
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ConditionEvaRecordViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<ConditionEvaRecordState>(context, { ConditionEvaRecordState() }) {

    fun loadState() {
        viewModelScope.launch {
            updateState {
                copy(
                    isLoading = true,
                    isEnabled = shortXManager.isConditionEvaluateRecorderEnabled()
                )
            }
            delay(1000)
            val records = withContext(Dispatchers.IO) {
                shortXManager.getConditionEvaluateRecords()
                    .reversed()
                    .mapNotNull { record ->
                        val actionIns = kotlin.runCatching {
                            logger.d("Map class: ${record.conditionClassName}")
                            val protoClass = Class.forName(record.conditionClassName)
                            val defIns =
                                Reflections.callStaticMethod(protoClass, "getDefaultInstance")
                            val packedIns = Any.pack(defIns as Message?)
                            packedIns.toCondition(shortXManager)
                        }.getOrElse {
                            logger.e(it, "Map Condition record error")
                            null
                        }
                        actionIns?.let {
                            ConditionEvaluateRecordUM(
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
        shortXManager.setConditionEvaluateRecorderEnabled(!state.value.isEnabled)
        updateState {
            copy(
                isEnabled = shortXManager.isConditionEvaluateRecorderEnabled()
            )
        }
    }

    fun clear() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            shortXManager.clearConditionEvaluateRecords()
            delay(666)
            loadState()
        }
    }
}
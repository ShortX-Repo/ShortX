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
import tornaco.apps.shortx.core.proto.rule.FactPublishRecord
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
import tornaco.apps.shortx.ui.base.ContextViewModel
import javax.inject.Inject

data class FactPublishRecordState(
    val isLoading: Boolean = false,
    val isEnabled: Boolean = false,
    val records: List<FactPublishluateRecordUM> = emptyList()
)

data class FactPublishluateRecordUM(
    val factIns: Fact,
    val formattedTimeStr: String,
    val record: FactPublishRecord
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class FactPublishRecordViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<FactPublishRecordState>(context, { FactPublishRecordState() }) {

    fun loadState() {
        viewModelScope.launch {
            updateState {
                copy(
                    isLoading = true,
                    isEnabled = shortXManager.isFactPublishRecordEnabled()
                )
            }
            delay(1000)
            val records = withContext(Dispatchers.IO) {
                shortXManager.getFactPublishRecords()
                    .reversed()
                    .mapNotNull { record ->
                        val factIns = kotlin.runCatching {
                            record.fact.toFact(shortXManager)
                        }.getOrElse {
                            logger.e(it, "Map Fact record error")
                            null
                        }
                        factIns?.let {
                            FactPublishluateRecordUM(
                                factIns = factIns,
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
        shortXManager.setFactPublishRecordEnabled(!state.value.isEnabled)
        updateState {
            copy(
                isEnabled = shortXManager.isFactPublishRecordEnabled()
            )
        }
    }

    fun clear() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            shortXManager.clearFactPublishRecords()
            delay(666)
            loadState()
        }
    }
}
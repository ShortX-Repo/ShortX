package tornaco.apps.shortx.ui.gesture

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.StateViewModel

data class GestureRecordListState(
    val gestures: List<GestureRecord> = emptyList(),
    val isLoading: Boolean = false,
    val isEdgeGestureDisabled: Boolean = false
)

class GestureRecordListViewModel :
    StateViewModel<GestureRecordListState, Unit>({ GestureRecordListState() }) {
    fun loadState() {
        viewModelScope.launch(Dispatchers.IO) {
            updateState { copy(isLoading = true) }

            val gestures = shortXManager.getAllGestureRecords(false)
            val isEdgeGestureDisabled =
                shortXManager.hasHookFlags(ShortXManager.Companion.HookFlags.FLAG_DISABLE_GESTURE_HOOK)
            updateState {
                copy(
                    gestures = gestures,
                    isEdgeGestureDisabled = isEdgeGestureDisabled,
                    isLoading = false
                )
            }
        }
    }

    fun delete(id: String) {
        shortXManager.deleteGestureRecordById(id)
        loadState()
    }

    fun rename(gestureRecord: GestureRecord, name: String) {
        val richRecord = shortXManager.getGestureRecordById(gestureRecord.id)
        richRecord?.let {
            shortXManager.addGestureRecord(
                GestureRecord.newBuilder(richRecord)
                    .setLabel(name)
                    .build()
            )
        }
        loadState()
    }
}
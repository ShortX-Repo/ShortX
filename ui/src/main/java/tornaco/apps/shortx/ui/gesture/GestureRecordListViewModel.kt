package tornaco.apps.shortx.ui.gesture

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.base.afterNavDelay

data class GestureRecordListState(
    val gestures: List<GestureRecord> = emptyList(),
    val isLoading: Boolean = false
)

class GestureRecordListViewModel :
    StateViewModel<GestureRecordListState>({ GestureRecordListState() }) {
    fun loadState() {
        viewModelScope.launch(Dispatchers.IO) {
            updateState { copy(isLoading = true) }
            afterNavDelay()

            val gestures = shortXManager.getAllGestureRecords(false)
            updateState {
                copy(gestures = gestures, isLoading = false)
            }
        }
    }

    fun delete(id: String) {
        shortXManager.deleteGestureRecordById(id)
        loadState()
    }
}
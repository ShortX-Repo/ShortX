package tornaco.apps.shortx.ui.settings

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ResetEventRegistry {
    private val _dataResetComplete = MutableSharedFlow<Long>()
    val dataResetComplete = _dataResetComplete.asSharedFlow()

    suspend fun dataHasBeenReset() {
        _dataResetComplete.emit(System.currentTimeMillis())
    }
}
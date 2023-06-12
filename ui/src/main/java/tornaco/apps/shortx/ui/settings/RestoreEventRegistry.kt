package tornaco.apps.shortx.ui.settings

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object RestoreEventRegistry {
    private val _dataRestoreComplete = MutableSharedFlow<Long>()
    val dataRestoreComplete = _dataRestoreComplete.asSharedFlow()

    suspend fun dataHasBeenRestore() {
        _dataRestoreComplete.emit(System.currentTimeMillis())
    }
}
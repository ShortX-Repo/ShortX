package tornaco.apps.shortx.ui.main.toggle

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import tornaco.apps.shortx.core.rule.ToggleObserver
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.main.ToggleSortBy
import tornaco.apps.shortx.ui.main.model.ToggleUM
import tornaco.apps.shortx.ui.settings.ResetEventRegistry
import tornaco.apps.shortx.ui.settings.RestoreEventRegistry
import java.util.*

data class ToggleListState(
    val rules: List<ToggleUM> = emptyList(),
    val ruleSortBy: ToggleSortBy = ToggleSortBy.CreateTime,
    val isLoading: Boolean = true,
)

@SuppressLint("StaticFieldLeak")
abstract class BaseToggleListViewModel(context: Context) :
    ContextViewModel<ToggleListState>(context, { ToggleListState() }) {
    private var loadToggleJob: Job? = null
    private val toggleObs = object : ToggleObserver() {
        override fun onToggleChanged(id: String) {
            super.onToggleChanged(id)
            loadToggleList()
        }
    }

    init {
        shortXManager.registerToggleObs(toggleObs)

        viewModelScope.launch {
            ResetEventRegistry.dataResetComplete.collect {
                logger.d("reload data since data has been reset")
                loadToggleList()
            }
        }

        viewModelScope.launch {
            RestoreEventRegistry.dataRestoreComplete.collect {
                logger.d("reload data since data restored")
                loadToggleList()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterToggleObs(toggleObs)
    }

    abstract fun loadToggleList(delay: Boolean = false)

    private fun delayLoadToggle(delay: Long = 200L) {
        loadToggleJob?.cancel()
        loadToggleJob = viewModelScope.launch {
            delay(delay)
            loadToggleList()
        }
    }

    fun deleteToggle(id: String) {
        viewModelScope.launch {
            shortXManager.deleteToggle(id)
        }
    }
}
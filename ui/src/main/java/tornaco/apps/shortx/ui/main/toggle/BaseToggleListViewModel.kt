package tornaco.apps.shortx.ui.main.toggle

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.toJson
import tornaco.apps.shortx.core.rule.ToggleObserver
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.main.ToggleSortBy
import tornaco.apps.shortx.ui.main.model.ToggleUM
import tornaco.apps.shortx.ui.settings.ResetEventRegistry
import tornaco.apps.shortx.ui.settings.RestoreEventRegistry
import tornaco.apps.shortx.ui.share.SharedData
import tornaco.apps.shortx.ui.share.addShareContentHeader

data class ToggleListState(
    val toggles: List<ToggleUM> = emptyList(),
    val ruleSortBy: ToggleSortBy = ToggleSortBy.CreateTime,
    val isLoading: Boolean = true,
)

@SuppressLint("StaticFieldLeak")
abstract class BaseToggleListViewModel(context: Context) :
    ShortXContextViewModel<ToggleListState>(context, { ToggleListState() }) {
    private val toggleObs = object : ToggleObserver() {
        override fun onToggleChanged(id: String) {
            super.onToggleChanged(id)
            postLoadToggleList()
        }
    }

    init {
        shortXManager.registerToggleObs(toggleObs)

        viewModelScope.launch {
            ResetEventRegistry.dataResetComplete.collect {
                logger.d("reload data since data has been reset")
                postLoadToggleList()
            }
        }

        viewModelScope.launch {
            RestoreEventRegistry.dataRestoreComplete.collect {
                logger.d("reload data since data restored")
                postLoadToggleList()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterToggleObs(toggleObs)
    }


    fun postLoadToggleList(delay: Boolean = false) {
        loadToggleList()
    }

    abstract fun loadToggleList(delay: Boolean = false)

    fun deleteToggle(id: String) {
        viewModelScope.launch {
            shortXManager.deleteToggle(id)
        }
    }

    fun setToggleEnabled(id: String, enable: Boolean) {
        viewModelScope.launch {
            shortXManager.setToggleEnabled(id, enable)
        }
    }

    fun toggleToShareString(toggle: ToggleUM): String? =
        shortXManager.getToggleById(toggle.id)?.toJson()
            ?.addShareContentHeader(mapOf("type" to SharedData.TYPE_TOGGLE))

    fun toggleSetToShareString(toggle: ToggleUM): String? = "TODO"
}
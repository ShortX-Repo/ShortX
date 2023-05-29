package tornaco.apps.shortx.ui.overlay

import androidx.lifecycle.viewModelScope
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.ui.base.StateViewModel

data class OverlayState(
    val values: List<String> = emptyList(),
    val formattedValues: List<String> = emptyList()
)

class OverlayViewModel : StateViewModel<OverlayState>({ OverlayState() }) {
    private val logger = Logger("OverlayViewModel")

    private val globalVarObs = object : IGlobalVarObserver.Stub() {
        override fun onAddOrUpdate(id: String?) {
            logger.d("globalVarObs onAddOrUpdate: $id")
            formatValues()
        }

        override fun onDelete(id: String?) {
            logger.d("globalVarObs onDelete: $id")
            formatValues()
        }
    }

    init {
        shortXManager.registerGlobalVarObs(globalVarObs)
    }

    fun init(labels: List<String>) {
        updateState { copy(values = labels) }
        formatValues()
    }

    fun formatValues() {
        viewModelScope.launchSafety {
            val values = state.value.values.map { str ->
                str.replaceGlobalVarValues { shortXManager.getAllGlobalVars() }
            }
            logger.d("values: $values")
            updateState {
                copy(
                    formattedValues = values
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterGlobalVarObs(globalVarObs)
    }
}
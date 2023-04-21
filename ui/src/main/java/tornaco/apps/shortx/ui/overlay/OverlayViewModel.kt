package tornaco.apps.shortx.ui.overlay

import android.icu.text.MessageFormat
import androidx.lifecycle.viewModelScope
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.valueList
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.ui.base.StateViewModel

data class OverlayState(
    val values: List<String> = emptyList(),
    val formattedValues: List<String> = emptyList()
)

class OverlayViewModel : StateViewModel<OverlayState>(OverlayState()) {
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
            val allVars = shortXManager.getAllGlobalVars()
                .associateBy { it.name }
                .mapValues {
                    val valueItems = it.value.valueList()
                    valueItems[0]
                }
            val values = state.value.values.map { str ->
                kotlin.runCatching { MessageFormat.format(str, allVars) }.getOrElse { str }
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
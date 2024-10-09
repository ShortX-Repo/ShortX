package tornaco.apps.shortx.ui.overlay

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.RuleContextAndVars
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.StateViewModel

data class ButtonState(val formattedLabel: String, val setting: OverlayButtonSetting)

data class OverlayState(
    val buttons: List<ButtonState> = emptyList(),
    val contextData: Map<String, String> = emptyMap(),
    val localVars: LocalVars = LocalVars.obtain(),
    val parameters: List<FuncParameterInput> = emptyList(),
) {
    val cv
        get() = RuleContextAndVars(
            contextData = ContextData.obtain(contextData),
            localVars = localVars,
            funcParameters = parameters,
            globalVarsLazy = { shortXManager.getAllGlobalVars() }
        )
}

class OverlayViewModel : StateViewModel<OverlayState, Unit>({ OverlayState() }) {
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

    fun init(
        action: ShowOverlayButton,
        localVars: LocalVars,
        contextData: Map<String, String>,
        parameters: List<FuncParameterInput>
    ) {
        updateState {
            copy(
                buttons = action.buttonSettingsList.map {
                    ButtonState(it.label, it)
                },
                localVars = localVars,
                contextData = contextData,
                parameters = parameters
            )
        }
        formatValues()
    }

    fun formatValues() {
        viewModelScope.launch {
            updateState {
                copy(buttons = buttons.map { button ->
                    val formattedLabel =
                        button.setting.label.compileContextAndVars(cv)
                    ButtonState(formattedLabel, button.setting)
                })
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterGlobalVarObs(globalVarObs)
    }
}
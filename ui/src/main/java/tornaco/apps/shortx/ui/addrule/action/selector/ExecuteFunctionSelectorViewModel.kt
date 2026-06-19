package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.func.FuncParameter
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.rule.asDefaultInput
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject
import tornaco.apps.shortx.core.i18n.I18N_R

data class ExecuteFunctionSelectorState(
    val functionId: String = "",
    val name: String? = null,
    val parameterInputs: List<FuncParameterInput> = emptyList(),
    val parameters: List<FuncParameter> = emptyList(),
    val loadErrorMessage: String? = null,
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ExecuteFunctionSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<ExecuteFunctionSelectorState>(
        context, { ExecuteFunctionSelectorState() }
    ) {

    private fun mergeParameterInputs(
        parameters: List<FuncParameter>,
        currentInputs: List<FuncParameterInput>
    ): List<FuncParameterInput> {
        return currentInputs.toMutableList().apply {
            addAll(parameters.filterNot { parameter ->
                val name = parameter.name
                any { input -> input.name == name }
            }.map { it.asDefaultInput() })
            removeIf { input -> parameters.none { parameter -> parameter.name == input.name } }
        }
    }

    private fun updateStateForFunction(
        functionId: String,
        fallbackName: String?,
        currentInputs: List<FuncParameterInput>
    ) {
        val function = shortXManager.getFunctionById(functionId)
        if (function == null) {
            updateState {
                copy(
                    functionId = functionId,
                    name = fallbackName,
                    parameters = emptyList(),
                    parameterInputs = currentInputs,
                    loadErrorMessage = i18N.get(
                        key = I18N_R.UI_ACTION_EXECUTE_FUNCTION_NOT_FOUND,
                        fallback = "Function not found, please set again"
                    )
                )
            }
            return
        }

        updateState {
            copy(
                functionId = function.id,
                name = function.name,
                parameters = function.parametersList,
                parameterInputs = mergeParameterInputs(function.parametersList, currentInputs),
                loadErrorMessage = null
            )
        }
    }

    fun initFrom(settings: Action.ExecuteFunction?) {
        if (settings == null) {
            resetState()
            return
        }

        updateStateForFunction(
            functionId = settings.functionId,
            fallbackName = settings.name,
            currentInputs = settings.parameterInputs
        )
    }

    fun selectFunction(functionId: String) {
        updateStateForFunction(
            functionId = functionId,
            fallbackName = null,
            currentInputs = emptyList()
        )
    }

    fun refreshSelectedFunction() {
        val currentState = state.value
        val functionId = currentState.functionId
        if (functionId.isBlank()) return

        updateStateForFunction(
            functionId = functionId,
            fallbackName = currentState.name,
            currentInputs = currentState.parameterInputs
        )
    }

    fun updateParamInput(update: FuncParameterInput) {
        updateState {
            copy(
                parameterInputs = parameterInputs.toMutableList().apply {
                    val index = indexOfFirst { it.name == update.name }
                    if (index >= 0) {
                        set(index, update)
                    }
                }
            )
        }
    }
}
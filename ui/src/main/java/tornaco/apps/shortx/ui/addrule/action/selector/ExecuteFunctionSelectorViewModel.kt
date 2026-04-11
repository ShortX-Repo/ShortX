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

    fun initFrom(settings: Action.ExecuteFunction?) {
        if (settings == null) {
            resetState()
            return
        }

        val function = shortXManager.getFunctionById(settings.functionId)
        if (function == null) {
            updateState {
                copy(
                    functionId = settings.functionId,
                    name = settings.name,
                    parameterInputs = settings.parameterInputs,
                    parameters = emptyList(),
                    loadErrorMessage = i18N.get(
                        key = "ui.action.execute.function.not.found",
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
                parameterInputs = settings.parameterInputs.toMutableList().apply {
                    addAll(function.parametersList.filterNot { parameter ->
                        val name = parameter.name
                        any { input -> input.name == name }
                    }.map { it.asDefaultInput() })
                },
                loadErrorMessage = null
            )
        }
    }

    fun selectFunction(functionId: String) {
        val function = shortXManager.getFunctionById(functionId)
        if (function == null) {
            updateState {
                copy(
                    functionId = functionId,
                    name = null,
                    parameters = emptyList(),
                    parameterInputs = emptyList(),
                    loadErrorMessage = i18N.get(
                        key = "ui.action.execute.function.not.found",
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
                parameterInputs = function.parametersList.map { it.asDefaultInput() },
                loadErrorMessage = null
            )
        }
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
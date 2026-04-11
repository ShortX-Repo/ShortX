package tornaco.apps.shortx.ui.function

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.func.FuncParameter
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.ShortXContextViewModelWithEffect
import java.util.UUID
import javax.inject.Inject
import tornaco.apps.shortx.core.proto.func.`Function` as ShortXFunction

fun defaultNewFunctionId() = "FUNC-" + UUID.randomUUID().toString()

data class AddFunctionState(
    val id: String = defaultNewFunctionId(),
    val name: String = "",
    val returnType: String = "string",
    val parameters: List<FuncParameter> = emptyList(),
    val actions: List<Action> = emptyList(),
    val createTime: Long = System.currentTimeMillis(),
    val lastUpdateTime: Long = System.currentTimeMillis(),
    val isEditFunction: Boolean = false,
    val functionChanged: Boolean = false,
)

sealed interface AddFunctionEffect {
    data class FunctionSaved(val functionId: String) : AddFunctionEffect
    data class FunctionDeleted(val functionId: String) : AddFunctionEffect
    data class AddParamFailNameExists(val name: String) : AddFunctionEffect
    data object FunctionNameRequired : AddFunctionEffect
    data object FunctionDeleteNotFound : AddFunctionEffect
}

@HiltViewModel
class AddFunctionViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModelWithEffect<AddFunctionState, AddFunctionEffect>(
        context,
        { AddFunctionState() }
    ) {

    val actionContentStateHandler = ActionContentStateHandler(
        onActionsChanged = { actionList ->
            updateState {
                copy(actions = actionList, functionChanged = true)
            }
        },
        parameterInputsProvider = {
            state.value.parameters
        }
    )

    private val shortx by lazy { shortXManager }

    fun setName(name: String) {
        if (name != state.value.name) {
            updateState {
                copy(name = name, functionChanged = true)
            }
        }
    }

    fun addParameter(parameter: FuncParameter, override: Boolean) {
        if (!override && state.value.parameters.any { it.name.lowercase() == parameter.name.lowercase() }) {
            postSideEffect(AddFunctionEffect.AddParamFailNameExists(parameter.name))
            return
        }
        updateState {
            copy(parameters = parameters.toMutableList().apply {
                val index = indexOfFirst { it.name == parameter.name }
                if (index >= 0) {
                    set(index, parameter)
                } else {
                    add(parameter)
                }
            }, functionChanged = true)
        }
    }

    fun removeParameter(parameter: FuncParameter) {
        updateState {
            copy(parameters = parameters.toMutableList().apply {
                removeIf { it.name == parameter.name }
            }, functionChanged = true)
        }
    }

    fun save() {
        viewModelScope.launch {
            if (state.value.name.isBlank()) {
                postSideEffect(AddFunctionEffect.FunctionNameRequired)
                return@launch
            }

            val protoActions = actionContentStateHandler.state.value.actions.map {
                it.toProtoAction().pack_()
            }

            val function = ShortXFunction.newBuilder()
                .setId(state.value.id)
                .setName(state.value.name)
                .setReturnType(state.value.returnType)
                .addAllParameters(state.value.parameters)
                .addAllActions(protoActions)
                .setCreateTime(state.value.createTime)
                .setLastUpdateTime(System.currentTimeMillis())
                .build()

            shortx.addFunction(function)
            postSideEffect(AddFunctionEffect.FunctionSaved(function.id))
        }
    }

    fun delete(functionId: String) {
        viewModelScope.launch {
            if (shortx.getFunctionById(functionId) == null) {
                postSideEffect(AddFunctionEffect.FunctionDeleteNotFound)
                return@launch
            }
            shortx.deleteFunction(functionId)
            postSideEffect(AddFunctionEffect.FunctionDeleted(functionId))
        }
    }

    fun initFromFunctionId(functionId: String?) {
        functionId?.let { id ->
            initFromFunction(shortx.getFunctionById(id))
        }
    }

    fun initFromFunction(function: ShortXFunction?) {
        viewModelScope.launch {
            function?.let {
                actionContentStateHandler.init(
                    actions = it.actionsList.mapNotNull { action -> action.toAction(shortx) }
                )
                updateState {
                    copy(
                        id = it.id,
                        name = it.name,
                        returnType = it.returnType.fallbackOnEmpty("string"),
                        parameters = it.parametersList,
                        actions = actionContentStateHandler.actions,
                        createTime = it.createTime,
                        lastUpdateTime = it.lastUpdateTime,
                        isEditFunction = true,
                    )
                }
            }
        }
    }
}
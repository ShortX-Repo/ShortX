package tornaco.apps.shortx.ui.function

import android.content.Context
import com.google.protobuf.Any
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.main.model.daToDirectActionUM
import tornaco.apps.shortx.ui.main.model.ruleToRuleUM
import javax.inject.Inject
import tornaco.apps.shortx.core.proto.func.`Function` as ShortXFunction

data class FunctionReferenceScanResult(
    val ruleTitles: List<String> = emptyList(),
    val directActionTitles: List<String> = emptyList(),
    val functionNames: List<String> = emptyList(),
) {
    val hasReferences: Boolean
        get() = ruleTitles.isNotEmpty() || directActionTitles.isNotEmpty() || functionNames.isNotEmpty()
}

data class PendingDeleteFunction(
    val function: ShortXFunction,
    val references: FunctionReferenceScanResult,
)

data class FunctionItemUiModel(
    val id: String,
    val name: String,
    val returnType: String,
    val parametersCount: Int,
    val actionsCount: Int,
    val raw: ShortXFunction,
)

data class FunctionListState(
    val functions: List<FunctionItemUiModel> = emptyList(),
    val pendingDelete: PendingDeleteFunction? = null,
    val promptMessage: String? = null,
)

@HiltViewModel
class FunctionListViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<FunctionListState>(context, { FunctionListState() }) {

    fun loadState() {
        viewModelScope.launch {
            val functions = shortXManager.getAllFunctions()
                .sortedByDescending { it.lastUpdateTime }
                .map {
                    FunctionItemUiModel(
                        id = it.id,
                        name = it.name,
                        returnType = it.returnType,
                        parametersCount = it.parametersCount,
                        actionsCount = it.actionsCount,
                        raw = it,
                    )
                }
            updateState { copy(functions = functions) }
        }
    }

    fun deleteFunction(id: String) {
        viewModelScope.launch {
            if (shortXManager.getFunctionById(id) == null) {
                updateState {
                    copy(
                        pendingDelete = null,
                        promptMessage = i18N["ui.function.delete.not.found"]
                    )
                }
                loadState()
                return@launch
            }

            shortXManager.deleteFunction(id)
            updateState { copy(pendingDelete = null, promptMessage = null) }
            loadState()
        }
    }

    fun prepareDelete(function: ShortXFunction) {
        viewModelScope.launch {
            updateState {
                copy(
                    pendingDelete = PendingDeleteFunction(
                        function = function,
                        references = scanReferences(function.id)
                    )
                )
            }
        }
    }

    fun dismissDelete() {
        updateState { copy(pendingDelete = null) }
    }

    fun consumePromptMessage() {
        updateState { copy(promptMessage = null) }
    }

    fun confirmDeletePending() {
        state.value.pendingDelete?.function?.id?.let(::deleteFunction)
    }

    private fun scanReferences(functionId: String): FunctionReferenceScanResult {
        val globalVars = shortXManager.getAllGlobalVars()

        val ruleTitles = shortXManager.getAllRules().mapNotNull { rule ->
            val hasReference = rule.actionsList.toActions().containsFunctionReference(functionId)
                    || rule.quit.actionsList.toActions().containsFunctionReference(functionId)
                    || rule.hook?.actionsOnEnabledList.toActions().containsFunctionReference(functionId)
                    || rule.hook?.actionsOnDisabledList.toActions().containsFunctionReference(functionId)
                    || rule.hook?.actionsOnDeletedList.toActions().containsFunctionReference(functionId)
                    || rule.hook?.actionsOnStopList.toActions().containsFunctionReference(functionId)
                    || rule.hook?.actionsOnUpdatedList.toActions().containsFunctionReference(functionId)

            if (hasReference) ruleToRuleUM(rule, globalVars).title else null
        }.distinct()

        val directActionTitles = shortXManager.getAllDirectAction().mapNotNull { directAction ->
            val hasReference = directAction.actionsList.toActions().containsFunctionReference(functionId)
                    || directAction.quit.actionsList.toActions().containsFunctionReference(functionId)
                    || directAction.hook?.actionsOnStopList.toActions().containsFunctionReference(functionId)

            if (hasReference) daToDirectActionUM(directAction, globalVars).title else null
        }.distinct()

        val functionNames = shortXManager.getAllFunctions().mapNotNull { function ->
            if (function.id == functionId) {
                null
            } else {
                val hasReference = function.actionsList.toActions().containsFunctionReference(functionId)
                if (hasReference) function.name else null
            }
        }.distinct()

        return FunctionReferenceScanResult(
            ruleTitles = ruleTitles,
            directActionTitles = directActionTitles,
            functionNames = functionNames,
        )
    }

    private fun List<Any>?.toActions(): List<Action> {
        return this?.mapNotNull { any -> any.toAction(shortXManager) } ?: emptyList()
    }

    private fun List<Action>.containsFunctionReference(functionId: String): Boolean {
        return any { action -> action.containsFunctionReference(functionId) }
    }

    private fun Action.containsFunctionReference(functionId: String): Boolean {
        return when (this) {
            is Action.ExecuteFunction -> this.functionId == functionId
            is Action.ShowStatusBarChip -> clickActions.containsFunctionReference(functionId)
                    || longClickActions.containsFunctionReference(functionId)
            is Action.ShowOverlay -> settings.containsFunctionReferenceInOverlay(functionId)
            is Action.ShowFloatWindow -> settings.containsFunctionReferenceInOverlay(functionId)
            is Action.ShowSideBar -> settings.containsFunctionReferenceInOverlay(functionId)
            is Action.PostNotification -> onClickActions.containsFunctionReference(functionId)
            is Action.ShowPPN -> onClickActions.containsFunctionReference(functionId)
            is Action.WhileLoop -> actions.containsFunctionReference(functionId)
            is Action.ForEach -> actions.containsFunctionReference(functionId)
            is Action.ForEachPkgSet -> actions.containsFunctionReference(functionId)
            is Action.ShowAlertDialog -> positiveActions.containsFunctionReference(functionId)
                    || negativeActions.containsFunctionReference(functionId)
                    || neutralActions.containsFunctionReference(functionId)
            is Action.IfThenElse -> ifActions.containsFunctionReference(functionId)
                    || elseActions.containsFunctionReference(functionId)
            is Action.SwitchCase -> cases.any { it.actions.containsFunctionReference(functionId) }
                    || deft.actions.containsFunctionReference(functionId)
            is Action.BiometricVerify -> allowActions.containsFunctionReference(functionId)
                    || denyActions.containsFunctionReference(functionId)
            is Action.WebSocketConnect -> openActions.containsFunctionReference(functionId)
                    || closeActions.containsFunctionReference(functionId)
                    || messageActions.containsFunctionReference(functionId)
                    || failureActions.containsFunctionReference(functionId)
            else -> false
        }
    }

    private fun List<OverlayButtonSetting>.containsFunctionReferenceInOverlay(functionId: String): Boolean {
        return any { setting ->
            setting.actionsList.toActions().containsFunctionReference(functionId)
                    || setting.longClickActionsList.toActions().containsFunctionReference(functionId)
        }
    }
}
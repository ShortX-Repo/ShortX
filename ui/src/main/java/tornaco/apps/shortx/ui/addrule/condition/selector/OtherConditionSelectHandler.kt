package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectSource
import tornaco.apps.shortx.ui.addrule.condition.EvaluateGlobalVarSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.getConditionAs
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.rememberEvaluateGlobalVarSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.code.CodeEditorDialog
import tornaco.apps.shortx.ui.base.code.rememberCodeEditorState

@Composable
fun otherConditionSelectHandler(addCondition: (Condition) -> Unit): (ConditionSelectSource) -> Boolean {
    val mvelConditionSelectorState =
        rememberCodeEditorState(title = LocalI18N.current["ui.title.mvel"],
            execute = {
                shortXManager.executeMVEL(it)
            }) { expression ->
            addCondition(Condition.MVEL(expression.src))
        }
    CodeEditorDialog(state = mvelConditionSelectorState)

    val evaluateGlobalVarSelectorDialogState = rememberEvaluateGlobalVarSelectorState {
        addCondition(it)
    }
    EvaluateGlobalVarSelectorDialog(state = evaluateGlobalVarSelectorDialogState)

    return { source ->
        when (source.clazz) {
            Condition.MVEL::class -> {
                mvelConditionSelectorState.show(source.getConditionAs<Condition.MVEL>()?.expression)
                true
            }

            Condition.TRUE::class -> {
                addCondition(Condition.TRUE())
                true
            }

            Condition.EvaluateGlobalVar::class -> {
                evaluateGlobalVarSelectorDialogState.show(source.getConditionAs())
                true
            }

            else -> false
        }
    }
}
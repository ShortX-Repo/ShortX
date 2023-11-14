package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.selector.DelaySelectorDialog
import tornaco.apps.shortx.ui.addrule.action.selector.SetRuleEnabledSelectorDialog
import tornaco.apps.shortx.ui.addrule.action.selector.rememberDelayState
import tornaco.apps.shortx.ui.addrule.action.selector.rememberSetRuleEnabledDialogState
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectSource
import tornaco.apps.shortx.ui.addrule.condition.EvaluateGlobalVarSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.EvaluateLocalVarSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.getConditionAs
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.rememberEvaluateGlobalVarSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberEvaluateLocalVarSelectorState
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

    val jsConditionSelectorState =
        rememberCodeEditorState(title = LocalI18N.current["ui.title.js"],
            execute = {
                shortXManager.executeJS(it)
            }) { expression ->
            addCondition(Condition.JS(expression.src))
        }
    CodeEditorDialog(state = jsConditionSelectorState)

    val evaluateGlobalVarSelectorDialogState = rememberEvaluateGlobalVarSelectorState {
        addCondition(it)
    }
    EvaluateGlobalVarSelectorDialog(state = evaluateGlobalVarSelectorDialogState)

    val evaluateLocalVarSelectorDialogState = rememberEvaluateLocalVarSelectorState {
        addCondition(it)
    }
    EvaluateLocalVarSelectorDialog(state = evaluateLocalVarSelectorDialogState)

    val setRuleEnabledState = rememberSetRuleEnabledDialogState(
        LocalI18N.current["ui.condition.is.rule.enabled"],
        onSelected = {
            addCondition(
                Condition.IsRuleEnabled(
                    ruleId = it.ruleId,
                    isEnabled = it.enableRule,
                    ruleLabel = it.ruleLabel
                )
            )
        })
    SetRuleEnabledSelectorDialog(state = setRuleEnabledState)


    val delaySelectionState =
        rememberDelayState(
            canUseAlarm = true,
            onSelected = {
                addCondition(Condition.RequireDelay(timeString = it.time, isAlarm = it.useAlarm))
            })
    DelaySelectorDialog(state = delaySelectionState)

    return { source ->
        when (source.clazz) {
            Condition.MVEL::class -> {
                mvelConditionSelectorState.show(source.getConditionAs<Condition.MVEL>()?.expression)
                true
            }

            Condition.JS::class -> {
                jsConditionSelectorState.show(source.getConditionAs<Condition.JS>()?.expression)
                true
            }

            Condition.TRUE::class -> {
                addCondition(Condition.TRUE())
                true
            }

            Condition.RequireDelay::class -> {
                delaySelectionState.show(
                    initialTime = source.getConditionAs<Condition.RequireDelay>()?.timeString,
                    useAlarm = source.getConditionAs<Condition.RequireDelay>()?.isAlarm
                )
                true
            }

            Condition.EvaluateGlobalVar::class -> {
                evaluateGlobalVarSelectorDialogState.show(source.getConditionAs())
                true
            }

            Condition.EvaluateLocalVar::class -> {
                evaluateLocalVarSelectorDialogState.show(source.getConditionAs())
                true
            }

            Condition.IsRuleEnabled::class -> {
                val currentValue = source.getConditionAs<Condition.IsRuleEnabled>()
                setRuleEnabledState.show(
                    currentValue?.let {
                        Action.SetRuleEnabled(
                            ruleId = currentValue.ruleId,
                            enableRule = currentValue.isEnabled,
                            ruleLabel = currentValue.ruleLabel
                        )
                    }
                )
                true
            }

            else -> false
        }
    }
}
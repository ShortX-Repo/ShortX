package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.selector.DelaySelectorDialog
import tornaco.apps.shortx.ui.addrule.action.selector.SetRuleEnabledSelectorDialog
import tornaco.apps.shortx.ui.addrule.action.selector.rememberDelayState
import tornaco.apps.shortx.ui.addrule.action.selector.rememberSetRuleEnabledDialogState
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectSource
import tornaco.apps.shortx.ui.addrule.condition.EvaluateContextVarSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.EvaluateGlobalVarSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.EvaluateLocalVarSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.TheXXTimeTodaySelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.getConditionAs
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.rememberEvaluateContextVarSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberEvaluateGlobalVarSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberEvaluateLocalVarSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberTheXXTimeTodaySelectorState
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.toBaseSelectorCurrentDataOrNull
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.code.ParcelableCode
import tornaco.apps.shortx.ui.base.code.js.JSEditorActivity
import tornaco.apps.shortx.ui.base.code.mvel.MVELEditorActivity
import tornaco.apps.shortx.ui.base.code.toParcelableCodeOrNull
import tornaco.apps.shortx.ui.base.rememberTextInputState

@Composable
fun otherConditionSelectHandler(addCondition: (Condition) -> Unit): (ConditionSelectSource) -> Boolean {
    val mvelEditorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.toBaseSelectorCurrentDataOrNull<ParcelableCode>()
            action?.let {
                addCondition(Condition.MVEL(it.expression))
            }
        }
    )

    val jsEditorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.toBaseSelectorCurrentDataOrNull<ParcelableCode>()
            action?.let {
                addCondition(Condition.JS(it.expression))
            }
        }
    )

    val evaluateGlobalVarSelectorDialogState = rememberEvaluateGlobalVarSelectorState {
        addCondition(it)
    }
    EvaluateGlobalVarSelectorDialog(state = evaluateGlobalVarSelectorDialogState)

    val evaluateContextVarSelectorDialogState = rememberEvaluateContextVarSelectorState {
        addCondition(it)
    }
    EvaluateContextVarSelectorDialog(state = evaluateContextVarSelectorDialogState)

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

    val factTagSelectorState = rememberTextInputState(
        title = LocalI18N.current["ui.condition.fact.tag"],
        message = null,
        onSelected = {
            addCondition(Condition.RequireFactTag(tag = it))
        })
    TextInputDialog(state = factTagSelectorState)

    val doXXAtTimeState = rememberTheXXTimeTodaySelectorState {
        addCondition(it)
    }
    TheXXTimeTodaySelectorDialog(state = doXXAtTimeState)

    val context = LocalContext.current

    return { source ->
        when (source.clazz) {
            Condition.MVEL::class -> {
                mvelEditorLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getConditionAs<Condition.MVEL>()?.expression?.toParcelableCodeOrNull(),
                        MVELEditorActivity::class.java
                    )
                )
                true
            }

            Condition.JS::class -> {
                jsEditorLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getConditionAs<Condition.JS>()?.expression?.toParcelableCodeOrNull(),
                        JSEditorActivity::class.java
                    )
                )
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

            Condition.EvaluateContextVar::class -> {
                evaluateContextVarSelectorDialogState.show(source.getConditionAs())
                true
            }

            Condition.EvaluateLocalVar::class -> {
                evaluateLocalVarSelectorDialogState.show(source.getConditionAs())
                true
            }

            Condition.RequireFactTag::class -> {
                factTagSelectorState.show(source.getConditionAs<Condition.RequireFactTag>()?.tag)
                true
            }

            Condition.TheXXTimeToday::class -> {
                doXXAtTimeState.show(source.getConditionAs())
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
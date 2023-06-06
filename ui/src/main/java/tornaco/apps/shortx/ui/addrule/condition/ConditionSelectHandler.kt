package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.code.CodeEditorDialog
import tornaco.apps.shortx.ui.base.code.rememberCodeEditorState
import kotlin.reflect.KClass

@Composable
fun conditionSelectHandler(addCondition: (Condition) -> Unit): (KClass<out Condition>) -> Boolean {
    // Condition selectors
    val currentPkgSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = { appList ->
                addCondition(
                    Condition.CurrentForegroundApp(
                        apps = appList,
                        pkgSets = emptyList()
                    )
                )
            },
            onPkgSetSelected = { pkgSets ->
                addCondition(
                    Condition.CurrentForegroundApp(
                        apps = emptyList(),
                        pkgSets = pkgSets
                    )
                )
            })
    AppPkgSelectorDialog(currentPkgSelectorState)

    val mvelConditionSelectorState =
        rememberCodeEditorState(title = LocalI18N.current["ui.title.mvel"]) { expression ->
            addCondition(Condition.MVEL(expression.src))
        }
    CodeEditorDialog(state = mvelConditionSelectorState)

    val evaluateGlobalVarSelectorDialogState = rememberEvaluateGlobalVarSelectorState {
        addCondition(it)
    }
    EvaluateGlobalVarSelectorDialog(state = evaluateGlobalVarSelectorDialogState)

    val batteryPercentDialog = rememberBatteryPercentSelectorState {
        addCondition(it)
    }
    BatteryPercentSelectorDialog(state = batteryPercentDialog)

    return { conClass ->
        when (conClass) {
            Condition.CurrentForegroundApp::class -> {
                currentPkgSelectorState.show()
                true
            }

            Condition.MVEL::class -> {
                mvelConditionSelectorState.show()
                true
            }

            Condition.TRUE::class -> {
                addCondition(Condition.TRUE())
                true
            }

            Condition.EvaluateGlobalVar::class -> {
                evaluateGlobalVarSelectorDialogState.show()
                true
            }

            Condition.BatteryPercent::class -> {
                batteryPercentDialog.show()
                true
            }

            else -> false
        }
    }
}
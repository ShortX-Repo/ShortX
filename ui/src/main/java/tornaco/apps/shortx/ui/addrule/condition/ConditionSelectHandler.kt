package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.code.CodeEditorDialog
import tornaco.apps.shortx.ui.base.code.rememberCodeEditorState

@Composable
fun conditionSelectHandler(addCondition: (Condition) -> Unit): (ConditionSelectSource) -> Boolean {
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

    val appIsRunningSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = { appList ->
                addCondition(
                    Condition.AppIsRunning(
                        apps = appList,
                        pkgSets = emptyList()
                    )
                )
            },
            onPkgSetSelected = { pkgSets ->
                addCondition(
                    Condition.AppIsRunning(
                        apps = emptyList(),
                        pkgSets = pkgSets
                    )
                )
            })
    AppPkgSelectorDialog(appIsRunningSelectorState)

    val appIsNotRunningSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = { appList ->
                addCondition(
                    Condition.AppIsNotRunning(
                        apps = appList,
                        pkgSets = emptyList()
                    )
                )
            },
            onPkgSetSelected = { pkgSets ->
                addCondition(
                    Condition.AppIsNotRunning(
                        apps = emptyList(),
                        pkgSets = pkgSets
                    )
                )
            })
    AppPkgSelectorDialog(appIsNotRunningSelectorState)

    val appHasAudioFocusSelectorState =
        rememberSingleAppPkgSelectorState {
            addCondition(
                Condition.AppHasAudioFocus(
                    apps = listOf(it),
                    pkgSets = emptyList()
                )
            )
        }
    SingleAppSelectorDialog(appHasAudioFocusSelectorState)

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

    return { source ->
        when (source.clazz) {
            Condition.CurrentForegroundApp::class -> {
                currentPkgSelectorState.show(
                    source.getConditionAs<Condition.CurrentForegroundApp>()?.apps,
                    source.getConditionAs<Condition.CurrentForegroundApp>()?.pkgSets
                )
                true
            }

            Condition.AppIsRunning::class -> {
                appIsRunningSelectorState.show(
                    source.getConditionAs<Condition.AppIsRunning>()?.apps,
                    source.getConditionAs<Condition.AppIsRunning>()?.pkgSets
                )
                true
            }

            Condition.AppIsNotRunning::class -> {
                appIsNotRunningSelectorState.show(
                    source.getConditionAs<Condition.AppIsNotRunning>()?.apps,
                    source.getConditionAs<Condition.AppIsNotRunning>()?.pkgSets
                )
                true
            }

            Condition.AppHasAudioFocus::class -> {
                appHasAudioFocusSelectorState.show()
                true
            }

            Condition.MVEL::class -> {
                mvelConditionSelectorState.show(source.getConditionAs<Condition.MVEL>()?.expression)
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
package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.condition.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.ComponentOrGVSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.ComponentsOrGV
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectSource
import tornaco.apps.shortx.ui.addrule.condition.getConditionAs
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.condition.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberComponentOrGVSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.ComponentType
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogState

@Composable
fun appConditionSelectHandler(addCondition: (Condition) -> Unit): (ConditionSelectSource) -> Boolean {
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
        rememberAppPkgSelectorState(
            onAppListSelected = { appList ->
                addCondition(
                    Condition.AppHasAudioFocus(
                        apps = appList,
                        pkgSets = emptyList()
                    )
                )
            },
            onPkgSetSelected = { pkgSets ->
                addCondition(
                    Condition.AppHasAudioFocus(
                        apps = emptyList(),
                        pkgSets = pkgSets
                    )
                )
            })
    AppPkgSelectorDialog(appHasAudioFocusSelectorState)


    val serviceIsRunningOpSelectorDialog: MenuDialogState<ComponentsOrGV> =
        rememberAllAnyMenuDialogState(
            conditionTitle = LocalI18N.current["ui.conditions.service.running"],
            onItemSelected = { componentsOrGV, op ->
                addCondition(
                    Condition.ServiceIsRunning(
                        services = requireNotNull(componentsOrGV?.components) { "Data to menu dialog is null!!" },
                        op = op
                    )
                )
            })
    MenuDialog(state = serviceIsRunningOpSelectorDialog)
    val serviceIsRunningSelectorState = rememberComponentOrGVSelectorState(
        types = listOf(ComponentType.Service),
        onSelected = {
            if (it.components.size > 1) {
                serviceIsRunningOpSelectorDialog.show(it)
            } else {
                addCondition(
                    Condition.ServiceIsRunning(
                        services = it.components,
                        op = Op.All
                    )
                )
            }
        }
    )
    ComponentOrGVSelectorDialog(state = serviceIsRunningSelectorState)

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

            Condition.ServiceIsRunning::class -> {
                serviceIsRunningSelectorState.show(source.getConditionAs<Condition.ServiceIsRunning>()?.services)
                true
            }

            Condition.AppHasAudioFocus::class -> {
                appHasAudioFocusSelectorState.show(
                    source.getConditionAs<Condition.AppHasAudioFocus>()?.apps,
                    source.getConditionAs<Condition.AppHasAudioFocus>()?.pkgSets
                )
                true
            }

            else -> false
        }
    }
}
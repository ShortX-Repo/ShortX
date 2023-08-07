package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.condition.ComponentOrGVSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.ConditionAppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectSource
import tornaco.apps.shortx.ui.addrule.condition.getConditionAs
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.rememberComponentOrGVSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberConditionAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.ComponentType

@Composable
fun appConditionSelectHandler(addCondition: (Condition) -> Unit): (ConditionSelectSource) -> Boolean {
    val currentPkgSelectorState =
        rememberConditionAppPkgSelectorState(
            onAppListSelected = { appList, op ->
                addCondition(
                    Condition.CurrentForegroundApp(
                        apps = appList,
                        pkgSets = emptyList(),
                        op
                    )
                )
            },
            onPkgSetSelected = { pkgSets, op ->
                addCondition(
                    Condition.CurrentForegroundApp(
                        apps = emptyList(),
                        pkgSets = pkgSets,
                        op
                    )
                )
            })
    ConditionAppPkgSelectorDialog(currentPkgSelectorState)

    val appHasNSelectorState =
        rememberConditionAppPkgSelectorState(
            onAppListSelected = { appList, op ->
                addCondition(
                    Condition.AppHasNotification(
                        apps = appList,
                        pkgSets = emptyList(),
                        op
                    )
                )
            },
            onPkgSetSelected = { pkgSets, op ->
                addCondition(
                    Condition.AppHasNotification(
                        apps = emptyList(),
                        pkgSets = pkgSets,
                        op
                    )
                )
            })
    ConditionAppPkgSelectorDialog(appHasNSelectorState)

    val appIsRunningSelectorState =
        rememberConditionAppPkgSelectorState(
            onAppListSelected = { appList, op ->
                addCondition(
                    Condition.AppIsRunning(
                        apps = appList,
                        pkgSets = emptyList(),
                        op
                    )
                )
            },
            onPkgSetSelected = { pkgSets, op ->
                addCondition(
                    Condition.AppIsRunning(
                        apps = emptyList(),
                        pkgSets = pkgSets,
                        op
                    )
                )
            })
    ConditionAppPkgSelectorDialog(appIsRunningSelectorState)

    val appIsNotRunningSelectorState =
        rememberConditionAppPkgSelectorState(
            onAppListSelected = { appList, op ->
                addCondition(
                    Condition.AppIsNotRunning(
                        apps = appList,
                        pkgSets = emptyList(),
                        op
                    )
                )
            },
            onPkgSetSelected = { pkgSets, op ->
                addCondition(
                    Condition.AppIsNotRunning(
                        apps = emptyList(),
                        pkgSets = pkgSets,
                        op
                    )
                )
            })
    ConditionAppPkgSelectorDialog(appIsNotRunningSelectorState)


    val appHasAudioFocusSelectorState =
        rememberConditionAppPkgSelectorState(
            onAppListSelected = { appList, op ->
                addCondition(
                    Condition.AppHasAudioFocus(
                        apps = appList,
                        pkgSets = emptyList(),
                        op
                    )
                )
            },
            onPkgSetSelected = { pkgSets, op ->
                addCondition(
                    Condition.AppHasAudioFocus(
                        apps = emptyList(),
                        pkgSets = pkgSets,
                        op
                    )
                )
            })
    ConditionAppPkgSelectorDialog(appHasAudioFocusSelectorState)

    val serviceIsRunningSelectorState = rememberComponentOrGVSelectorState(
        types = listOf(ComponentType.Service),
        onSelected = { it, op ->
            addCondition(
                Condition.ServiceIsRunning(
                    services = it.components,
                    op = op
                )
            )
        }
    )
    ComponentOrGVSelectorDialog(state = serviceIsRunningSelectorState)

    return { source ->
        when (source.clazz) {
            Condition.CurrentForegroundApp::class -> {
                currentPkgSelectorState.show(
                    source.getConditionAs<Condition.CurrentForegroundApp>()?.apps,
                    source.getConditionAs<Condition.CurrentForegroundApp>()?.pkgSets,
                    source.getConditionAs<Condition.CurrentForegroundApp>()?.op
                )
                true
            }

            Condition.AppIsRunning::class -> {
                appIsRunningSelectorState.show(
                    source.getConditionAs<Condition.AppIsRunning>()?.apps,
                    source.getConditionAs<Condition.AppIsRunning>()?.pkgSets,
                    source.getConditionAs<Condition.AppIsRunning>()?.op,
                )
                true
            }

            Condition.AppIsNotRunning::class -> {
                appIsNotRunningSelectorState.show(
                    source.getConditionAs<Condition.AppIsNotRunning>()?.apps,
                    source.getConditionAs<Condition.AppIsNotRunning>()?.pkgSets,
                    source.getConditionAs<Condition.AppIsNotRunning>()?.op,
                )
                true
            }

            Condition.AppHasNotification::class -> {
                appHasNSelectorState.show(
                    source.getConditionAs<Condition.AppHasNotification>()?.apps,
                    source.getConditionAs<Condition.AppHasNotification>()?.pkgSets,
                    source.getConditionAs<Condition.AppHasNotification>()?.op,
                )
                true
            }

            Condition.ServiceIsRunning::class -> {
                serviceIsRunningSelectorState.show(
                    source.getConditionAs<Condition.ServiceIsRunning>()?.services,
                    source.getConditionAs<Condition.ServiceIsRunning>()?.op
                )
                true
            }

            Condition.AppHasAudioFocus::class -> {
                appHasAudioFocusSelectorState.show(
                    source.getConditionAs<Condition.AppHasAudioFocus>()?.apps,
                    source.getConditionAs<Condition.AppHasAudioFocus>()?.pkgSets,
                    source.getConditionAs<Condition.AppHasAudioFocus>()?.op,
                )
                true
            }

            else -> false
        }
    }
}
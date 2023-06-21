package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.base.SingleAppSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.base.rememberSingleAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.condition.ComponentOrGVSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.rememberComponentOrGVSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.AppComponentSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.ComponentType
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAppComponentSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N

@Composable
fun appActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val launchAppSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val first = it[0]
                val action = Action.LaunchApp(app = first)
                addAction(action)
            })
    AppPkgSelectorDialog(state = launchAppSelectorState)

    val stopAppSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.StopApp(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.StopApp(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = stopAppSelectorState)

    val removeTasksSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.RemoveTasks(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.RemoveTasks(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = removeTasksSelectorState)

    val startAppProcessSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.StartAppProcess(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.StartAppProcess(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = startAppProcessSelectorState)

    val setAppInactiveSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.SetAppInactive(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.SetAppInactive(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = setAppInactiveSelectorState)

    val enableAppProcessSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.EnableApp(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.EnableApp(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = enableAppProcessSelectorState)

    val disableAppProcessSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.DisableApp(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.DisableApp(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = disableAppProcessSelectorState)

    val suspendAppProcessSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.SuspendApp(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.SuspendApp(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = suspendAppProcessSelectorState)

    val unsuspendAppProcessSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.UnSuspendApp(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.UnSuspendApp(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = unsuspendAppProcessSelectorState)

    val removeNotificationDialogState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.RemoveNotificationForPackage(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.RemoveNotificationForPackage(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = removeNotificationDialogState)

    val startActivityIntentSelectorState = rememberStartActivityIntentDialogState(onSelected = {
        addAction(it)
    })
    StartActivityIntentDialog(state = startActivityIntentSelectorState)

    val startActivityComponentSelectorState = rememberAppComponentSelectorState(
        types = listOf(ComponentType.Activity),
        maxItemCount = 1,
        onAppComponentSelected = { infoList ->
            if (infoList.isNotEmpty()) {
                addAction(
                    Action.StartActivity(
                        componentNameAsString = infoList[0].component.toComponentName()
                            .flattenToString()
                    )
                )
            }
        }
    )
    AppComponentSelectorDialog(startActivityComponentSelectorState)

    val singleAppSelectorState = rememberSingleAppPkgSelectorState {
        startActivityComponentSelectorState.show(it.pkg)
    }
    SingleAppSelectorDialog(state = singleAppSelectorState)


    val startActivityUrlSchemaDialogState = rememberStartActivityUrlSchemaDialogState {
        addAction(it)
    }
    StartActivityUrlSchemaDialog(state = startActivityUrlSchemaDialogState)


    val stopServiceSelectorState =
        rememberComponentOrGVSelectorState(
            types = listOf(ComponentType.Service),
            onSelected = { it, _ ->
                addAction(Action.StopService(services = it.components))
            })
    ComponentOrGVSelectorDialog(state = stopServiceSelectorState)

    val killProcessSelectorState = rememberProcessListSelectorState(onSelected = {
        addAction(Action.KillProcessByName(processList = it))
    })
    ProcessListSelectorDialog(state = killProcessSelectorState)

    return { source: ActionSelectSource ->
        when (source.clazz) {
            Action.StartActivityIntent::class -> {
                startActivityIntentSelectorState.show(source.getActionAs())
                true
            }

            Action.StartActivityUrlSchema::class -> {
                startActivityUrlSchemaDialogState.show(source.getActionAs())
                true
            }

            Action.StartActivity::class -> {
                singleAppSelectorState.show()
                true
            }

            Action.LaunchApp::class -> {
                launchAppSelectorState.show(source.getActionAs<Action.LaunchApp>()?.app?.let {
                    listOf(it)
                })
                true
            }

            Action.StopApp::class -> {
                stopAppSelectorState.show(
                    source.getActionAs<Action.StopApp>()?.apps,
                    source.getActionAs<Action.StopApp>()?.pkgSets
                )
                true
            }

            Action.RemoveTasks::class -> {
                removeTasksSelectorState.show(
                    source.getActionAs<Action.RemoveTasks>()?.apps,
                    source.getActionAs<Action.RemoveTasks>()?.pkgSets
                )
                true
            }

            Action.StartAppProcess::class -> {
                startAppProcessSelectorState.show(
                    source.getActionAs<Action.StartAppProcess>()?.apps,
                    source.getActionAs<Action.StartAppProcess>()?.pkgSets
                )
                true
            }

            Action.SetAppInactive::class -> {
                setAppInactiveSelectorState.show(
                    source.getActionAs<Action.SetAppInactive>()?.apps,
                    source.getActionAs<Action.SetAppInactive>()?.pkgSets
                )
                true
            }

            Action.EnableApp::class -> {
                enableAppProcessSelectorState.show(
                    source.getActionAs<Action.EnableApp>()?.apps,
                    source.getActionAs<Action.EnableApp>()?.pkgSets
                )
                true
            }

            Action.DisableApp::class -> {
                disableAppProcessSelectorState.show(
                    source.getActionAs<Action.DisableApp>()?.apps,
                    source.getActionAs<Action.DisableApp>()?.pkgSets
                )
                true
            }

            Action.SuspendApp::class -> {
                suspendAppProcessSelectorState.show(
                    source.getActionAs<Action.SuspendApp>()?.apps,
                    source.getActionAs<Action.SuspendApp>()?.pkgSets
                )
                true
            }

            Action.UnSuspendApp::class -> {
                unsuspendAppProcessSelectorState.show(
                    source.getActionAs<Action.UnSuspendApp>()?.apps,
                    source.getActionAs<Action.UnSuspendApp>()?.pkgSets
                )
                true
            }

            Action.StopService::class -> {
                stopServiceSelectorState.show(source.getActionAs<Action.StopService>()?.services)
                true
            }

            Action.KillProcessByName::class -> {
                killProcessSelectorState.show(source.getActionAs<Action.KillProcessByName>()?.processList)
                true
            }

            Action.RemoveNotificationForPackage::class -> {
                removeNotificationDialogState.show(
                    source.getActionAs<Action.RemoveNotificationForPackage>()?.apps,
                    source.getActionAs<Action.RemoveNotificationForPackage>()?.pkgSets
                )
                true
            }

            else -> false
        }
    }
}

@Composable
fun AppActions(addAction: (Action) -> Unit) {
    val i18N = LocalI18N.current
    val actionItems = arrayOf(
        Action.StartActivity::class,
        Action.StartActivityIntent::class,
        Action.StartActivityUrlSchema::class,
        Action.LaunchApp::class,
        Action.StopApp::class,
        Action.StartAppProcess::class,
        Action.SetAppInactive::class,
        Action.EnableApp::class,
        Action.DisableApp::class,
        Action.SuspendApp::class,
        Action.UnSuspendApp::class,
        Action.StopService::class,
        Action.RemoveTasks::class,
        Action.KillProcessByName::class,
        Action.RemoveNotificationForPackage::class,
    ).map {
        SelectorItem(
            type = it,
            labelAndDescription = labelAndDescriptionForActionSelector(i18N, it),
            icon = imageVectorForAction(it),
            iconColor = colorForAction(it)
        )
    }

    val selectHandler = appActionSelectHandler(addAction = addAction)

    SelectorList(
        actionItems = actionItems,
        itemSelected = { it, _ ->
            selectHandler(ActionSelectSource.FromClass(it))
        }
    )
}
package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.condition.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.SingleAppSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberSingleAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.AppComponentSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.ComponentType
import tornaco.apps.shortx.ui.addrule.fact.rememberAppComponentSelectorState
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

    return { source: ActionSelectSource ->
        when (source.clazz) {
            Action.StartActivityIntent::class -> {
                startActivityIntentSelectorState.show(source.getActionAs())
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

            Action.StartAppProcess::class -> {
                startAppProcessSelectorState.show(
                    source.getActionAs<Action.StopApp>()?.apps,
                    source.getActionAs<Action.StopApp>()?.pkgSets
                )
                true
            }

            Action.EnableApp::class -> {
                enableAppProcessSelectorState.show(
                    source.getActionAs<Action.StopApp>()?.apps,
                    source.getActionAs<Action.StopApp>()?.pkgSets
                )
                true
            }

            Action.DisableApp::class -> {
                disableAppProcessSelectorState.show(
                    source.getActionAs<Action.StopApp>()?.apps,
                    source.getActionAs<Action.StopApp>()?.pkgSets
                )
                true
            }

            Action.SuspendApp::class -> {
                suspendAppProcessSelectorState.show(
                    source.getActionAs<Action.StopApp>()?.apps,
                    source.getActionAs<Action.StopApp>()?.pkgSets
                )
                true
            }

            Action.UnSuspendApp::class -> {
                unsuspendAppProcessSelectorState.show(
                    source.getActionAs<Action.StopApp>()?.apps,
                    source.getActionAs<Action.StopApp>()?.pkgSets
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
        Action.LaunchApp::class,
        Action.StopApp::class,
        Action.StartAppProcess::class,
        Action.EnableApp::class,
        Action.DisableApp::class,
        Action.SuspendApp::class,
        Action.UnSuspendApp::class,
    ).map {
        Item(it, labelAndDescriptionForActionSelector(i18N, it), imageVectorForAction(it))
    }

    val selectHandler = appActionSelectHandler(addAction = addAction)

    ActionList(
        actionItems = actionItems,
        itemSelected = { it, _ ->
            selectHandler(ActionSelectSource.FromClass(it))
        }
    )
}
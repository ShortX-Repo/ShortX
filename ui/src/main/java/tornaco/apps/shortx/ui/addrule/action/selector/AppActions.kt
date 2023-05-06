package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.condition.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import kotlin.reflect.KClass


@Composable
fun appActionSelectHandler(addAction: (Action) -> Unit): (KClass<out Action>) -> Boolean {
    val launchAppSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val first = it[0]
                val action = Action.LaunchApp(appPkg = first.let { app ->

                    AppPkg.newBuilder().setUserId(app.pkg.userId).setPkgName(app.pkg.pkgName)
                        .build()
                }, appLabel = first.label)
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

    return { it ->
        when (it) {
            Action.LaunchApp::class -> {
                launchAppSelectorState.show()
                true
            }
            Action.StopApp::class -> {
                stopAppSelectorState.show()
                true
            }
            Action.StartAppProcess::class -> {
                startAppProcessSelectorState.show()
                true
            }
            Action.EnableApp::class -> {
                enableAppProcessSelectorState.show()
                true
            }
            Action.DisableApp::class -> {
                disableAppProcessSelectorState.show()
                true
            }
            Action.SuspendApp::class -> {
                suspendAppProcessSelectorState.show()
                true
            }
            Action.UnSuspendApp::class -> {
                unsuspendAppProcessSelectorState.show()
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
            selectHandler(it)
        }
    )
}
package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.condition.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N

@Composable
fun AppActions(addAction: (Action) -> Unit) {
    val vm = hiltViewModel<ActionSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.loadInstalledApps()
    }
    val state = vm.state.collectAsState()
    val installedApps = state.value.installedApps

    val launchAppSelectorState =
        rememberAppPkgSelectorState(apps = installedApps, onAppListSelected = {
            val first = it[0]
            val action = Action.LaunchApp(appPkg = first.let { app ->
                AppPkg.newBuilder().setUserId(app.pkg.userId).setPkgName(app.pkg.pkgName).build()
            }, appLabel = first.label)
            addAction(action)
        })
    AppPkgSelectorDialog(state = launchAppSelectorState)

    val stopAppSelectorState =
        rememberAppPkgSelectorState(apps = installedApps, onAppListSelected = {
            val action = Action.StopApp(apps = it)
            addAction(action)
        }, onPkgSetSelected = {
            val action = Action.StopApp(pkgSets = it)
            addAction(action)
        })
    AppPkgSelectorDialog(state = stopAppSelectorState)

    val startAppProcessSelectorState =
        rememberAppPkgSelectorState(apps = installedApps, onAppListSelected = {
            val action = Action.StartAppProcess(apps = it)
            addAction(action)
        }, onPkgSetSelected = {
            val action = Action.StartAppProcess(pkgSets = it)
            addAction(action)
        })
    AppPkgSelectorDialog(state = startAppProcessSelectorState)

    val enableAppProcessSelectorState =
        rememberAppPkgSelectorState(apps = installedApps, onAppListSelected = {
            val action = Action.EnableApp(apps = it)
            addAction(action)
        }, onPkgSetSelected = {
            val action = Action.EnableApp(pkgSets = it)
            addAction(action)
        })
    AppPkgSelectorDialog(state = enableAppProcessSelectorState)

    val disableAppProcessSelectorState =
        rememberAppPkgSelectorState(apps = installedApps, onAppListSelected = {
            val action = Action.DisableApp(apps = it)
            addAction(action)
        }, onPkgSetSelected = {
            val action = Action.DisableApp(pkgSets = it)
            addAction(action)
        })
    AppPkgSelectorDialog(state = disableAppProcessSelectorState)

    val suspendAppProcessSelectorState =
        rememberAppPkgSelectorState(apps = installedApps, onAppListSelected = {
            val action = Action.SuspendApp(apps = it)
            addAction(action)
        }, onPkgSetSelected = {
            val action = Action.SuspendApp(pkgSets = it)
            addAction(action)
        })
    AppPkgSelectorDialog(state = suspendAppProcessSelectorState)

    val unsuspendAppProcessSelectorState =
        rememberAppPkgSelectorState(apps = installedApps, onAppListSelected = {
            val action = Action.UnSuspendApp(apps = it)
            addAction(action)
        }, onPkgSetSelected = {
            val action = Action.UnSuspendApp(pkgSets = it)
            addAction(action)
        })
    AppPkgSelectorDialog(state = unsuspendAppProcessSelectorState)

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

    ActionList(
        actionItems = actionItems,
        itemSelected = { it, _ ->
            when (it) {
                Action.LaunchApp::class -> {
                    launchAppSelectorState.show()
                }
                Action.StopApp::class -> {
                    stopAppSelectorState.show()
                }
                Action.StartAppProcess::class -> {
                    startAppProcessSelectorState.show()
                }
                Action.EnableApp::class -> {
                    enableAppProcessSelectorState.show()
                }
                Action.DisableApp::class -> {
                    disableAppProcessSelectorState.show()
                }
                Action.SuspendApp::class -> {
                    suspendAppProcessSelectorState.show()
                }
                Action.UnSuspendApp::class -> {
                    unsuspendAppProcessSelectorState.show()
                }
            }
        })
}
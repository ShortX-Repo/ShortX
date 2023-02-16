package tornaco.apps.shortx.ui.addrule.action

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.ui.addrule.condition.AppPkgSelector
import tornaco.apps.shortx.ui.addrule.condition.AppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.models.Action
import tornaco.apps.shortx.ui.base.*
import kotlin.reflect.KClass

@Composable
fun ActionSelectors(
    header: @Composable ColumnScope.() -> Unit = {
        MediumTitle(LocalI18N.current["ui.actions.title"])
    },
    addAction: (Action) -> Unit,
    removeAction: (Action) -> Unit,
    actions: List<Action>,
    installedApps: List<App>,
) {
    // Actions
    val shellCommandSelectorState = rememberShellCommandSelectorState { cmd ->
        addAction(Action.ShellCommand(cmd))
    }
    ShellCommandSelector(state = shellCommandSelectorState)

    val toastSelectionState =
        rememberTextInputState(title = LocalI18N.current["ui.action.show.toast"],
            message = null,
            onSelected = {
                addAction(Action.ShowToast(it))
            })
    TextInputDialog(state = toastSelectionState)

    val delaySelectionState =
        rememberTextInputState(title = LocalI18N.current["ui.action.delay"],
            message = LocalI18N.current["ui.action.delay.input.message"],
            onSelected = {
                addAction(Action.Delay(it.toLongOrNull() ?: 0))
            })
    TextInputDialog(state = delaySelectionState)

    val mvelSelectorState = rememberMVELSelectorState(
        title = LocalI18N.current["ui.title.mvel"]
    ) { expression ->
        addAction(Action.MVEL(expression))
    }
    MVELSelector(state = mvelSelectorState)

    val showOverlayButtonSelectorState =
        rememberShowOverlayButtonSelectorState(
            installedApps = installedApps
        ) { settings ->
            val action = Action.ShowOverlay(
                autoCollapse = settings.autoCollapse,
                settings = settings.items.map { it.toOverlayButtonSetting() }
            )
            addAction(action)
        }
    ShowOverlayButtonSelector(state = showOverlayButtonSelectorState)

    val launchAppSelectorState =
        rememberAppPkgSelectorState(apps = installedApps, onAppListSelected = {
            val first = it[0]
            val action = Action.LaunchApp(first.let { app ->
                AppPkg.newBuilder().setUserId(app.pkg.userId).setPkgName(app.pkg.pkgName).build()
            }, appLabel = first.label)
            addAction(action)
        })
    AppPkgSelector(state = launchAppSelectorState)

    val stopAppSelectorState =
        rememberAppPkgSelectorState(apps = installedApps, onAppListSelected = {
            val action = Action.StopApp(it)
            addAction(action)
        }, onPkgSetSelected = {
            val action = Action.StopApp(pkgSets = it)
            addAction(action)
        })
    AppPkgSelector(state = stopAppSelectorState)

    val startAppProcessSelectorState =
        rememberAppPkgSelectorState(apps = installedApps, onAppListSelected = {
            val action = Action.StartAppProcess(it)
            addAction(action)
        }, onPkgSetSelected = {
            val action = Action.StartAppProcess(pkgSets = it)
            addAction(action)
        })
    AppPkgSelector(state = startAppProcessSelectorState)

    val enableAppProcessSelectorState =
        rememberAppPkgSelectorState(apps = installedApps, onAppListSelected = {
            val action = Action.EnableApp(it)
            addAction(action)
        }, onPkgSetSelected = {
            val action = Action.EnableApp(pkgSets = it)
            addAction(action)
        })
    AppPkgSelector(state = enableAppProcessSelectorState)

    val disableAppProcessSelectorState =
        rememberAppPkgSelectorState(apps = installedApps, onAppListSelected = {
            val action = Action.DisableApp(it)
            addAction(action)
        }, onPkgSetSelected = {
            val action = Action.DisableApp(pkgSets = it)
            addAction(action)
        })
    AppPkgSelector(state = disableAppProcessSelectorState)

    val suspendAppProcessSelectorState =
        rememberAppPkgSelectorState(apps = installedApps, onAppListSelected = {
            val action = Action.SuspendApp(it)
            addAction(action)
        }, onPkgSetSelected = {
            val action = Action.SuspendApp(pkgSets = it)
            addAction(action)
        })
    AppPkgSelector(state = suspendAppProcessSelectorState)

    val unsuspendAppProcessSelectorState =
        rememberAppPkgSelectorState(apps = installedApps, onAppListSelected = {
            val action = Action.UnsuspendApp(it)
            addAction(action)
        }, onPkgSetSelected = {
            val action = Action.UnsuspendApp(pkgSets = it)
            addAction(action)
        })
    AppPkgSelector(state = unsuspendAppProcessSelectorState)

    Actions(
        header = header,
        actions = actions,
        selectAction = { action: KClass<out Action> ->
            onActionMenuSelected(
                action = action,
                addAction = addAction,
                shellCommandSelectorState = shellCommandSelectorState,
                toastSelectionState = toastSelectionState,
                delaySelectionState = delaySelectionState,
                mvelSelectorState = mvelSelectorState,
                showOverlayButtonSelectorState = showOverlayButtonSelectorState,
                launchAppSelectorState = launchAppSelectorState,
                stopAppSelectorState = stopAppSelectorState,
                startAppProcessSelectorState = startAppProcessSelectorState,
                enableAppProcessSelectorState = enableAppProcessSelectorState,
                disableAppProcessSelectorState = disableAppProcessSelectorState,
                suspendAppProcessSelectorState = suspendAppProcessSelectorState,
                unsuspendAppProcessSelectorState = unsuspendAppProcessSelectorState,
            )
        },
        removeAction = { action ->
            removeAction(action)
        },
        onActionClick = { action ->
            when (action) {
                is Action.ShowOverlay -> {
                    showOverlayButtonSelectorState.show(
                        ShowOverlayButtonSettings(
                            action.autoCollapse,
                            action.settings.map { it.toOverlayButtonItem() })
                    )
                }
                else -> {}
            }
        })
}

private fun onActionMenuSelected(
    action: KClass<out Action>,
    addAction: (Action) -> Unit,
    shellCommandSelectorState: ShellCommandSelectorState,
    toastSelectionState: TextInputState,
    delaySelectionState: TextInputState,
    mvelSelectorState: MVELSelectorState,
    showOverlayButtonSelectorState: ShowOverlayButtonSelectorState,
    launchAppSelectorState: AppPkgSelectorState,
    stopAppSelectorState: AppPkgSelectorState,
    startAppProcessSelectorState: AppPkgSelectorState,
    enableAppProcessSelectorState: AppPkgSelectorState,
    disableAppProcessSelectorState: AppPkgSelectorState,
    suspendAppProcessSelectorState: AppPkgSelectorState,
    unsuspendAppProcessSelectorState: AppPkgSelectorState,
) {
    when (action) {
        Action.TakeScreenshot::class -> {
            addAction(Action.TakeScreenshot)
        }
        Action.ShellCommand::class -> {
            shellCommandSelectorState.show()
        }
        Action.ShowToast::class -> {
            toastSelectionState.show()
        }
        Action.Delay::class -> {
            delaySelectionState.show()
        }
        Action.MVEL::class -> {
            mvelSelectorState.show()
        }
        Action.HideOverlay::class -> {
            addAction(Action.HideOverlay)
        }
        Action.ShowOverlay::class -> {
            showOverlayButtonSelectorState.show(
                ShowOverlayButtonSettings(
                    false,
                    emptyList()
                )
            )
        }
        Action.LaunchApp::class -> {
            launchAppSelectorState.show()
        }

        Action.DisableBT::class,
        Action.DisableDND::class,
        Action.DisableDarkMode::class,
        Action.DisableData::class,
        Action.DisableFlashLight::class,
        Action.DisableHotSpot::class,
        Action.DisableNFC::class,
        Action.DisableWifi::class,

        Action.EnableBT::class,
        Action.EnableDND::class,
        Action.EnableDarkMode::class,
        Action.EnableData::class,
        Action.EnableFlashLight::class,
        Action.EnableHotSpot::class,
        Action.EnableNFC::class,
        Action.EnableWifi::class -> {
            action.objectInstance?.let {
                addAction(it)
            }
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
        Action.UnsuspendApp::class -> {
            unsuspendAppProcessSelectorState.show()
        }
    }
}


@Composable
private fun Actions(
    header: @Composable ColumnScope.() -> Unit,
    actions: List<Action>,
    selectAction: (KClass<out Action>) -> Unit,
    removeAction: (Action) -> Unit,
    onActionClick: (Action) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        header()
        Column(modifier = Modifier.fillMaxWidth()) {
            actions.forEach {
                ListItem(
                    titleLines = labelLinesForAction(action = it),
                    icon = {
                        Icon(imageVector = imageVectorForAction(action = it::class))
                    },
                    action1 = {
                        IconButton(onClick = {
                            removeAction(it)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close, contentDescription = "Remove"
                            )
                        }
                    },
                    onClick = { onActionClick(it) })
            }

            Box {
                var showActionSelectorMenu by remember {
                    mutableStateOf(false)
                }
                FilledTonalButton(onClick = {
                    showActionSelectorMenu = true
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add, contentDescription = "Select"
                    )
                    TinySpacer()
                    Text(LocalI18N.current["ui.actions.add"])
                }

                ActionSelectorMenu(isOpen = showActionSelectorMenu,
                    setIsOpen = { showActionSelectorMenu = it }) {
                    showActionSelectorMenu = false
                    selectAction(it)
                }
            }
        }
    }
}
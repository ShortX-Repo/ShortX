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
import tornaco.apps.shortx.ui.addrule.condition.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.models.Action
import tornaco.apps.shortx.ui.base.*

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

    Actions(
        header = header,
        actions = actions,
        selectAction = { action: Class<out Action> ->
            when (action) {
                Action.TakeScreenshot::class.java -> {
                    addAction(Action.TakeScreenshot)
                }
                Action.ShellCommand::class.java -> {
                    shellCommandSelectorState.show()
                }
                Action.ShowToast::class.java -> {
                    toastSelectionState.show()
                }
                Action.Delay::class.java -> {
                    delaySelectionState.show()
                }
                Action.MVEL::class.java -> {
                    mvelSelectorState.show()
                }
                Action.HideOverlay::class.java -> {
                    addAction(Action.HideOverlay)
                }
                Action.ShowOverlay::class.java -> {
                    showOverlayButtonSelectorState.show(
                        ShowOverlayButtonSettings(
                            false,
                            emptyList()
                        )
                    )
                }
                Action.LaunchApp::class.java -> {
                    launchAppSelectorState.show()
                }
            }
        },
        removeAction = { action ->
            removeAction(action)
        },
        onActionClick = { action ->
            when (action) {
                is Action.TakeScreenshot -> {
                }
                is Action.ShellCommand -> {
                }
                is Action.ShowToast -> {
                }
                is Action.Delay -> {
                }
                is Action.MVEL -> {
                }
                Action.HideOverlay -> {
                }
                is Action.ShowOverlay -> {
                    showOverlayButtonSelectorState.show(
                        ShowOverlayButtonSettings(
                            action.autoCollapse,
                            action.settings.map { it.toOverlayButtonItem() })
                    )
                }
                is Action.LaunchApp -> {}
            }
        })
}


@Composable
private fun Actions(
    header: @Composable ColumnScope.() -> Unit,
    actions: List<Action>,
    selectAction: (Class<out Action>) -> Unit,
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
                        IconForAction(action = it::class.java)
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
package tornaco.apps.shortx.ui.addrule.action

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.ui.addrule.models.Action
import tornaco.apps.shortx.ui.base.*


@Composable
fun ActionSelectors(
    header: @Composable ColumnScope.() -> Unit = {
        MediumTitle(LocalI18N.current["ui.actions.title"])
    },
    addAction: (Action) -> Unit,
    removeAction: (Action) -> Unit,
    selectableActions: Map<I18NKey, List<Action>>,
    actions: List<Action>
) {
    // Actions
    val shellCommandSelectorState = rememberShellCommandSelectorState { cmd ->
        addAction(Action.ShellCommand(cmd))
    }
    ShellCommandSelector(state = shellCommandSelectorState)

    val mvelSelectorState = rememberMVELSelectorState { expression ->
        addAction(Action.MVEL(expression))
    }
    MVELSelector(state = mvelSelectorState)

    val showOverlayButtonSelectorState =
        rememberShowOverlayButtonSelectorState(
            selectableActions = selectableActions
        ) { settings ->
            val action = Action.ShowOverlay(
                autoCollapse = settings.autoCollapse,
                settings = settings.items.map { it.toOverlayButtonSetting() }
            )
            addAction(action)
        }
    ShowOverlayButtonSelector(state = showOverlayButtonSelectorState)

    Actions(
        header = header,
        selectableActions = selectableActions,
        actions = actions,
        selectAction = { action: Action ->
            when (action) {
                is Action.TakeScreenshot -> {
                    addAction(action)
                }
                is Action.ShellCommand -> {
                    shellCommandSelectorState.show()
                }
                is Action.ShowToast -> {
                    addAction(Action.ShowToast("Hello"))
                }
                is Action.Delay -> {
                    addAction(Action.Delay(1000))
                }
                is Action.MVEL -> {
                    mvelSelectorState.show()
                }
                Action.HideOverlay -> {
                    addAction(Action.HideOverlay)
                }
                is Action.ShowOverlay -> {
                    showOverlayButtonSelectorState.show(
                        ShowOverlayButtonSettings(
                            false,
                            emptyList()
                        )
                    )
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
            }
        })
}


@Composable
private fun Actions(
    header: @Composable ColumnScope.() -> Unit,
    selectableActions: Map<I18NKey, List<Action>>,
    actions: List<Action>,
    selectAction: (Action) -> Unit,
    removeAction: (Action) -> Unit,
    onActionClick: (Action) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        header()
        val selectorState = rememberDropdownSelectorState()

        Column(modifier = Modifier.fillMaxWidth()) {
            actions.forEach {
                ListItem(
                    titleLines = labelLinesForAction(action = it),
                    icon = {
                        IconForAction(action = it)
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

                TextButton(onClick = {
                    selectorState.open()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add, contentDescription = "Select"
                    )
                    TinySpacer()
                    Text(LocalI18N.current["ui.actions.add"])
                }

                val actionItems = selectableActions.mapValues {
                    it.value.map { action ->
                        DropdownItem(
                            labelLines = labelForActionSelector(action),
                            data = action,
                            icon = {
                                IconForAction(action = action)
                            })
                    }
                }
                DropdownSelector(state = selectorState, items = actionItems) {
                    selectAction(it.data)
                }
            }
        }
    }
}

@Composable
private fun labelForActionSelector(action: Action): List<String> {
    return when (action) {
        is Action.ShowToast -> {
            listOf("Show Toast")
        }
        is Action.ShellCommand -> {
            listOf("Shell command")
        }
        is Action.Delay -> {
            listOf("Delay")
        }
        Action.TakeScreenshot -> {
            listOf("Take Screenshot")
        }
        is Action.MVEL -> {
            listOf("MVEL expression")
        }
        Action.HideOverlay -> {
            listOf("HideOverlay")
        }
        is Action.ShowOverlay -> {
            listOf("ShowOverlay")
        }
    }
}

@Composable
private fun labelLinesForAction(action: Action): List<String> {
    return when (action) {
        is Action.ShowToast -> {
            listOf("Show Toast", action.message)
        }
        is Action.ShellCommand -> {
            listOf("Shell command", action.command)
        }
        is Action.Delay -> {
            listOf("Delay", action.time.toString())
        }
        Action.TakeScreenshot -> {
            listOf("Take Screenshot")
        }
        is Action.MVEL -> {
            listOf("MVEL expression", action.expression)
        }
        Action.HideOverlay -> {
            listOf("HideOverlay")
        }
        is Action.ShowOverlay -> {
            mutableListOf("ShowOverlay").apply {
                add("${action.settings.size} buttons")
            }
        }
    }
}

@Composable
private fun IconForAction(action: Action) {
    when (action) {
        is Action.ShowToast -> {
            Icon(
                imageVector = Icons.Filled.Message, contentDescription = "Toast"
            )
        }
        is Action.ShellCommand -> {
            Icon(
                imageVector = Icons.Filled.Tag, contentDescription = "Shell"
            )
        }
        is Action.Delay -> {
            Icon(
                imageVector = Icons.Filled.Timer, contentDescription = "Delay"
            )
        }
        Action.TakeScreenshot -> {
            Icon(
                imageVector = Icons.Filled.Devices, contentDescription = "Take Screenshot"
            )
        }
        is Action.MVEL -> {
            Icon(
                imageVector = Icons.Filled.Code, contentDescription = "MVEL"
            )
        }
        Action.HideOverlay -> {
            Icon(
                imageVector = Icons.Filled.Window, contentDescription = "HideOverlay"
            )
        }
        is Action.ShowOverlay -> {
            Icon(
                imageVector = Icons.Filled.Window, contentDescription = "ShowOverlay"
            )
        }
    }
}
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme

enum class ActionCategory(val i18NKey: String, val icon: ImageVector) {
    Ui("UI", Icons.Outlined.Preview),
    Apps("ui.action.category.app", Icons.Outlined.Apps),
    Settings("ui.action.system.settings", Icons.Outlined.ToggleOn),
    Advanced("ui.action.advanced", Icons.Outlined.Extension),
    Remote("ui.action.remote", Icons.Outlined.Cloud)
}

class ActionSelectorDialogState(val onSelected: (Action) -> Unit) {
    var isShowing by mutableStateOf(false)

    fun show() {
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberActionSelectorDialogState(onSelected: (Action) -> Unit): ActionSelectorDialogState {
    return remember {
        ActionSelectorDialogState(onSelected)
    }
}

@Composable
fun ActionSelectorDialog(state: ActionSelectorDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ActionSelector(state)
        }
    }
}

@Composable
private fun ActionSelector(
    state: ActionSelectorDialogState
) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.title.select.action"]) },
                        actions = {
                            IconButton(onClick = {
                                state.dismiss()
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                    )
                }) { paddingValues ->

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var selectedItem by remember { mutableStateOf(ActionCategory.Ui.ordinal) }
                    val items = ActionCategory.values().map { it.i18NKey }
                    val icons = ActionCategory.values().map { it.icon }

                    NavigationRail(
                        modifier = Modifier
                            .fillMaxHeight()
                            .shadow(4.dp)
                    ) {
                        items.forEachIndexed { index, item ->
                            NavigationRailItem(
                                icon = { Icon(imageVector = icons[index]) },
                                label = { Text(LocalI18N.current[item]) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index }
                            )
                        }
                    }

                    StandardSpacer()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        val addAction: (Action) -> Unit = {
                            state.onSelected(it)
                            state.dismiss()
                        }

                        when (selectedItem) {
                            ActionCategory.Ui.ordinal -> {
                                UiActions(addAction = addAction)
                            }
                            ActionCategory.Apps.ordinal -> {
                                AppActions(addAction = addAction)
                            }
                            ActionCategory.Settings.ordinal -> {
                                SettingsActions(addAction = addAction)
                            }
                            ActionCategory.Advanced.ordinal -> {
                                ExtActions(addAction = addAction)
                            }
                            ActionCategory.Remote.ordinal -> {
                                RemoteActions(addAction = addAction)
                            }
                            else -> {}
                        }
                    }
                }


            }
        }
    }
}
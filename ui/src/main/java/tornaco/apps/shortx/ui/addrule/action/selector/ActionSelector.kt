@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme

enum class ActionCategory(val i18NKey: String, val icon: String) {
    Ui("UI", Remix.Design.layout_2_line),
    Apps("ui.action.category.app", Remix.System.apps_2_line),
    Settings("ui.action.system.settings", Remix.System.settings_2_line),
    Life("ui.action.life", Remix.Map.riding_line),
    Advanced("ui.action.advanced", Remix.System.function_line),
    Remote("ui.action.remote", Remix.Business.cloud_line)
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
                                icon = { RemixIcon(remixName = icons[index]) },
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
                            ActionCategory.Life.ordinal -> {
                                LifeActions(addAction = addAction)
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
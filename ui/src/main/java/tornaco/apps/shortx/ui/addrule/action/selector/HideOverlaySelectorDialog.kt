@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme


class HideOverlayDialogState(
    val onSelected: (Action.HideOverlay) -> Unit
) {
    var isShowing by mutableStateOf(false)

    fun show() {
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberHideOverlayDialogState(
    onSelected: (Action.HideOverlay) -> Unit
): HideOverlayDialogState {
    return remember {
        HideOverlayDialogState(onSelected)
    }
}

@Composable
fun HideOverlayDialog(state: HideOverlayDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            HideOverlaySelector(state)
        }
    }
}

@Composable
private fun HideOverlaySelector(dialogState: HideOverlayDialogState) {
    var tags by remember {
        mutableStateOf("")
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.action.show.overlay"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (tags.isNotBlank()) {
                                    dialogState.onSelected(
                                        Action.HideOverlay(
                                            tags = kotlin.runCatching {
                                                tags.split(",").toList().map { it.trim() }
                                            }.getOrElse {
                                                listOf(tags)
                                            }
                                        )
                                    )
                                    dialogState.dismiss()
                                }
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) { paddingValues ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    DialogMessage(text = "Tags to hide")
                    OutlinedTextField(value = tags, onValueChange = {
                        tags = it
                    })
                }
            }
        }
    }

}
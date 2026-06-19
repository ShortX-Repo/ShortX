@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.DialogMessage
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

private const val SPLITTER = ","

class HideOverlayDialogState(
    val onSelected: (Action.HideOverlay) -> Unit
) {
    var tags by mutableStateOf("")
    var isShowing by mutableStateOf(false)

    fun show(currentAction: Action.HideOverlay? = null) {
        this.tags = currentAction?.tags?.joinToString(SPLITTER) ?: ""
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
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current[I18N_R.UI_ACTION_HIDE_OVERLAY]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (dialogState.tags.isNotBlank()) {
                                    dialogState.onSelected(
                                        Action.HideOverlay(
                                            tags = kotlin.runCatching {
                                                dialogState.tags.split(SPLITTER).toList()
                                                    .map { it.trim() }
                                            }.getOrElse {
                                                listOf(dialogState.tags)
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
                    ShortXTextField(value = dialogState.tags, onValueChange = {
                        dialogState.tags = it
                    })
                }
            }
        }
    }

}
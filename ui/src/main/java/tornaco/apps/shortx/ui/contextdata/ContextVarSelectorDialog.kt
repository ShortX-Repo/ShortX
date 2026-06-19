@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.contextdata

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.context.allKeys
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.UiClipboard
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

class ContextVarSelectorState(
    val onContextVarSelected: (String) -> Unit
) : CommonDialogState()

@Composable
fun rememberContextVarSelectorState(
    onContextVarSelected: (String) -> Unit
): ContextVarSelectorState {
    return remember {
        ContextVarSelectorState(onContextVarSelected)
    }
}

@Composable
fun ContextVarSelectorDialog(state: ContextVarSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ContextVarSelector(state)
        }
    }
}

@Composable
private fun ContextVarSelector(selectorState: ContextVarSelectorState) {
    val context = LocalContext.current
    val i18N = LocalI18N.current
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = i18N[I18N_R.UI_CONTEXT_DATA_TITLE]) },
                        actions = {
                            IconButton(onClick = {
                                UiClipboard.copy(
                                    UiClipboard.ClipboardContent.ActionContent(
                                        Action.ShowDanmu(
                                            text = allKeys.joinToString(separator = "\n") { key ->
                                                "$key={$key}"
                                            },
                                            icon = null,
                                        )
                                    )
                                )
                                Toast.makeText(
                                    context,
                                    i18N[I18N_R.UI_COMMON_COPIED_TO_CLIPBOARD],
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                                RemixIcon(remixName = Remix.Document.clipboard_fill)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                selectorState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        }
                    )
                }) { paddingValues ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = paddingValues
                ) {
                    items(allKeys) {
                        ListItem(title = it, onClick = {
                            selectorState.onContextVarSelected(it)
                            selectorState.dismiss()
                        })
                    }
                }
            }
        }
    }
}

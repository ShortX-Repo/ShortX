package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.core.util.UserHandleUtils
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.theme.ShortXTheme


class PinedItemSelectorState(
    val onSelected: (Action.LaunchPinedItem.PinedItem) -> Unit
) : CommonDialogState()

@Composable
fun rememberPinedItemSelectorState(
    onSelected: (Action.LaunchPinedItem.PinedItem) -> Unit
): PinedItemSelectorState {
    return remember {
        PinedItemSelectorState(
            onSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinedItemSelectorDialog(state: PinedItemSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXTheme {
                Scaffold(modifier = Modifier,
                    topBar = {
                        ShortXTopAppBar(
                            modifier = Modifier,
                            title = { Text(text = LocalI18N.current["ui.action.launch.pined.item"]) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    state.dismiss()
                                }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            }
                        )
                    }) { paddingValues ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        val items = shortXManager.getRequestPinShortcuts().mapNotNull {
                            val intent = it.intent
                            if (intent != null) {
                                Action.LaunchPinedItem.PinedItem(
                                    label = (it.shortLabel ?: it.longLabel
                                    ?: PkgUtils.packageNameOf(it.intent)).toString(),
                                    intent = intent,
                                    userId = UserHandleUtils.userHandleToUserId(it.userHandle)
                                )
                            } else null
                        }

                        item {
                            TipRow(text = LocalI18N.current["ui.action.launch.pined.item.tip"])
                        }

                        items(items) {
                            ListItem(
                                titleLines = listOf(it.label),
                                onClick = {
                                    state.onSelected(it)
                                    state.dismiss()
                                })
                        }
                    }
                }
            }
        }
    }
}

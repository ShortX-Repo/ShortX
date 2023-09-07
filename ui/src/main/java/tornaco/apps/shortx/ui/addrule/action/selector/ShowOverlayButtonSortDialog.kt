@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.theme.ShortXTheme


class ShowOverlayButtonSortState(
    val buttons: () -> List<OverlayButtonSetting>,
    val applySorted: (List<OverlayButtonSetting>) -> Unit
) : CommonDialogState()

@Composable
fun rememberShowOverlayButtonSortState(
    buttons: () -> List<OverlayButtonSetting>,
    apply: (List<OverlayButtonSetting>) -> Unit
): ShowOverlayButtonSortState {
    return remember(buttons) {
        ShowOverlayButtonSortState(buttons, apply)
    }
}

@Composable
fun ShowOverlayButtonSortDialog(state: ShowOverlayButtonSortState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShowOverlayButtonSortDialogContent(state)
        }
    }
}


@Composable
private fun ShowOverlayButtonSortDialogContent(state: ShowOverlayButtonSortState) {
    val data = remember(state) {
        mutableStateOf(state.buttons())
    }
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.common.long.press.sort"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                state.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                state.applySorted(data.value)
                                state.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) { paddingValues ->
                val sortState = rememberReorderableLazyListState(onMove = { from, to ->
                    data.value = data.value.toMutableList().apply {
                        add(to.index, removeAt(from.index))
                    }
                })

                LazyColumn(
                    state = sortState.listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(bottom = 64.dp)
                        .reorderable(sortState)
                        .detectReorderAfterLongPress(sortState)
                ) {
                    items(items = data.value, key = { it.id }) { item ->
                        ReorderableItem(sortState, key = item.id) { isDragging ->
                            val scale = animateFloatAsState(
                                if (isDragging) 1.06f else 1f,
                                label = "ScaleAnim"
                            )
                            ListItem(
                                modifier = Modifier.scale(scale.value),
                                titleLines = listOf(item.label),
                                icon = {
                                    RemixIcon(remixName = item.icon)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
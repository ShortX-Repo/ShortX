@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.mapper.labelLinesForAction
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.theme.ErrorRed
import tornaco.apps.shortx.ui.theme.ShortXTheme


class ActionSortState(val actions: List<Action>, val applySorted: (List<Action>) -> Unit) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    fun show() {
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberActionSortState(
    actions: List<Action>,
    apply: (List<Action>) -> Unit
): ActionSortState {
    return remember(actions) {
        ActionSortState(actions, apply)
    }
}

@Composable
fun ActionSortDialog(state: ActionSortState) {
    if (state.isShow) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ActionSortDialogContent(state)
        }
    }
}


@Composable
private fun ActionSortDialogContent(state: ActionSortState) {
    val data = remember(state) {
        mutableStateOf(state.actions)
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
                    items(data.value, { it }) { item ->
                        ReorderableItem(sortState, key = item) { isDragging ->
                            val scale = animateFloatAsState(
                                if (isDragging) 1.06f else 1f,
                                label = "SacleAnim"
                            )
                            ListItem(
                                modifier = Modifier.scale(scale.value),
                                titleLines = labelLinesForAction(action = item),
                                textDecoration = if (item.isEnabled) TextDecoration.None else TextDecoration.LineThrough,
                                titleColor = if (item.isEnabled) Color.Unspecified else ErrorRed,
                                textColor = if (item.isEnabled) Color.Unspecified else ErrorRed,
                                icon = {
                                    ActionIcon(item, false)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
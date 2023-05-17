@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
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
                            val scale = animateFloatAsState(if (isDragging) 1.06f else 1f)
                            ListItem(
                                modifier = Modifier.scale(scale.value),
                                titleLines = labelLinesForAction(action = item),
                                icon = {
                                    RemixIcon(remixName = imageVectorForAction(action = item::class))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
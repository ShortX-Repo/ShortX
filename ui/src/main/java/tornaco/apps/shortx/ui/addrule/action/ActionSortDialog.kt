package tornaco.apps.shortx.ui.addrule.action

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*


class ActionSortState(val actions: List<Action>, val apply: (List<Action>) -> Unit) {
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
        val data = remember(state) {
            mutableStateOf(state.actions)
        }
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.common.long.press.sort"])
        }, buttons = {
            TextButton(onClick = {

            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            val sortState = rememberReorderableLazyListState(onMove = { from, to ->
                data.value = data.value.toMutableList().apply {
                    add(to.index, removeAt(from.index))
                }
            })

            LazyColumn(
                state = sortState.listState,
                modifier = Modifier
                    .fillMaxHeight(0.42f)
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
                                Icon(imageVector = imageVectorForAction(action = item::class))
                            }
                        )
                    }
                }
            }
        })
    }
}
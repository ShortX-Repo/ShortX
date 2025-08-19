@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base

import android.os.Parcelable
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
import kotlinx.parcelize.Parcelize
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.io.Serializable

@Parcelize
data class SortItem(
    val index: Int,
    val title: String,
    val description: String? = null,
    val icon: String? = null
) : Parcelable, Serializable

class CommonSortState(
    val applySorted: (List<SortItem>) -> Unit
) :
    CommonDialogState()

@Composable
fun rememberCommonSortState(
    apply: (List<SortItem>) -> Unit
): CommonSortState {
    return remember {
        CommonSortState(apply)
    }
}

@Composable
fun CommonSortDialog(
    state: CommonSortState,
    items: List<SortItem>
) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            DialogContent(state, items)
        }
    }
}


@Composable
private fun DialogContent(
    state: CommonSortState,
    menus: List<SortItem>
) {
    val data = remember(menus) {
        mutableStateOf(menus)
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
                                titleLines = listOfNotNull(
                                    item.title,
                                    item.description,
                                    "@${item.index}"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
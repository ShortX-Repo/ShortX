package tornaco.apps.shortx.ui.settings.log


import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.RemixIcon

@Composable
fun LogList(
    horizontalScrollEnabled: Boolean,
    listState: LazyListState,
    state: LogState
) {
    var modifier = Modifier
        .fillMaxSize()
    if (horizontalScrollEnabled) {
        modifier = modifier.horizontalScroll(state = rememberScrollState())
    }
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(16.dp)
    ) {
        items(state.logs) { item ->
            Text(text = item, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun MenuActions(
    clearLogs: () -> Unit,
    scrollToTop: () -> Unit,
    scrollToBottom: () -> Unit,
    toggleHorizontalScroll: () -> Unit
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            clearLogs()
        }) {
            RemixIcon(Remix.System.delete_bin_4_fill)
        }
        IconButton(onClick = {
            toggleHorizontalScroll()
        }) {
            RemixIcon(Remix.Editor.text_wrap)
        }
        IconButton(onClick = {
            scrollToTop()
        }) {
            RemixIcon(Remix.Arrows.arrow_up_fill)
        }
        IconButton(onClick = {
            scrollToBottom()
        }) {
            RemixIcon(Remix.Arrows.arrow_down_fill)
        }
    }
}
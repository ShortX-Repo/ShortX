package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortXMediumAppBarScaffold(
    title: String,
    verticalScroll: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    onBackPressed: (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ShortXMediumTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    onBackPressed?.let {
                        IconButton(onClick = {
                            onBackPressed.invoke()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    }
                },
                actions = actions,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar,
        snackbarHost,
        floatingActionButton,
        floatingActionButtonPosition,
        containerColor,
        contentColor,
        contentWindowInsets
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .consumeWindowInsets(it)
                .thenIf(Modifier.verticalScroll(rememberScrollState()), verticalScroll),
        ) {
            Spacer(modifier = Modifier.size(it.calculateTopPadding()))
            content()
            Spacer(modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.size(it.calculateBottomPadding()))
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ShortXAppBarScaffold(
    title: String,
    verticalScroll: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    onBackPressed: (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ShortXTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    onBackPressed?.let {
                        IconButton(onClick = {
                            onBackPressed.invoke()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    }
                },
                actions = actions,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar,
        snackbarHost,
        floatingActionButton,
        floatingActionButtonPosition,
        containerColor,
        contentColor,
        contentWindowInsets
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .consumeWindowInsets(it)
                .thenIf(Modifier.verticalScroll(rememberScrollState()), verticalScroll),
        ) {
            Spacer(modifier = Modifier.size(it.calculateTopPadding()))
            content()
            Spacer(modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.size(it.calculateBottomPadding()))
        }
    }
}
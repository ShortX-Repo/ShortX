@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.code

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeLibraryQuery
import tornaco.apps.shortx.core.proto.common.CodeType
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.handleSideEffect
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CodeLibraryListActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, CodeLibraryListActivity::class.java))
        }
    }

    @Composable
    override fun Content() {
        CodeLibraryListScreen()
    }
}

@Composable
fun CodeLibraryListScreen() {
    val vm = hiltViewModel<CodeLibraryListViewModel>()
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(vm) {
        vm.loadCodeLibrary()
    }

    vm.handleSideEffect { effect ->
        when (effect) {
            is CodeLibraryEffect.ShowError -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }

            is CodeLibraryEffect.ItemDeleted -> {
                Toast.makeText(context, "代码已删除", Toast.LENGTH_SHORT).show()
            }

            is CodeLibraryEffect.ItemsDeleted -> {
                Toast.makeText(context, "已删除 ${effect.count} 个代码", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 处理返回键
    BackHandler(enabled = state.isSelectionMode) {
        vm.clearSelection()
    }

    ShortXTheme {
        Surface(modifier = Modifier.fillMaxSize()) {

            Scaffold(
                topBar = {
                    CodeLibraryTopBar(
                        state = state,
                        onSearchQueryChange = { query ->
                            vm.updateQuery(
                                CodeLibraryQuery.newBuilder(state.query).setKeyword(query).build()
                            )
                        },
                        onFilterClick = { /* TODO: 实现过滤功能 */ },
                        onDeleteSelected = {
                            vm.deleteSelectedItems()
                        },
                        onClearSelection = {
                            vm.clearSelection()
                        },
                    )
                },
                floatingActionButton = {
                    if (!state.isSelectionMode) {
                        CreateCodeFab(
                            onCreateCode = { type ->
                                CodeLibraryEditorActivity.start(context, null, type)
                            }
                        )
                    }
                }
            ) { paddingValues ->
                CodeLibraryContent(
                    modifier = Modifier.padding(paddingValues),
                    state = state,
                    onItemClick = { item ->
                        if (state.isSelectionMode) {
                            vm.toggleSelection(item.id)
                        } else {
                            CodeLibraryEditorActivity.start(context, item)
                        }
                    },
                    onItemLongClick = { item ->
                        vm.toggleSelection(item.id)
                    },
                    onDeleteItem = { item ->
                        vm.deleteItem(item.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun CodeLibraryTopBar(
    state: CodeLibraryListState,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onDeleteSelected: () -> Unit,
    onClearSelection: () -> Unit,
) {
    var showSearch by remember { mutableStateOf(false) }

    ShortXTopAppBar(
        title = {
            if (showSearch) {
                OutlinedTextField(
                    value = state.query.keyword,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("搜索代码...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            showSearch = false
                            onSearchQueryChange("")
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "清除搜索")
                        }
                    }
                )
            } else {
                Text(
                    text = if (state.isSelectionMode) {
                        "已选择 ${state.selectedItems.size} 项"
                    } else {
                        "代码库"
                    }
                )
            }
        },
        navigationIcon = {
            if (state.isSelectionMode) {
                IconButton(onClick = onClearSelection) {
                    Icon(Icons.Default.Clear, contentDescription = "取消选择")
                }
            }
        },
        actions = {
            if (state.isSelectionMode) {
                IconButton(onClick = onDeleteSelected) {
                    Icon(Icons.Default.Delete, contentDescription = "删除选中项")
                }
            } else {
                if (!showSearch) {
                    IconButton(onClick = { showSearch = true }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                    IconButton(onClick = onFilterClick) {
                        Icon(Icons.Default.FilterList, contentDescription = "过滤")
                    }
                }
            }
        }
    )
}

@Composable
private fun CreateCodeFab(
    onCreateCode: (CodeType) -> Unit
) {
    val menuState = rememberMenuDialogState<CodeType>(
        title = "选择代码类型",
        menuItems = listOf(
            MenuDialogItem(
                id = CodeType.CodeType_MVEL.name,
                title = "MVEL",
                iconName = Remix.Development.code_line
            ),
            MenuDialogItem(
                id = CodeType.CodeType_JAVASCRIPT.name,
                title = "JavaScript",
                iconName = Remix.Logos.vuejs_line
            )
        )
    ) { _, data ->
        onCreateCode(CodeType.valueOf(data))
    }

    MenuDialog(state = menuState)

    FloatingActionButton(
        onClick = { menuState.show() }
    ) {
        Icon(Icons.Default.Add, contentDescription = "添加代码")
    }
}

@Composable
private fun CodeLibraryContent(
    modifier: Modifier = Modifier,
    state: CodeLibraryListState,
    onItemClick: (CodeLibraryItem) -> Unit,
    onItemLongClick: (CodeLibraryItem) -> Unit,
    onDeleteItem: (CodeLibraryItem) -> Unit
) {
    when (state.loadState) {
        is tornaco.apps.shortx.ui.base.LoadState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("加载中...")
            }
        }

        is tornaco.apps.shortx.ui.base.LoadState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("加载失败: ${state.loadState.err?.message}")
            }
        }

        is tornaco.apps.shortx.ui.base.LoadState.Success -> {
            if (state.items.isEmpty()) {
                EmptyCodeLibrary(modifier = modifier)
            } else {
                CodeLibraryList(
                    modifier = modifier,
                    items = state.items,
                    selectedItems = state.selectedItems,
                    isSelectionMode = state.isSelectionMode,
                    onItemClick = onItemClick,
                    onItemLongClick = onItemLongClick,
                    onDeleteItem = onDeleteItem
                )
            }
        }
    }
}

@Composable
private fun EmptyCodeLibrary(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Code,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "暂无代码片段",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "点击右下角的 + 按钮添加代码",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CodeLibraryList(
    modifier: Modifier = Modifier,
    items: List<CodeLibraryItem>,
    selectedItems: Set<String>,
    isSelectionMode: Boolean,
    onItemClick: (CodeLibraryItem) -> Unit,
    onItemLongClick: (CodeLibraryItem) -> Unit,
    onDeleteItem: (CodeLibraryItem) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { item ->
            CodeLibraryItemCard(
                item = item,
                isSelected = item.id in selectedItems,
                isSelectionMode = isSelectionMode,
                onClick = { onItemClick(item) },
                onLongClick = { onItemLongClick(item) },
                onDelete = { onDeleteItem(item) },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CodeLibraryItemCard(
    item: CodeLibraryItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deleteDialogState = rememberConfirmDialogState()

    ConfirmDialog(
        title = "删除代码",
        state = deleteDialogState,
        data = item,
        messageHint = { "确定要删除代码 \"${item.name}\" 吗？" },
        onConfirm = { onDelete() }
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() }
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            // 代码类型图标
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (item.type) {
                            CodeType.CodeType_MVEL -> MaterialTheme.colorScheme.primaryContainer
                            CodeType.CodeType_JAVASCRIPT -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                RemixIcon(
                    remixName = when (item.type) {
                        CodeType.CodeType_MVEL -> Remix.Development.code_line
                        CodeType.CodeType_JAVASCRIPT -> Remix.Logos.vuejs_line
                        else -> Remix.Development.code_line
                    },
                    modifier = Modifier.size(20.dp),
                    tint = when (item.type) {
                        CodeType.CodeType_MVEL -> MaterialTheme.colorScheme.onPrimaryContainer
                        CodeType.CodeType_JAVASCRIPT -> MaterialTheme.colorScheme.onSecondaryContainer
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (item.description.isNotBlank()) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                StandardSpacer()

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (item.type) {
                            CodeType.CodeType_MVEL -> "MVEL"
                            CodeType.CodeType_JAVASCRIPT -> "JavaScript"
                            else -> item.type.name
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = formatDate(item.updatedAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!isSelectionMode) {
                IconButton(onClick = { deleteDialogState.show() }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
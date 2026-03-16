@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.code

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.code.toByteArrayWrapper
import tornaco.apps.shortx.core.code.toCodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeType
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DisposableEffectWithLifeCycle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CodeLibSelectorActivity : BaseSelectorActivity<ByteArrayWrapper>() {

    companion object {
        private const val EXTRA_CODE_TYPE = "extra_code_type"

        fun newIntent(
            context: Context,
            currentItem: CodeLibraryItem? = null,
            codeType: CodeType? = null
        ): Intent {
            return BaseSelectorActivity.newIntent(
                context,
                currentItem?.toByteArrayWrapper(),
                CodeLibSelectorActivity::class.java
            ).apply {
                codeType?.let { putExtra(EXTRA_CODE_TYPE, it.name) }
            }
        }
    }

    @Composable
    override fun BaseSelectorActivity<ByteArrayWrapper>.SelectorContent(currentData: ByteArrayWrapper?) {
        val codeType = intent.getStringExtra(EXTRA_CODE_TYPE)?.let { CodeType.valueOf(it) }
        CodeLibSelector(currentData = currentData, codeType = codeType)
    }
}

fun ActivityResult.popupCodeLibraryItemResult(): CodeLibraryItem? {
    return popupSelectorResult<ByteArrayWrapper>()?.let {
        runCatching { it.toCodeLibraryItem() }.getOrNull()
    }
}

private class CodeLibDialogState(
    val onSelected: (CodeLibraryItem) -> Unit
) : CommonDialogState()

@Composable
private fun rememberCodeLibDialogState(
    onSelected: (CodeLibraryItem) -> Unit
): CodeLibDialogState {
    return remember {
        CodeLibDialogState(onSelected)
    }
}

@Composable
private fun BaseSelectorActivity<ByteArrayWrapper>.CodeLibSelector(
    currentData: ByteArrayWrapper?,
    codeType: CodeType?
) {
    val vm = hiltViewModel<CodeLibSelectorViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm, currentData, codeType) {
        vm.initFrom(currentData, codeType)
        vm.loadItems()
    }

    DisposableEffectWithLifeCycle(
        onResume = {
            vm.loadItems()
        }
    )

    val dialogState = rememberCodeLibDialogState(
        onSelected = {
            vm.selectItem(it)
        }
    )

    CodeLibSelectorDialog(
        state = dialogState,
        selectorState = state,
        onKeywordChange = vm::updateKeyword,
        onReload = vm::loadItems
    )

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    ShortXTopAppBar(
                        title = {
                            Text(
                                if (codeType == null) {
                                    "选择代码"
                                } else {
                                    "选择${labelForCodeType(codeType)}代码"
                                }
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { cancelAndFinish() }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    state.selectedItem?.let {
                                        setResultAndFinish(it.toByteArrayWrapper())
                                    }
                                },
                                enabled = state.selectedItem != null
                            ) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues)
                        .animateContentSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center
                ) {
                    StandardSpacer()

                    state.selectedItem?.let { selectedItem ->
                        ListItem(
                            titleLines = buildCodeItemLines(selectedItem),
                            icon = {
                                ColoredIcon(size = 36.dp) {
                                    RemixIcon(remixName = iconForCodeType(selectedItem.type))
                                }
                            }
                        )
                        LargeSpacer()
                    }

                    Button(onClick = { dialogState.show() }) {
                        Text(
                            text = if (state.selectedItem == null) {
                                if (codeType == null) "选择代码" else "选择${labelForCodeType(codeType)}代码"
                            } else {
                                "重新选择代码"
                            }
                        )
                    }

                    if (state.selectedItem == null) {
                        LargeSpacer()
                        Text(
                            text = if (codeType == null) {
                                "请选择一个代码库条目"
                            } else {
                                "请选择一个${labelForCodeType(codeType)}代码条目"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeLibSelectorDialog(
    state: CodeLibDialogState,
    selectorState: CodeLibSelectorState,
    onKeywordChange: (String) -> Unit,
    onReload: () -> Unit
) {
    if (!state.isShowing) {
        return
    }

    ShortFullScreenXDialog(
        onDismissRequest = {
            state.dismiss()
        }
    ) {
        ShortXTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Scaffold(
                    topBar = {
                        ShortXTopAppBar(
                            title = { Text("选择代码") },
                            navigationIcon = {
                                IconButton(onClick = { state.dismiss() }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .padding(paddingValues)
                    ) {
                        OutlinedTextField(
                            value = selectorState.keyword,
                            onValueChange = onKeywordChange,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("搜索代码...") },
                            trailingIcon = {
                                if (selectorState.keyword.isNotBlank()) {
                                    IconButton(onClick = { onKeywordChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "清除搜索"
                                        )
                                    }
                                }
                            }
                        )

                        StandardSpacer()

                        when (selectorState.loadState) {
                            LoadState.Loading -> {
                                Loading(modifier = Modifier.fillMaxSize())
                            }

                            is LoadState.Error -> {
                                LoadError(
                                    modifier = Modifier.fillMaxSize(),
                                    message = selectorState.loadState.err?.message,
                                    refresh = onReload
                                )
                            }

                            is LoadState.Success -> {
                                if (selectorState.items.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (selectorState.keyword.isBlank()) {
                                                selectorState.filterType?.let {
                                                    "暂无${labelForCodeType(it)}代码"
                                                } ?: "暂无代码"
                                            } else {
                                                "未找到匹配的代码"
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(
                                            items = selectorState.items,
                                            key = { it.id }
                                        ) { item ->
                                            CodeLibSelectableItem(
                                                item = item,
                                                isSelected = selectorState.selectedItem?.id == item.id,
                                                onClick = {
                                                    state.onSelected(item)
                                                    state.dismiss()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeLibSelectableItem(
    item: CodeLibraryItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        titleLines = buildCodeItemLines(item),
        icon = {
            ColoredIcon(size = 36.dp) {
                RemixIcon(remixName = iconForCodeType(item.type))
            }
        },
        action1 = {
            if (isSelected) {
                OutlineBadge(text = "已选")
            }
        },
        onClick = onClick
    )
}

private fun buildCodeItemLines(item: CodeLibraryItem): List<String> {
    val lines = mutableListOf<String>()
    lines += item.name

    item.description
        .takeIf { it.isNotBlank() }
        ?.let {
            lines += it
        } ?: contentPreview(item.content)?.let {
        lines += it
    }

    lines += "${labelForCodeType(item.type)} • ${formatCodeItemTime(item.updatedAt)}"

    return lines
}

private fun contentPreview(content: String): String? {
    val firstNonBlankLine = content.lineSequence().firstOrNull { it.isNotBlank() }?.trim()
    return firstNonBlankLine?.let {
        if (it.length > 80) "${it.take(77)}..." else it
    }
}

private fun iconForCodeType(type: CodeType): String {
    return when (type) {
        CodeType.CodeType_MVEL -> Remix.Development.code_line
        CodeType.CodeType_JAVASCRIPT -> Remix.Logos.vuejs_line
        else -> Remix.Development.code_line
    }
}

private fun labelForCodeType(type: CodeType): String {
    return when (type) {
        CodeType.CodeType_MVEL -> "MVEL"
        CodeType.CodeType_JAVASCRIPT -> "JavaScript"
        else -> type.name
    }
}

private fun formatCodeItemTime(timestamp: Long): String {
    val formatter = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
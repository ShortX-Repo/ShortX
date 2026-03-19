@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.code

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeType
import tornaco.apps.shortx.core.proto.common.CoroutineContext
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.newIntent
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.TextDisplayActivity
import tornaco.apps.shortx.ui.base.code.ParcelableCode
import tornaco.apps.shortx.ui.base.code.js.JSEditorActivity
import tornaco.apps.shortx.ui.base.code.mvel.MVELEditorActivity
import tornaco.apps.shortx.ui.base.handleSideEffect
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class CodeLibraryEditorActivity : BaseActivity() {

    companion object {
        private const val EXTRA_CODE_ITEM = "extra_code_item"
        private const val EXTRA_CODE_TYPE = "extra_code_type"

        fun start(
            context: Context,
            item: CodeLibraryItem? = null,
            type: CodeType? = null
        ) {
            val intent = Intent(context, CodeLibraryEditorActivity::class.java).apply {
                item?.let { putExtra(EXTRA_CODE_ITEM, it.toByteArray()) }
                type?.let { putExtra(EXTRA_CODE_TYPE, it.name) }
            }
            context.startActivity(intent)
        }
    }

    @Composable
    override fun Content() {
        val itemBytes = intent.getByteArrayExtra(EXTRA_CODE_ITEM)
        val item = itemBytes?.let { CodeLibraryItem.parseFrom(it) }
        val typeString = intent.getStringExtra(EXTRA_CODE_TYPE)
        val type = typeString?.let { CodeType.valueOf(it) }

        CodeLibraryEditorScreen(
            initialItem = item,
            initialType = type,
        ) {
            finish()
        }
    }
}

@Composable
fun CodeLibraryEditorScreen(
    initialItem: CodeLibraryItem? = null,
    importItem: CodeLibraryItem? = null,
    initialType: CodeType? = null,
    url: String? = null,
    screenTitle: String? = null,
    saveIconName: String? = null,
    closeAfterSave: Boolean = false,
    onBack: () -> Unit
) {
    val vm = hiltViewModel<CodeLibraryEditorViewModel>()
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    val codeEditorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            result.popupSelectorResult<ParcelableCode>()?.let {
                vm.updateContent(it.expression)
            }
        }
    )

    var showUnsavedChangesDialog by remember { mutableStateOf(false) }

    LaunchedEffect(vm, initialItem, importItem, initialType, url) {
        when {
            url != null -> vm.initFromUrl(url)
            importItem != null -> vm.initForImportItem(importItem)
            initialItem != null -> vm.initWithItem(initialItem)
            initialType != null -> vm.createNewItem(initialType)
        }
    }

    vm.handleSideEffect { sideEffect ->
        when (sideEffect) {
            is CodeLibraryEditorSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is CodeLibraryEditorSideEffect.ShowExecutionResult -> {
                if (sideEffect.result.isNotBlank()) {
                    TextDisplayActivity.start(context, "执行结果", sideEffect.result)
                } else {
                    Toast.makeText(context, "执行完成", Toast.LENGTH_SHORT).show()
                }
            }

            is CodeLibraryEditorSideEffect.NavigateBack -> {
                onBack()
            }
        }
    }

    // 处理返回键
    BackHandler(enabled = state.hasUnsavedChanges) {
        showUnsavedChangesDialog = true
    }

    // 未保存更改确认对话框
    val unsavedChangesDialogState = rememberConfirmDialogState()

    ConfirmDialog(
        title = "未保存的更改",
        state = unsavedChangesDialogState,
        data = Unit,
        messageHint = { "您有未保存的更改，确定要离开吗？" },
        onConfirm = {
            onBack()
        }
    )

    if (showUnsavedChangesDialog) {
        LaunchedEffect(Unit) {
            unsavedChangesDialogState.show()
            showUnsavedChangesDialog = false
        }
    }

    val launchCodeEditor = {
        val currentCode = ParcelableCode(
            expression = state.content,
            coroutineContext = CoroutineContext.CoroutineContext_Default
        )
        when (state.type) {
            CodeType.CodeType_MVEL -> {
                codeEditorLauncher.launch(
                    newIntent(context, currentCode, MVELEditorActivity::class.java)
                )
            }

            CodeType.CodeType_JAVASCRIPT -> {
                codeEditorLauncher.launch(
                    newIntent(context, currentCode, JSEditorActivity::class.java)
                )
            }

            else -> {
                Toast.makeText(context, "暂不支持该代码类型", Toast.LENGTH_SHORT).show()
            }
        }
    }

    ShortXTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    CodeLibraryEditorTopBar(
                        state = state,
                        screenTitle = screenTitle,
                        saveIconName = saveIconName,
                        onSave = {
                            vm.save()
                            if (closeAfterSave) {
                                onBack()
                            }
                        },
                        onExecute = { vm.execute() },
                        onBack = {
                            if (state.hasUnsavedChanges) {
                                showUnsavedChangesDialog = true
                            } else {
                                onBack()
                            }
                        }
                    )
                }
            ) { paddingValues ->
                when (state.onlineCodeLoadState) {
                    is LoadState.Error -> {
                        LoadError(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            vm.initFromUrl(url)
                        }
                    }

                    LoadState.Loading -> {
                        Loading(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        )
                    }

                    is LoadState.Success -> {
                        CodeLibraryEditorContent(
                            modifier = Modifier.padding(paddingValues),
                            state = state,
                            onNameChange = { vm.updateName(it) },
                            onDescriptionChange = { vm.updateDescription(it) },
                            onEditCode = launchCodeEditor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeLibraryEditorTopBar(
    state: CodeLibraryEditorState,
    screenTitle: String?,
    saveIconName: String?,
    onSave: () -> Unit,
    onExecute: () -> Unit,
    onBack: () -> Unit
) {
    ShortXTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = screenTitle ?: if (state.isNewItem) "新建代码" else "编辑代码",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                RemixIcon(remixName = Remix.System.close_line)
            }
        },
        actions = {
            // 执行按钮
            if (!state.isExecuting && state.content.isNotBlank()) {
            }

            if (state.isExecuting) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Loading(modifier = Modifier.size(28.dp))
                }
            }

            // 保存按钮
            IconButton(
                onClick = onSave,
                enabled = state.canSave
            ) {
                if (saveIconName == null) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "保存",
                        tint = if (state.canSave) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                } else {
                    RemixIcon(
                        remixName = saveIconName,
                        tint = if (state.canSave) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun CodeLibraryEditorContent(
    modifier: Modifier = Modifier,
    state: CodeLibraryEditorState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onEditCode: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 基本信息卡片
        ShortXCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "基本信息",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                ShortXTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = { Text("代码名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = state.nameError != null
                )

                state.nameError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                ShortXTextField(
                    value = state.description,
                    onValueChange = onDescriptionChange,
                    label = { Text("描述（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "代码内容",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            AssistChip(
                label = { Text(if (state.content.isBlank()) "编辑代码" else "编辑") },
                onClick = onEditCode
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ShortXCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            if (state.content.isBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无代码，点击“编辑代码”开始编写",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                SelectionContainer {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = state.content,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        state.contentError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp, start = 4.dp)
            )
        }
    }
}
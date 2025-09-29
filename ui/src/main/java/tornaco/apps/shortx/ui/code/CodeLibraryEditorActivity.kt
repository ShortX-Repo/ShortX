@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.code

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.rosemoe.sora.app.CEComposeBinding
import io.github.rosemoe.sora.app.databinding.CodeEditorComposeBinding
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeType
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextDisplayActivity
import tornaco.apps.shortx.ui.base.handleSideEffect
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
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

        CodeLibraryEditorScreen(item, type)
    }
}

@Composable
fun CodeLibraryEditorScreen(
    initialItem: CodeLibraryItem? = null,
    initialType: CodeType? = null
) {
    val vm = hiltViewModel<CodeLibraryEditorViewModel>()
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    var showUnsavedChangesDialog by remember { mutableStateOf(false) }

    LaunchedEffect(vm) {
        if (initialItem != null) {
            vm.initWithItem(initialItem)
        } else if (initialType != null) {
            vm.createNewItem(initialType)
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
                (context as? CodeLibraryEditorActivity)?.finish()
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
            (context as? CodeLibraryEditorActivity)?.finish()
        }
    )

    if (showUnsavedChangesDialog) {
        LaunchedEffect(Unit) {
            unsavedChangesDialogState.show()
            showUnsavedChangesDialog = false
        }
    }

    ShortXTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    CodeLibraryEditorTopBar(
                        state = state,
                        onSave = { vm.save() },
                        onExecute = { vm.execute() },
                        onTypeChange = { type -> vm.updateType(type) },
                        onBack = {
                            if (state.hasUnsavedChanges) {
                                showUnsavedChangesDialog = true
                            } else {
                                (context as? CodeLibraryEditorActivity)?.finish()
                            }
                        }
                    )
                }
            ) { paddingValues ->
                CodeLibraryEditorContent(
                    modifier = Modifier.padding(paddingValues),
                    state = state,
                    onNameChange = { vm.updateName(it) },
                    onDescriptionChange = { vm.updateDescription(it) },
                    onContentChange = { vm.updateContent(it) },
                    onTagsChange = { vm.updateTags(it) }
                )
            }
        }
    }
}

@Composable
private fun CodeLibraryEditorTopBar(
    state: CodeLibraryEditorState,
    onSave: () -> Unit,
    onExecute: () -> Unit,
    onTypeChange: (CodeType) -> Unit,
    onBack: () -> Unit
) {
    ShortXTopAppBar(
        title = {
            val typeMenuState = rememberMenuDialogState<CodeType>(
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
                onTypeChange(CodeType.valueOf(data))
            }

            MenuDialog(state = typeMenuState)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (state.isNewItem) "新建代码" else "编辑代码",
                    style = MaterialTheme.typography.titleLarge
                )
                StandardSpacer()
                AssistChip(
                    label = { Text(state.type.name) },
                    onClick = { typeMenuState.show() }
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
                IconButton(onClick = onExecute) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = "执行代码"
                    )
                }
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
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "保存",
                    tint = if (state.canSave) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
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
    onContentChange: (String) -> Unit,
    onTagsChange: (List<String>) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 基本信息卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

                OutlinedTextField(
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

                OutlinedTextField(
                    value = state.description,
                    onValueChange = onDescriptionChange,
                    label = { Text("描述（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.tags.joinToString(", "),
                    onValueChange = { tagsString ->
                        val tags = tagsString.split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                        onTagsChange(tags)
                    },
                    label = { Text("标签（用逗号分隔）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("例如：工具, 自动化, 测试") }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 代码编辑器
        Text(
            text = "代码内容",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 使用AndroidViewBinding实现代码编辑器
        val subscribeState by LVLStateHolder.collectAsState()

        AndroidViewBinding(
            factory = { inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean ->
                CodeEditorComposeBinding.inflate(inflater, parent, attachToParent)
                    .apply {
                        CEComposeBinding(
                            binding = this,
                            showToolbar = subscribeState.isSubscribed,
                            initialText = state.content
                        ) { newContent ->
                            composeLogger.logOnDebug("Code content change: $newContent")
                            onContentChange(newContent)
                        }.onCreate()
                    }
            },
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            onRelease = {
                CEComposeBinding(
                    binding = this,
                    showToolbar = subscribeState.isSubscribed,
                    initialText = ""
                ).onDestroy()
            }
        )
    }
}
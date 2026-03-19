package tornaco.apps.shortx.ui.code

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.code.CodeLibraryHelper
import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeType
import tornaco.apps.shortx.core.rule.parseShareContentCodeLibraryItem
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.ShortXContextViewModelWithEffect
import java.net.URL
import javax.inject.Inject

data class CodeLibraryEditorState(
    val item: CodeLibraryItem? = null,
    val isNewItem: Boolean = true,
    val hasUnsavedChanges: Boolean = false,
    val isSaving: Boolean = false,
    val isExecuting: Boolean = false,
    val onlineCodeLoadState: LoadState<Unit> = LoadState.Success(Unit),
    val name: String = "",
    val description: String = "",
    val content: String = "",
    val type: CodeType = CodeType.CodeType_MVEL,
    val tags: List<String> = emptyList(),
    val nameError: String? = null,
    val contentError: String? = null,
    val canSave: Boolean = false
)

sealed interface CodeLibraryEditorSideEffect {
    data class ShowToast(val message: String) : CodeLibraryEditorSideEffect
    data class ShowExecutionResult(val result: String) : CodeLibraryEditorSideEffect
    object NavigateBack : CodeLibraryEditorSideEffect
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class CodeLibraryEditorViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ShortXContextViewModelWithEffect<CodeLibraryEditorState, CodeLibraryEditorSideEffect>(
    context, { CodeLibraryEditorState() }
) {

    private val shortX by lazy { shortXManager }

    fun initWithItem(item: CodeLibraryItem) {
        updateState {
            copy(
                item = item,
                isNewItem = false,
                name = item.name,
                description = item.description,
                content = item.content,
                type = item.type,
                tags = item.tagsList,
                hasUnsavedChanges = false,
                onlineCodeLoadState = LoadState.Success(Unit)
            )
        }
        validateAndUpdateCanSave()
    }

    fun initForImportItem(item: CodeLibraryItem) {
        updateState {
            copy(
                item = item,
                isNewItem = true,
                name = item.name,
                description = item.description,
                content = item.content,
                type = item.type,
                tags = item.tagsList,
                hasUnsavedChanges = false,
                onlineCodeLoadState = LoadState.Success(Unit)
            )
        }
        validateAndUpdateCanSave()
    }

    fun createNewItem(type: CodeType) {
        updateState {
            copy(
                item = null,
                isNewItem = true,
                name = "",
                description = "",
                content = "",
                type = type,
                tags = emptyList(),
                hasUnsavedChanges = false,
                onlineCodeLoadState = LoadState.Success(Unit)
            )
        }
        validateAndUpdateCanSave()
    }

    fun initFromUrl(url: String?) {
        url?.let {
            viewModelScope.launch {
                updateState { copy(onlineCodeLoadState = LoadState.Loading) }
                withContext(Dispatchers.IO) {
                    runCatching {
                        val content = URL(url).readText()
                        val item = requireNotNull(parseShareContentCodeLibraryItem(content)) {
                            "Fail to parse Code library item:\n$content"
                        }
                        updateState { copy(onlineCodeLoadState = LoadState.Success(Unit)) }
                        initForImportItem(item)
                    }.onFailure {
                        logger.e(it, "initFromUrl")
                        updateState { copy(onlineCodeLoadState = LoadState.Error(it)) }
                    }
                }
            }
        }
    }

    fun updateName(name: String) {
        updateState {
            copy(
                name = name,
                hasUnsavedChanges = true
            )
        }
        validateAndUpdateCanSave()
    }

    fun updateDescription(description: String) {
        updateState {
            copy(
                description = description,
                hasUnsavedChanges = true
            )
        }
        validateAndUpdateCanSave()
    }

    fun updateContent(content: String) {
        updateState {
            copy(
                content = content,
                hasUnsavedChanges = true
            )
        }
        validateAndUpdateCanSave()
    }

    fun updateType(type: CodeType) {
        updateState {
            copy(
                type = type,
                hasUnsavedChanges = true
            )
        }
        validateAndUpdateCanSave()
    }

    fun updateTags(tags: List<String>) {
        updateState {
            copy(
                tags = tags,
                hasUnsavedChanges = true
            )
        }
        validateAndUpdateCanSave()
    }

    fun save() {
        val currentState = state.value
        if (!currentState.canSave) {
            return
        }

        updateState { copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val itemToSave = if (currentState.item != null) {
                    currentState.item.toBuilder()
                        .setName(currentState.name)
                        .setDescription(currentState.description)
                        .setContent(currentState.content)
                        .setType(currentState.type)
                        .clearTags()
                        .addAllTags(currentState.tags)
                        .setUpdatedAt(System.currentTimeMillis())
                        .build()
                } else {
                    CodeLibraryHelper.createCodeLibraryItem(
                        id = generateId(),
                        name = currentState.name,
                        description = currentState.description,
                        content = currentState.content,
                        type = currentState.type,
                        tags = currentState.tags,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                }

                if (currentState.isNewItem) {
                    shortX.addCodeLibraryItem(itemToSave)
                } else {
                    shortX.updateCodeLibraryItem(itemToSave)
                }

                updateState {
                    copy(
                        item = itemToSave,
                        hasUnsavedChanges = false,
                        isSaving = false,
                        isNewItem = false
                    )
                }

                postSideEffect(CodeLibraryEditorSideEffect.ShowToast("保存成功"))

            } catch (e: Exception) {
                updateState { copy(isSaving = false) }
                postSideEffect(CodeLibraryEditorSideEffect.ShowToast("保存失败: ${e.message}"))
            }
        }
    }

    fun execute() {
        val currentState = state.value
        if (currentState.content.isBlank()) {
            postSideEffect(CodeLibraryEditorSideEffect.ShowToast("代码内容不能为空"))
            return
        }

        updateState { copy(isExecuting = true) }

        viewModelScope.launch {
            try {
                val result = when (currentState.type) {
                    CodeType.CodeType_MVEL -> {
                        // TODO: 调用 shortX.executeMVEL(currentState.content, context)
                        "MVEL execution result: ${currentState.content}"
                    }

                    CodeType.CodeType_JAVASCRIPT -> {
                        // TODO: 调用 shortX.executeJS(currentState.content, context)
                        "JS execution result: ${currentState.content}"
                    }

                    else -> {
                        "Unknown code type: ${currentState.type}"
                    }
                }

                postSideEffect(CodeLibraryEditorSideEffect.ShowExecutionResult(result))

            } catch (e: Exception) {
                postSideEffect(CodeLibraryEditorSideEffect.ShowToast("执行失败: ${e.message}"))
            } finally {
                updateState { copy(isExecuting = false) }
            }
        }
    }

    private fun validateAndUpdateCanSave() {
        val currentState = state.value
        val nameError = if (currentState.name.isBlank()) "名称不能为空" else null
        val contentError = if (currentState.content.isBlank()) "代码内容不能为空" else null
        val canSave = nameError == null && contentError == null && !currentState.isSaving

        updateState {
            copy(
                nameError = nameError,
                contentError = contentError,
                canSave = canSave
            )
        }
    }

    private fun generateId(): String {
        return "code_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}
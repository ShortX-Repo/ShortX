package tornaco.apps.shortx.ui.code

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.code.CodeLibraryHelper
import tornaco.apps.shortx.core.proto.toJson
import tornaco.apps.shortx.core.proto.toProto
import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeLibraryQuery
import tornaco.apps.shortx.core.proto.common.CodeLibrarySortBy
import tornaco.apps.shortx.core.proto.common.CodeType
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.ShortXContextViewModelWithEffect
import tornaco.apps.shortx.ui.share.addShareContentHeader
import tornaco.apps.shortx.ui.share.getContentData
import tornaco.apps.shortx.ui.share.getContentHeader
import java.nio.charset.Charset
import javax.inject.Inject

data class CodeLibraryListState(
    val items: List<CodeLibraryItem> = emptyList(),
    val loadState: LoadState<List<CodeLibraryItem>> = LoadState.Loading,
    val query: CodeLibraryQuery = CodeLibraryHelper.createCodeLibraryQuery(),
    val selectedItems: Set<String> = emptySet(),
    val isSelectionMode: Boolean = false
)

sealed interface CodeLibraryEffect {
    data class ShowError(val message: String) : CodeLibraryEffect
    data class ItemDeleted(val id: String) : CodeLibraryEffect
    data class ItemsDeleted(val count: Int) : CodeLibraryEffect
    data class ItemImported(val name: String) : CodeLibraryEffect
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class CodeLibraryListViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModelWithEffect<CodeLibraryListState, CodeLibraryEffect>(
        context,
        { CodeLibraryListState() }
    ) {

    fun loadCodeLibrary() {
        updateState { copy(loadState = LoadState.Loading) }

        viewModelScope.launch {
            try {
                val itemWrappers = shortXManager.getAllCodeLibraryItems()
                val items = itemWrappers
                logger.d("getAllCodeLibraryItems- ${itemWrappers.size}")
                updateState {
                    copy(
                        items = items,
                        loadState = LoadState.Success(items)
                    )
                }
            } catch (e: Exception) {
                updateState { copy(loadState = LoadState.Error(e)) }
                postSideEffect(CodeLibraryEffect.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    fun updateQuery(query: CodeLibraryQuery) {

    }

    fun codeLibraryItemToShareString(item: CodeLibraryItem): String? {
        return shortXManager.getCodeLibraryItemById(item.id)?.let {
            CodeLibraryItem.newBuilder(it)
                .setId("SHARE-${System.currentTimeMillis()}")
                .build()
        }?.toJson()?.addShareContentHeader(mapOf("type" to "CodeLibraryItem"))
    }

    fun importFromUri(uri: Uri) {
        viewModelScope.launch {
            runCatching {
                kotlinx.coroutines.withContext(Dispatchers.IO) {
                    val text = context.contentResolver.openInputStream(uri)?.use {
                        it.bufferedReader(Charset.defaultCharset()).readText()
                    } ?: error("文件内容为空")

                    val importedItem = text.toImportCodeLibraryItem()
                    shortXManager.addCodeLibraryItem(importedItem)
                    importedItem
                }
            }.onSuccess {
                loadCodeLibrary()
                postSideEffect(CodeLibraryEffect.ItemImported(it.name))
            }.onFailure {
                logger.e(it, "importFromUri")
                postSideEffect(CodeLibraryEffect.ShowError(it.message ?: "导入失败"))
            }
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            try {
                shortXManager.deleteCodeLibraryItem(id)
                updateState {
                    copy(
                        items = items.filter { it.id != id },
                        selectedItems = selectedItems - id
                    )
                }
                postSideEffect(CodeLibraryEffect.ItemDeleted(id))
            } catch (e: Exception) {
                postSideEffect(CodeLibraryEffect.ShowError(e.message ?: "Delete failed"))
            }
        }
    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            try {
                val selectedIds = state.value.selectedItems
                selectedIds.forEach { id ->
                    shortXManager.deleteCodeLibraryItem(id)
                }
                updateState {
                    copy(
                        items = items.filter { it.id !in selectedIds },
                        selectedItems = emptySet(),
                        isSelectionMode = false
                    )
                }
                postSideEffect(CodeLibraryEffect.ItemsDeleted(selectedIds.size))
            } catch (e: Exception) {
                postSideEffect(CodeLibraryEffect.ShowError(e.message ?: "Delete failed"))
            }
        }
    }

    fun toggleSelection(id: String) {
        updateState {
            val newSelectedItems = if (id in selectedItems) {
                selectedItems - id
            } else {
                selectedItems + id
            }

            copy(
                selectedItems = newSelectedItems,
                isSelectionMode = newSelectedItems.isNotEmpty()
            )
        }
    }

    fun clearSelection() {
        updateState {
            copy(
                selectedItems = emptySet(),
                isSelectionMode = false
            )
        }
    }

    private fun String.toImportCodeLibraryItem(): CodeLibraryItem {
        val content = when (val header = getContentHeader()) {
            emptyMap<String, String>() -> trim()
            else -> {
                require(header["type"] == "CodeLibraryItem") { "不支持的分享文件类型" }
                getContentData().trim()
            }
        }

        require(content.isNotBlank()) { "文件内容为空" }

        val externalItem = CodeLibraryItem.parseFrom(
            CodeLibraryItem.newBuilder().toProto(content).toByteArray()
        )
        val existingNames = shortXManager.getAllCodeLibraryItems().map { it.name }.toSet()
        var checkedName = externalItem.name.ifBlank { "导入的代码" }
        while (checkedName in existingNames) {
            checkedName += "(2)"
        }

        val now = System.currentTimeMillis()
        return CodeLibraryItem.newBuilder(externalItem)
            .setId(generateId())
            .setName(checkedName)
            .setCreatedAt(now)
            .setUpdatedAt(now)
            .build()
    }

    private fun generateId(): String {
        return "code_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}
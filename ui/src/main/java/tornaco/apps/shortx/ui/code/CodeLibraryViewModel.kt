package tornaco.apps.shortx.ui.code

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.code.CodeLibraryHelper
import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeLibraryQuery
import tornaco.apps.shortx.core.proto.common.CodeLibrarySortBy
import tornaco.apps.shortx.core.proto.common.CodeType
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.ShortXContextViewModelWithEffect
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
                updateState {
                    copy(
                        items = filterItems(items, query),
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
        updateState {
            copy(
                query = query,
                items = filterItems(items, query)
            )
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

    private fun filterItems(
        items: List<CodeLibraryItem>,
        query: CodeLibraryQuery
    ): List<CodeLibraryItem> {
        var filtered = items

        // 按关键词过滤
        if (query.keyword.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(query.keyword, ignoreCase = true) ||
                        it.description.contains(query.keyword, ignoreCase = true) ||
                        it.content.contains(query.keyword, ignoreCase = true)
            }
        }

        // 按类型过滤
        if (query.type != CodeType.UNRECOGNIZED) {
            filtered = filtered.filter { it.type == query.type }
        }

        // 按标签过滤
        if (query.tagsList.isNotEmpty()) {
            filtered = filtered.filter { item ->
                query.tagsList.any { tag -> item.tagsList.contains(tag) }
            }
        }

        // 排序
        return when (query.sortBy) {
            CodeLibrarySortBy.CodeLibrarySortBy_NAME_ASC -> filtered.sortedBy { it.name }
            CodeLibrarySortBy.CodeLibrarySortBy_NAME_DESC -> filtered.sortedByDescending { it.name }
            CodeLibrarySortBy.CodeLibrarySortBy_CREATED_ASC -> filtered.sortedBy { it.createdAt }
            CodeLibrarySortBy.CodeLibrarySortBy_CREATED_DESC -> filtered.sortedByDescending { it.createdAt }
            CodeLibrarySortBy.CodeLibrarySortBy_UPDATED_ASC -> filtered.sortedBy { it.updatedAt }
            CodeLibrarySortBy.CodeLibrarySortBy_UPDATED_DESC -> filtered.sortedByDescending { it.updatedAt }
            CodeLibrarySortBy.CodeLibrarySortBy_TYPE_ASC -> filtered.sortedBy { it.type.name }
            CodeLibrarySortBy.CodeLibrarySortBy_TYPE_DESC -> filtered.sortedByDescending { it.type.name }
            else -> filtered
        }
    }

    private fun getMockData(): List<CodeLibraryItem> {
        return listOf(
            CodeLibraryHelper.createCodeLibraryItem(
                id = "1",
                name = "获取当前时间",
                description = "获取当前系统时间的MVEL表达式",
                type = CodeType.CodeType_MVEL,
                content = "new java.util.Date()",
                tags = listOf("时间", "工具")
            ),
            CodeLibraryHelper.createCodeLibraryItem(
                id = "2",
                name = "显示Toast",
                description = "显示Toast消息的JavaScript代码",
                type = CodeType.CodeType_JAVASCRIPT,
                content = "Android.showToast('Hello World');",
                tags = listOf("UI", "消息")
            ),
            CodeLibraryHelper.createCodeLibraryItem(
                id = "3",
                name = "字符串处理",
                description = "常用的字符串处理函数",
                type = CodeType.CodeType_MVEL,
                content = "str.trim().toLowerCase()",
                tags = listOf("字符串", "工具")
            )
        )
    }
}
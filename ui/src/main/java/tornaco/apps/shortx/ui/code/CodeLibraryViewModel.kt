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
}
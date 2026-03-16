package tornaco.apps.shortx.ui.code

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.code.toCodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeType
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class CodeLibSelectorState(
    val selectedItem: CodeLibraryItem? = null,
    val filterType: CodeType? = null,
    val allItems: List<CodeLibraryItem> = emptyList(),
    val items: List<CodeLibraryItem> = emptyList(),
    val keyword: String = "",
    val loadState: LoadState<List<CodeLibraryItem>> = LoadState.Loading
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class CodeLibSelectorViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ShortXContextViewModel<CodeLibSelectorState>(context, { CodeLibSelectorState() }) {

    fun initFrom(currentData: ByteArrayWrapper?, filterType: CodeType? = null) {
        val currentItem = currentData?.let {
            runCatching { it.toCodeLibraryItem() }.getOrNull()
        }

        updateState {
            copy(selectedItem = currentItem, filterType = filterType)
        }
    }

    fun loadItems() {
        updateState {
            copy(loadState = LoadState.Loading)
        }

        viewModelScope.launch {
            try {
                val currentState = state.value
                val allItems = shortXManager.getAllCodeLibraryItems()
                    .sortedByDescending { it.updatedAt }
                val selectedItem = currentState.selectedItem?.id?.let { selectedId ->
                    allItems.firstOrNull { it.id == selectedId }
                }
                val items = filterItems(
                    source = allItems,
                    keyword = currentState.keyword,
                    filterType = currentState.filterType
                )

                updateState {
                    copy(
                        selectedItem = selectedItem,
                        allItems = allItems,
                        items = items,
                        loadState = LoadState.Success(items)
                    )
                }
            } catch (e: Exception) {
                updateState {
                    copy(loadState = LoadState.Error(e))
                }
            }
        }
    }

    fun updateKeyword(keyword: String) {
        val currentState = state.value
        val items = filterItems(
            source = currentState.allItems,
            keyword = keyword,
            filterType = currentState.filterType
        )
        updateState {
            copy(
                keyword = keyword,
                items = items,
                loadState = LoadState.Success(items)
            )
        }
    }

    fun selectItem(item: CodeLibraryItem) {
        updateState {
            copy(selectedItem = item)
        }
    }

    private fun filterItems(
        source: List<CodeLibraryItem>,
        keyword: String,
        filterType: CodeType?
    ): List<CodeLibraryItem> {
        val typeFiltered = filterType?.let { type ->
            source.filter { it.type == type }
        } ?: source

        val trimmedKeyword = keyword.trim()
        if (trimmedKeyword.isBlank()) {
            return typeFiltered
        }

        return typeFiltered.filter { item ->
            item.name.contains(trimmedKeyword, ignoreCase = true) ||
                item.description.contains(trimmedKeyword, ignoreCase = true) ||
                item.content.contains(trimmedKeyword, ignoreCase = true) ||
                item.tagsList.any { tag -> tag.contains(trimmedKeyword, ignoreCase = true) }
        }
    }
}
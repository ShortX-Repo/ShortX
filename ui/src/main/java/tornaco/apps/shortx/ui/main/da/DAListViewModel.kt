package tornaco.apps.shortx.ui.main.da

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.lifecycle.viewModelScope
import com.github.promeg.pinyinhelper.Pinyin
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.main.CommonSetFilter
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.main.model.daToDirectActionUM
import java.text.Collator
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DAListViewModel @Inject constructor(@ApplicationContext context: Context) :
    BaseDAListViewModel(context) {

    @OptIn(FlowPreview::class)
    override val daList: StateFlow<LoadState<DAList>> by lazy {
        state.map {
            it.loadDARequest
        }.combine(state.map { it.searchQuery }) { _, query ->
            runCatching {
                LoadState.Success(getDAList(query))
            }.getOrElse {
                logger.e(it, "load da list")
                LoadState.Error(it)
            }
        }.debounce(300)
            .stateIn(viewModelScope, SharingStarted.Lazily, initialValue = LoadState.Loading)
    }

    private suspend fun getDAList(searchQuery: String): DAList {
        logger.d("getDAList: $searchQuery")
        val gvs = shortXManager.getAllGlobalVars()
        val allDASets = shortXManager.getAllDASets()

        val selectedFilterId = pref.selectedDAFilter.firstOrNull()
        val selectedSetOrNull =
            selectedFilterId?.let { allDASets.firstOrNull { it.id == selectedFilterId } }
        if (selectedSetOrNull == null) {
            // This set is not exists anymore. or it is prebuilt all/flat
            val prebuiltSet = CommonSetFilter.prebuiltOfId(selectedFilterId)
            pref.setSelectedDAFilter(prebuiltSet.filterId)
            updateState { copy(selectedDASetFilter = prebuiltSet) }
        } else {
            updateState {
                copy(
                    selectedDASetFilter = CommonSetFilter.UserSet(
                        id = selectedFilterId,
                        title = selectedSetOrNull.title
                    )
                )
            }
        }

        val selectedDASetFilter = state.value.selectedDASetFilter
        val allDAs = shortXManager.getAllDirectAction().apply {
            logger.i("all da: ${this.joinToString { it.title }}")
        }

        cleanUpInvalidDynamicShortcuts(allDAs)

        val das = allDAs
            .filter { directAction ->
                when (selectedDASetFilter) {
                    CommonSetFilter.PinnedAll -> {
                        allDASets.none { set -> set.id == directAction.directActionSetId }
                    }

                    CommonSetFilter.PinnedAllFlat -> {
                        true
                    }

                    is CommonSetFilter.UserSet -> {
                        directAction.directActionSetId == selectedDASetFilter.id
                    }
                }
            }
            .sortedByDescending { it.createTime }
            .mapNotNull { directAction ->
                runCatching {
                    daToDirectActionUM(directAction, gvs)
                }.getOrElse {
                    logger.e(it, "daToDirectActionUM")
                    null
                }
            }

        val sets = allDASets
            .sortedWith { o1, o2 ->
                Collator.getInstance(Locale.CHINESE).compare(o1.title, o2.title)
            }
            .map { directActionSet ->
                val subDAs = allDAs.filter { it.directActionSetId == directActionSet.id }
                DirectActionUM(
                    id = directActionSet.id,
                    title = directActionSet.title,
                    titlePinyin = Pinyin.toPinyin(directActionSet.title, "").lowercase(),
                    description = directActionSet.description,
                    updateTime = FuzzyDateTimeFormatter.getTimeAgo(
                        context,
                        Date(directActionSet.lastUpdateTime)
                    ),
                    createTime = FuzzyDateTimeFormatter.getTimeAgo(
                        context,
                        Date(directActionSet.createTime)
                    ),
                    icon = directActionSet.icon,
                    iconColor = Color.Red.value,
                    directAction = DirectAction.getDefaultInstance(),
                    versionCode = 0,
                    isDirectActionSet = true,
                    itemsCount = subDAs.size,
                    hasAnyParameter = false
                )
            }

        val daSetFilters = listOf(
            CommonSetFilter.PinnedAll,
            CommonSetFilter.PinnedAllFlat
        ) + sets.map { CommonSetFilter.UserSet(it.id, it.title) }

        val finalDAS = when (selectedDASetFilter) {
            CommonSetFilter.PinnedAll -> {
                // No filter, show set and das.
                sets + das
            }

            CommonSetFilter.PinnedAllFlat -> {
                das
            }

            else -> {
                das
            }
        }.filter {
            searchQuery.isBlank() || it.title.contains(
                searchQuery, true
            ) || it.titlePinyin.contains(searchQuery.lowercase(), true)
        }

        return DAList(
            das = finalDAS,
            daSetFilters = daSetFilters,
        )
    }

    private fun cleanUpInvalidDynamicShortcuts(allDAs: List<DirectAction>) {
        val allDynamicShortcuts = ShortcutManagerCompat.getDynamicShortcuts(context)
        val invalidShortcuts = allDynamicShortcuts.filter { shortcut ->
            allDAs.none { it.id == shortcut.id }
        }
        logger.w("invalidShortcuts: $invalidShortcuts")
        ShortcutManagerCompat.removeDynamicShortcuts(
            context,
            invalidShortcuts.map { it.id })
    }

    fun dismissDASet(daSet: DirectActionUM, token: String?) {
        shortXManager.getAllDirectAction().filter {
            it.directActionSetId == daSet.id
        }.forEach {
            moveOutSet(it, token)
        }
    }

    fun deleteDASet(daSet: DirectActionUM, alsoDeleteItems: Boolean, token: String?) {
        logger.i("deleteDASet: ${daSet.title}")
        if (alsoDeleteItems) {
            shortXManager.getAllDirectAction().filter {
                it.directActionSetId == daSet.id
            }.forEach {
                logger.w("deleteDASet, Delete da: ${it.title}")
                shortXManager.deleteDirectAction(it.id)
            }
        }
        shortXManager.deleteDASet(daSet.id)
    }

    fun rename(daSet: DirectActionUM, newName: String, token: String?) {
        val originalSet = shortXManager.getDASetById(daSet.id)
        originalSet?.let {
            shortXManager.addOrUpdateDASet(
                DirectActionSet.newBuilder(originalSet).setTitle(newName).build()
            )
        }
    }

    fun selectDASetFilter(daSetFilter: CommonSetFilter) {
        updateState { copy(selectedDASetFilter = daSetFilter) }
        viewModelScope.launch {
            pref.setSelectedDAFilter(daSetFilter.filterId)
            requestLoadDAList()
        }
    }
}
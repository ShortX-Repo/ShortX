package tornaco.apps.shortx.ui.main.da

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.ui.base.afterNavLongDelay
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

    override fun loadDAList(delay: Boolean) {
        logger.d("loadDAList")
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            if (delay) afterNavLongDelay()
            withContext(Dispatchers.IO) {
                val activeJobs = shortXManager.getActiveJobs().apply {
                    logger.d("activeJobs: $this")
                }
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
                            daToDirectActionUM(directAction, activeJobs, gvs)
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
                            runningInsCount = 0,
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

                updateState {
                    copy(
                        das = when (selectedDASetFilter) {
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
                        },
                        daSetFilters = daSetFilters,
                        isLoading = false
                    )
                }
            }
        }
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
            loadDAList()
        }
    }
}
package tornaco.apps.shortx.ui.main.da

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.ui.base.afterNavLongDelay
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
                val selectedDASetFilter = state.value.selectedDASetFilter
                val das = shortXManager.getAllDirectAction()
                    .filter {
                        if (selectedDASetFilter == null) {
                            // No filter, show items belong to no set.
                            it.directActionSetId.isNullOrEmpty()
                        } else {
                            it.directActionSetId == selectedDASetFilter.id
                        }
                    }
                    .sortedByDescending { it.createTime }
                    .mapNotNull { directAction ->
                        runCatching {
                            daToDirectActionUM(directAction, activeJobs)
                        }.getOrElse {
                            logger.e(it, "daToDirectActionUM")
                            null
                        }
                    }

                val sets = shortXManager.getAllDASets()
                    .sortedWith { o1, o2 ->
                        Collator.getInstance(Locale.CHINESE).compare(o1.title, o2.title)
                    }
                    .map {
                        DirectActionUM(
                            id = it.id,
                            title = it.title,
                            description = it.description,
                            updateTime = FuzzyDateTimeFormatter.getTimeAgo(
                                context,
                                Date(it.lastUpdateTime)
                            ),
                            createTime = FuzzyDateTimeFormatter.getTimeAgo(
                                context,
                                Date(it.createTime)
                            ),
                            icon = it.icon,
                            iconColor = Color.Red.value,
                            runningInsCount = 0,
                            directAction = DirectAction.getDefaultInstance(),
                            versionCode = 0,
                            isDirectActionSet = true
                        )
                    }

                val daSetFilters = sets.map { DaSetFilter(it.id, it.title) }

                updateState {
                    copy(
                        das = if (selectedDASetFilter == null) {
                            // No filter, show set and das.
                            sets + das
                        } else {
                            das
                        },
                        daSetFilters = daSetFilters,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun dismissDASet(daSet: DirectActionUM, token: String?) {
        shortXManager.getAllDirectAction().filter {
            it.directActionSetId == daSet.id
        }.forEach {
            moveOutSet(it, token)
        }
    }

    fun rename(daSet: DirectActionUM, newName: String, token: String?) {
        val originalSet = shortXManager.getDASetById(daSet.id)
        originalSet?.let {
            shortXManager.addOrUpdateDASet(
                DirectActionSet.newBuilder(originalSet).setTitle(newName).build()
            )
        }
    }

    // null means all.
    fun selectDASetFilter(daSetFilter: DaSetFilter?) {
        updateState { copy(selectedDASetFilter = daSetFilter) }
        loadDAList()
    }
}
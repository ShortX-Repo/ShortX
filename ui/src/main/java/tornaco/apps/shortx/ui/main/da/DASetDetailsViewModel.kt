package tornaco.apps.shortx.ui.main.da

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.ui.main.model.daToDirectActionUM

class DASetDetailsViewModel constructor(
    context: Context,
    private val daSetId: String
) :
    BaseDAListViewModel(context) {
    override fun loadDAList(delay: Boolean) {
        logger.d("loadDASet")
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            withContext(Dispatchers.IO) {
                val activeJobs = shortXManager.getActiveJobs().apply {
                    logger.d("activeJobs: $this")
                }
                val gvs = shortXManager.getAllGlobalVars()
                val das = shortXManager.getAllDirectAction()
                    .filter {
                        it.directActionSetId == daSetId
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
                updateState {
                    copy(das = das, isLoading = false)
                }
            }
        }
    }
}
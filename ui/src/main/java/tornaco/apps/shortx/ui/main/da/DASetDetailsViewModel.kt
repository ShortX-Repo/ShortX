package tornaco.apps.shortx.ui.main.da

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.ui.base.afterNavDelay
import tornaco.apps.shortx.ui.main.model.daToDirectActionUM

class DASetDetailsViewModel(
    context: Context,
    private val daSetId: String
) :
    BaseDAListViewModel(context) {
    override fun loadDAList(delay: Boolean) {
        logger.d("loadDASet")
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            withContext(Dispatchers.IO) {
                val startLoadingTime = System.currentTimeMillis()
                val gvs = shortXManager.getAllGlobalVars()
                val das = shortXManager.getAllDirectAction()
                    .filter {
                        it.directActionSetId == daSetId
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
                if (das.size > 3) {
                    afterNavDelay(System.currentTimeMillis() - startLoadingTime)
                }
                updateState {
                    copy(das = das, isLoading = false)
                }
            }
        }
    }
}
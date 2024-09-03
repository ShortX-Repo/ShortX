package tornaco.apps.shortx.ui.main.da

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.afterNavDelay
import tornaco.apps.shortx.ui.main.model.daToDirectActionUM

class DASetDetailsViewModel(
    context: Context,
    private val daSetId: String
) :
    BaseDAListViewModel(context) {

    @OptIn(FlowPreview::class)
    override val daList: StateFlow<LoadState<DAList>> by lazy {
        loadDARequest.debounce(300).map {
            runCatching {
                LoadState.Success(getDAList())
            }.getOrElse {
                logger.e(it, "load da list")
                LoadState.Error(it)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = LoadState.Loading)
    }

    private suspend fun getDAList(): DAList {
        logger.d("loadDASet")
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
        return DAList(das = das)
    }
}
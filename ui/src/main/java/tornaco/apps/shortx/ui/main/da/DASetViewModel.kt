package tornaco.apps.shortx.ui.main.da

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.ui.main.MainViewModel
import tornaco.apps.shortx.ui.main.model.daToDirectActionUM
import javax.inject.Inject

@HiltViewModel
class DASetViewModel @Inject constructor(@ApplicationContext context: Context) :
    MainViewModel(context) {

    fun loadDASet(setId: String) {
        logger.d("loadDASet")
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            withContext(Dispatchers.IO) {
                val activeJobs = shortXManager.getActiveJobs().apply {
                    logger.d("activeJobs: $this")
                }
                val das = shortXManager.getAllDirectAction()
                    .filter {
                        it.directActionSetId == setId
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
                updateState {
                    copy(das = das, isLoading = false)
                }
            }

        }
    }
}
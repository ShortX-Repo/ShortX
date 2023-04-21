package tornaco.apps.shortx.ui.addrule.fact

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.AppComponentInfo
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.util.*
import javax.inject.Inject

data class AppComponentStates(
    val activities: List<AppComponentInfo> = emptyList(),
    val services: List<AppComponentInfo> = emptyList(),
    val providers: List<AppComponentInfo> = emptyList(),
    val receivers: List<AppComponentInfo> = emptyList(),
    val isLoading: Boolean = false
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AppComponentSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<AppComponentStates>(context, AppComponentStates()) {

    fun loadActivities(pkg: AppPkg) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateState { copy(isLoading = true) }
                val res = shortXManager.getActivities(pkg)
                updateState {
                    copy(activities = res, isLoading = false)
                }
            }
        }
    }

    fun loadServices(pkg: AppPkg) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateState { copy(isLoading = true) }
                val res = shortXManager.getServices(pkg)
                updateState {
                    copy(services = res, isLoading = false)
                }
            }
        }
    }

    fun loadProviders(pkg: AppPkg) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateState { copy(isLoading = true) }
                val res = shortXManager.getProviders(pkg)
                updateState {
                    copy(providers = res, isLoading = false)
                }
            }
        }
    }

    fun loadReceivers(pkg: AppPkg) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateState { copy(isLoading = true) }
                val res = shortXManager.getReceivers(pkg)
                updateState {
                    copy(receivers = res, isLoading = false)
                }
            }
        }
    }
}
package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.remote.RemoteExecuteMVELAction
import tornaco.apps.shortx.ui.remote.RemoteRepo
import java.text.Collator
import java.util.*
import javax.inject.Inject

interface RemoteExecuteMVELActionLoadState {
    object Loading : RemoteExecuteMVELActionLoadState
    object Error : RemoteExecuteMVELActionLoadState
    data class Success(val data: List<RemoteExecuteMVELAction>) : RemoteExecuteMVELActionLoadState
}

data class SelectorState(
    val installedApps: List<App> = emptyList(),
    val remoteActionsState: RemoteExecuteMVELActionLoadState = RemoteExecuteMVELActionLoadState.Loading
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ActionSelectorViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ContextViewModel<SelectorState>(context) {

    @Inject
    lateinit var remoteRepo: RemoteRepo

    override fun initState(): SelectorState {
        return SelectorState()
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) {
                shortXManager.getInstalledApps()
                    .sortedWith { o1, o2 ->
                        Collator.getInstance(Locale.CHINESE).compare(o1.label, o2.label)
                    }.sortedBy { it.category.ordinal }
            }
            updateState {
                copy(installedApps = apps)
            }
        }
    }

    fun loadRemoteActions() {
        viewModelScope.launch {
            updateState {
                copy(remoteActionsState = RemoteExecuteMVELActionLoadState.Loading)
            }
            delay(500)
            kotlin.runCatching {
                val actions = remoteRepo.getActions()
                actions.collectLatest {
                    updateState {
                        copy(remoteActionsState = RemoteExecuteMVELActionLoadState.Success(it))
                    }
                }
                logger.d("loadRemoteActions complete.")
            }.onFailure {
                logger.e(it, "loadRemoteActions error")
                updateState {
                    copy(remoteActionsState = RemoteExecuteMVELActionLoadState.Error)
                }
            }
        }
    }
}
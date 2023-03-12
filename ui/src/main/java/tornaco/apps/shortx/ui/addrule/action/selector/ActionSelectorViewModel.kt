package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.remote.RemoteExecuteMVELAction
import tornaco.apps.shortx.ui.remote.RemoteRepo
import java.text.Collator
import java.util.*
import javax.inject.Inject

data class SelectorState(
    val installedApps: List<App> = emptyList(),
    val remoteActions: List<RemoteExecuteMVELAction> = emptyList()
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
            val actions = remoteRepo.getActions()
            updateState {
                copy(remoteActions = actions)
            }
        }
    }
}
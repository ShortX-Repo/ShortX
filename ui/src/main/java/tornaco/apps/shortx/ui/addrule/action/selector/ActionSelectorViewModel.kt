package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.remote.RemoteExecuteMVELAction
import tornaco.apps.shortx.ui.remote.RemoteRepo
import tornaco.apps.shortx.ui.settings.pref.Pref
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

interface RemoteExecuteMVELActionLoadState {
    object Loading : RemoteExecuteMVELActionLoadState
    object Error : RemoteExecuteMVELActionLoadState
    data class Success(val data: List<RemoteExecuteMVELAction>) : RemoteExecuteMVELActionLoadState
}

data class SelectorState(
    val remoteActionsState: RemoteExecuteMVELActionLoadState = RemoteExecuteMVELActionLoadState.Loading,
    val recentActions: List<KClass<out Action>> = emptyList()
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ActionSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<SelectorState>(context, { SelectorState() }) {

    @Inject
    lateinit var remoteRepo: RemoteRepo

    private val pref by lazy { Pref(context) }

    init {
        viewModelScope.launch {
            pref.recentSelectedActions.collectLatest { classNameList ->
                logger.d("recentSelectedActions changed: $classNameList")
                val actionClasses = classNameList.mapNotNull {
                    kotlin.runCatching { Class.forName(it).kotlin }.getOrElse {
                        logger.e(it, "Action class error")
                        null
                    }
                }.filterIsInstance<KClass<out Action>>().reversed()
                logger.d("recentSelectedActions: $actionClasses")
                updateState {
                    copy(recentActions = actionClasses)
                }
            }
        }
    }

    fun onActionAdd(action: Action) {
        viewModelScope.launch {
            pref.addRecentSelectedAction(action::class.jvmName)
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
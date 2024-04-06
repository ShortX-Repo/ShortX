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
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.remote.RemoteExecuteMVELAction
import tornaco.apps.shortx.ui.remote.RemoteRepo
import tornaco.apps.shortx.ui.settings.pref.Pref
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
    val recentActions: List<KClass<out Action>> = emptyList(),
    val selectedFilter: ActionCategory = ActionCategory.All
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ActionSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<SelectorState>(context, { SelectorState() }) {

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
                }.filterIsInstance<KClass<out Action>>().reversed().take(6)
                logger.d("recentSelectedActions: $actionClasses")
                updateState {
                    copy(recentActions = actionClasses)
                }
            }
        }

        viewModelScope.launch {
            pref.selectedActionFilter.collectLatest {
                updateState {
                    copy(selectedFilter = runCatching {
                        ActionCategory.valueOf(it)
                    }.getOrElse { ActionCategory.All })
                }
            }
        }
    }

    fun onActionAdd(action: Action) {
        viewModelScope.launch {
            pref.addRecentSelectedAction(action::class.jvmName)
        }
    }

    fun selectFilterCategory(category: ActionCategory) {
        updateState {
            copy(selectedFilter = category)
        }
        viewModelScope.launch {
            pref.setSelectedActionFilter(category.name)
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
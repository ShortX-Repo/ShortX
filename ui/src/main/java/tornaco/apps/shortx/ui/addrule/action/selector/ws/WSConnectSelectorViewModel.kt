package tornaco.apps.shortx.ui.addrule.action.selector.ws

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class WSSelectorState(
    val id: String = defaultNewActionId(),
    val url: String = "",
    val evaluatingActions: List<String> = emptyList()
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class WSConnectSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<WSSelectorState>(context, {
        WSSelectorState()
    }) {

    val openActionHandler: ActionContentStateHandler = ActionContentStateHandler()
    val closeActionHandler: ActionContentStateHandler = ActionContentStateHandler()
    val messageActionHandler: ActionContentStateHandler = ActionContentStateHandler()
    val failureActionHandler: ActionContentStateHandler = ActionContentStateHandler()

    private val actionEvaluateListener = object : IActionEvaluateListener.Stub() {
        override fun onStart(id: String) {
            logger.d("actionEvaluateListener#onStart: $id")
            updateState { copy(evaluatingActions = tornaco.apps.shortx.core.shortXManager.getEvaluatingActions()) }
        }

        override fun onComplete(id: String) {
            logger.d("actionEvaluateListener#onComplete: $id")
            updateState { copy(evaluatingActions = tornaco.apps.shortx.core.shortXManager.getEvaluatingActions()) }
        }
    }

    init {
        tornaco.apps.shortx.core.shortXManager.registerActionEvaluateListener(actionEvaluateListener)
    }

    override fun onCleared() {
        super.onCleared()
        tornaco.apps.shortx.core.shortXManager.unregisterActionEvaluateListener(
            actionEvaluateListener
        )
    }


    fun init(currentAction: Action.WebSocketConnect?) {
        updateState { copy(evaluatingActions = tornaco.apps.shortx.core.shortXManager.getEvaluatingActions()) }

        currentAction?.let {
            updateState {
                copy(id = currentAction.id, url = currentAction.url)
            }
            openActionHandler.init(currentAction.openActions)
            closeActionHandler.init(currentAction.closeActions)
            messageActionHandler.init(currentAction.messageActions)
            failureActionHandler.init(currentAction.failureActions)
        }
    }

    fun inputUrl(url: String) {
        updateState {
            copy(url = url)
        }
    }
}
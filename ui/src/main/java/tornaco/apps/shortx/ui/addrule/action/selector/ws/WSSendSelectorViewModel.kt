package tornaco.apps.shortx.ui.addrule.action.selector.ws

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class WSSendSelectorState(
    val id: String = defaultNewActionId(),
    val url: String = "",
    val message: String = "",
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class WSSendSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<WSSendSelectorState>(context, {
        WSSendSelectorState()
    }) {

    fun init(currentAction: Action.WebSocketSend?) {
        currentAction?.let {
            updateState {
                copy(
                    id = currentAction.id,
                    url = currentAction.url,
                    message = currentAction.message
                )
            }
        }
    }

    fun inputUrl(url: String) {
        updateState {
            copy(url = url)
        }
    }

    fun inputMessage(message: String) {
        updateState {
            copy(message = message)
        }
    }
}
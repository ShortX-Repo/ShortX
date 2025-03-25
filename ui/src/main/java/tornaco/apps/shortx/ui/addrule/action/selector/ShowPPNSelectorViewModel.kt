package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.core.proto.action.NotificationButton
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import java.util.UUID
import javax.inject.Inject

data class ShowPPNSelectorState(
    val id: String = defaultNewActionId(),
    val tag: String,
    val title: String,
    val message: String,
    val icon: String,
    val vibrate: Boolean,
    val sound: Boolean,
    val buttonAndHandlers: List<NotificationButtonAndHandler>,
    val onClickHandler: ActionContentStateHandler,
    val evaluatingActions: List<String> = emptyList()
)

fun ShowPPNSelectorState.toAction(): Action.ShowPPN {
    return Action.ShowPPN(
        id = this.id,
        tag = this.tag,
        title = this.title,
        message = this.message,
        icon = this.icon,
        vibrate = this.vibrate,
        sound = this.sound,
        buttons = this.buttonAndHandlers.map { buttonAndHandler ->
            NotificationButton.newBuilder()
                .setLabel(buttonAndHandler.label)
                .setId(buttonAndHandler.id)
                .addAllAction(buttonAndHandler.handler.actions.map { it.toProtoAction().pack_() })
                .build()
        },
        onClickActions = this.onClickHandler.actions,
        displayTimeSeconds = 9
    )
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowPPNSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<ShowPPNSelectorState>(
        context, {
            ShowPPNSelectorState(
                tag = "tag-${System.currentTimeMillis()}",
                title = "",
                message = "",
                icon = "",
                vibrate = false,
                sound = false,
                buttonAndHandlers = emptyList(),
                onClickHandler = ActionContentStateHandler()
            )
        }
    ) {

    private val actionEvaluateListener = object : IActionEvaluateListener.Stub() {
        override fun onStart(id: String) {
            logger.d("actionEvaluateListener#onStart: $id")
            updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }
        }

        override fun onComplete(id: String) {
            logger.d("actionEvaluateListener#onComplete: $id")
            updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }
        }
    }

    init {
        shortXManager.registerActionEvaluateListener(actionEvaluateListener)
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterActionEvaluateListener(actionEvaluateListener)
    }

    fun initFrom(settings: Action.ShowPPN) {
        logger.d("initFrom: $settings")
        updateState {
            copy(
                id = settings.id,
                tag = settings.tag,
                title = settings.title,
                message = settings.message,
                icon = settings.icon,
                vibrate = settings.vibrate,
                sound = settings.sound,
                evaluatingActions = shortXManager.getEvaluatingActions(),
                buttonAndHandlers = settings.buttons.map { notificationButton ->
                    NotificationButtonAndHandler(
                        label = notificationButton.label,
                        id = notificationButton.id,
                        handler = ActionContentStateHandler().apply {
                            init(notificationButton.actionList.mapNotNull {
                                it.toAction(
                                    shortXManager
                                )
                            })
                        })
                },
                onClickHandler = onClickHandler.apply {
                    init(settings.onClickActions)
                }
            )
        }
    }

    fun newButton() {
        val button =
            NotificationButton.newBuilder().setId("Button-${UUID.randomUUID()}").setLabel("-")
                .build()
        updateState {
            copy(
                buttonAndHandlers = buttonAndHandlers.toMutableList().apply {
                    add(
                        NotificationButtonAndHandler(
                            label = button.label,
                            id = button.id,
                            handler = ActionContentStateHandler()
                        )
                    )
                }
            )
        }
    }

    fun removeButton(index: Int) {
        updateState {
            copy(
                buttonAndHandlers = buttonAndHandlers.toMutableList().apply {
                    removeAt(index)
                }
            )
        }
    }

    fun updateTag(it: String) {
        updateState { copy(tag = it) }
    }

    fun updateTitle(it: String) {
        updateState { copy(title = it) }
    }

    fun updateMessage(it: String) {
        updateState { copy(message = it) }
    }

    fun setVibrate(it: Boolean) {
        updateState { copy(vibrate = it) }
    }

    fun setSound(it: Boolean) {
        updateState { copy(sound = it) }
    }

    fun updateIcon(it: String) {
        updateState { copy(icon = it) }
    }

    fun updateLabel(index: Int, it: String) {
        updateState {
            copy(
                buttonAndHandlers = buttonAndHandlers.toMutableList().apply {
                    val current = this[index]
                    val updated = current.copy(label = it)
                    set(index, updated)
                }
            )
        }
    }
}
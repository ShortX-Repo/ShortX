package tornaco.apps.shortx.ui.addrule.action.selector.actionsidebar

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.core.proto.action.ActionSidebarButton
import tornaco.apps.shortx.core.proto.action.ActionSidebarDismissBehavior
import tornaco.apps.shortx.core.proto.action.ActionSidebarPosition
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import java.util.UUID
import javax.inject.Inject

private fun newButtonId() = "ASB-${UUID.randomUUID()}"

data class ShowActionSidebarSelectorState(
    val buttons: List<ActionSidebarButtonEditorState> = emptyList(),
    val tag: String = "",
    val displayTitle: String = "",
    val dismissBehavior: ActionSidebarDismissBehavior = ActionSidebarDismissBehavior.ActionSidebarDismissBehavior_OutsideTapOrClose,
    val position: ActionSidebarPosition = ActionSidebarPosition.ActionSidebarPosition_Left,
    val id: String = defaultNewActionId(),
    val evaluatingActions: List<String> = emptyList(),
    val validationMessage: String? = null,
)

data class ActionSidebarButtonEditorState(
    val id: String = newButtonId(),
    val icon: String = BuildProp.DEFAULT_OVERLAY_BUTTON_ICON,
    val text: String = "",
    val tapActionHandle: ActionContentStateHandler = ActionContentStateHandler(),
    val longPressActionHandle: ActionContentStateHandler = ActionContentStateHandler(),
)

fun ActionSidebarButtonEditorState.toProto(order: Int): ActionSidebarButton {
    return ActionSidebarButton.newBuilder()
        .setId(id.fallbackOnEmpty { newButtonId() })
        .setIcon(icon)
        .setText(text)
        .setOrder(order)
        .addAllTapActions(tapActionHandle.state.value.actions.map { it.toProtoAction().pack_() })
        .addAllLongPressActions(longPressActionHandle.state.value.actions.map { it.toProtoAction().pack_() })
        .build()
}

fun ActionSidebarButton.toEditorState(): ActionSidebarButtonEditorState {
    return ActionSidebarButtonEditorState(
        id = id.fallbackOnEmpty { newButtonId() },
        icon = icon,
        text = text,
        tapActionHandle = ActionContentStateHandler().apply {
            updateState { copy(actions = tapActionsList.mapNotNull { it.toAction(shortXManager) }) }
        },
        longPressActionHandle = ActionContentStateHandler().apply {
            updateState { copy(actions = longPressActionsList.mapNotNull { it.toAction(shortXManager) }) }
        },
    )
}

fun ShowActionSidebarSelectorState.toAction(): Action.ShowActionSidebar {
    return Action.ShowActionSidebar(
        id = id,
        buttons = buttons.mapIndexed { index, button -> button.toProto(index) },
        dismissBehavior = dismissBehavior,
        displayTitle = displayTitle,
        position = position,
        tag = tag,
    )
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowActionSidebarSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<ShowActionSidebarSelectorState>(context, { ShowActionSidebarSelectorState() }) {

    private val actionEvaluateListener = object : IActionEvaluateListener.Stub() {
        override fun onStart(id: String) {
            updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }
        }

        override fun onComplete(id: String) {
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

    fun initFrom(action: Action.ShowActionSidebar) {
        updateState {
            copy(
                id = action.id,
                buttons = action.buttons.sortedBy { it.order }.map { it.toEditorState() },
                tag = action.tag,
                displayTitle = action.displayTitle,
                dismissBehavior = action.dismissBehavior,
                position = action.position,
                evaluatingActions = shortXManager.getEvaluatingActions(),
                validationMessage = null,
            )
        }
    }

    fun addButton() {
        updateState { copy(buttons = buttons + ActionSidebarButtonEditorState(), validationMessage = null) }
    }

    fun removeButton(index: Int) {
        updateState { copy(buttons = buttons.toMutableList().apply { removeAt(index) }) }
    }

    fun updateIcon(index: Int, icon: String) {
        updateState { copy(buttons = buttons.replaceAt(index) { copy(icon = icon) }) }
    }

    fun updateText(index: Int, text: String) {
        updateState { copy(buttons = buttons.replaceAt(index) { copy(text = text) }) }
    }

    fun applySort(sorted: List<ActionSidebarButtonEditorState>) {
        updateState { copy(buttons = sorted) }
    }

    fun updatePosition(position: ActionSidebarPosition) {
        updateState { copy(position = position) }
    }

    fun validate(): Boolean {
        val message = when {
            state.value.buttons.isEmpty() -> "ui.action.show.action.sidebar.empty"
            state.value.buttons.any { it.icon.isBlank() && it.text.isBlank() } -> "ui.action.show.action.sidebar.empty"
            else -> null
        }
        updateState { copy(validationMessage = message) }
        return message == null
    }
}

private fun <T> List<T>.replaceAt(index: Int, transform: T.() -> T): List<T> {
    return toMutableList().apply { set(index, get(index).transform()) }
}

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
import tornaco.apps.shortx.core.proto.action.ActionSidebarStyleSettings
import tornaco.apps.shortx.core.proto.common.UiThemeMode
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

private const val DEFAULT_PANEL_BG = 0xFF1F1F23.toInt()
private const val DEFAULT_ICON_BG = 0xFF3A3A40.toInt()
private const val DEFAULT_ICON_COLOR = 0xFFFFFFFF.toInt()
private const val DEFAULT_TEXT_COLOR = 0xFFFFFFFF.toInt()

fun defaultActionSidebarStyleSettings(): ActionSidebarStyleSettings {
    return ActionSidebarStyleSettings.newBuilder()
        .setBackgroundColor(DEFAULT_PANEL_BG)
        .setBackgroundAlphaPercent(92)
        .setPanelCornerRadiusDp(24)
        .setButtonCornerRadiusDp(18)
        .setPanelWidthDp(108)
        .setMaxHeightDp(520)
        .setIconSizeDp(24)
        .setIconBackgroundSizeDp(46)
        .setIconColor(DEFAULT_ICON_COLOR)
        .setIconBackgroundColor(DEFAULT_ICON_BG)
        .setTextColor(DEFAULT_TEXT_COLOR)
        .setTextSizeSp(11)
        .setContentPaddingHorizontalDp(8)
        .setContentPaddingVerticalDp(8)
        .setItemSpacingDp(10)
        .build()
}

fun ActionSidebarStyleSettings.withDefaults(): ActionSidebarStyleSettings {
    val defaults = defaultActionSidebarStyleSettings()
    return ActionSidebarStyleSettings.newBuilder(this)
        .setBackgroundColor(if (backgroundColor == 0) defaults.backgroundColor else backgroundColor)
        .setBackgroundAlphaPercent(if (backgroundAlphaPercent == 0) defaults.backgroundAlphaPercent else backgroundAlphaPercent)
        .setPanelCornerRadiusDp(if (panelCornerRadiusDp == 0) defaults.panelCornerRadiusDp else panelCornerRadiusDp)
        .setButtonCornerRadiusDp(if (buttonCornerRadiusDp == 0) defaults.buttonCornerRadiusDp else buttonCornerRadiusDp)
        .setPanelWidthDp(if (panelWidthDp == 0) defaults.panelWidthDp else panelWidthDp)
        .setMaxHeightDp(if (maxHeightDp == 0) defaults.maxHeightDp else maxHeightDp)
        .setIconSizeDp(if (iconSizeDp == 0) defaults.iconSizeDp else iconSizeDp)
        .setIconBackgroundSizeDp(if (iconBackgroundSizeDp == 0) defaults.iconBackgroundSizeDp else iconBackgroundSizeDp)
        .setIconColor(if (iconColor == 0) defaults.iconColor else iconColor)
        .setIconBackgroundColor(if (iconBackgroundColor == 0) defaults.iconBackgroundColor else iconBackgroundColor)
        .setTextColor(if (textColor == 0) defaults.textColor else textColor)
        .setTextSizeSp(if (textSizeSp == 0) defaults.textSizeSp else textSizeSp)
        .setContentPaddingHorizontalDp(if (contentPaddingHorizontalDp == 0) defaults.contentPaddingHorizontalDp else contentPaddingHorizontalDp)
        .setContentPaddingVerticalDp(if (contentPaddingVerticalDp == 0) defaults.contentPaddingVerticalDp else contentPaddingVerticalDp)
        .setItemSpacingDp(if (itemSpacingDp == 0) defaults.itemSpacingDp else itemSpacingDp)
        .build()
}

data class ShowActionSidebarSelectorState(
    val buttons: List<ActionSidebarButtonEditorState> = emptyList(),
    val tag: String = "",
    val displayTitle: String = "",
    val dismissBehavior: ActionSidebarDismissBehavior = ActionSidebarDismissBehavior.ActionSidebarDismissBehavior_OutsideTapOrClose,
    val position: ActionSidebarPosition = ActionSidebarPosition.ActionSidebarPosition_Left,
    val style: ActionSidebarStyleSettings = defaultActionSidebarStyleSettings(),
    val themeMode: UiThemeMode = UiThemeMode.UiThemeMode_System,
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
        style = style.withDefaults(),
        themeMode = themeMode,
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
                style = action.style.withDefaults(),
                themeMode = action.themeMode,
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

    fun updateStyle(style: ActionSidebarStyleSettings) {
        updateState { copy(style = style.withDefaults()) }
    }

    fun updateThemeMode(themeMode: UiThemeMode) {
        updateState { copy(themeMode = themeMode) }
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

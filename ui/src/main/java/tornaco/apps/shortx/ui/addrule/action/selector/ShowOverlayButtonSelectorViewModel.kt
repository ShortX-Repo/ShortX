package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.util.*
import javax.inject.Inject

private fun newButtonId() = "BTN-${UUID.randomUUID()}"

data class OverlayButtonSelectorState(
    val buttons: List<Button> = emptyList(),
    val autoCollapse: Boolean = false,

    val tag: String = "",
    val maxWidth: Int = 500,
    val maxHeight: Int = 500,
    val buttonMinWidth: Int = 64,
    val backgroundColor: String = "",
    val backgroundAlpha: Float = 0.8f,
    val overlayPaddingV: Int = 0,
    val overlayPaddingH: Int = 0,
    val enableGlobalDrag: Boolean = true,
    val enableAutoEdge: Boolean = false,
    val id: String = defaultNewActionId(),

    // UI states
    val isButtonListExpanded: Boolean = true
)

data class Button(
    val id: String = newButtonId(),
    val icon: String = BuildProp.DEFAULT_OVERLAY_BUTTON_ICON,
    val label: String = "",
    val actionHandle: ActionContentStateHandler = ActionContentStateHandler(),
    val longClickActionHandle: ActionContentStateHandler = ActionContentStateHandler()
)

fun Button.toOverlayButtonSetting(): OverlayButtonSetting {
    return OverlayButtonSetting.newBuilder()
        .setId(id.fallbackOnEmpty { newButtonId() })
        .setIcon(icon).setLabel(label)
        .addAllActions(actionHandle.state.value.actions.map { it.toProtoAction().pack_() })
        .addAllLongClickActions(longClickActionHandle.state.value.actions.map {
            it.toProtoAction().pack_()
        })
        .build()
}

fun OverlayButtonSetting.toButton(): Button {
    return Button(
        id = id.fallbackOnEmpty { newButtonId() },
        icon = icon,
        label = label,
        actionHandle = ActionContentStateHandler().apply {
            updateState {
                copy(actions = actionsList.mapNotNull {
                    it.toAction(shortXManager = shortXManager)
                })
            }
        },
        longClickActionHandle = ActionContentStateHandler().apply {
            updateState {
                copy(actions = longClickActionsList.mapNotNull {
                    it.toAction(shortXManager = shortXManager)
                })
            }
        },
    )
}

fun OverlayButtonSelectorState.toAction(): Action.ShowOverlay {
    return Action.ShowOverlay(
        id = this.id,
        autoCollapse = this.autoCollapse,
        tag = this.tag,
        settings = this.buttons.map { it.toOverlayButtonSetting() },
        maxHeight = this.maxHeight,
        maxWidth = this.maxWidth,
        buttonMinWidth = this.buttonMinWidth,
        backgroundColor = this.backgroundColor,
        backgroundAlpha = this.backgroundAlpha,
        enableGlobalDrag = this.enableGlobalDrag,
        overlayPaddingV = this.overlayPaddingV,
        overlayPaddingH = this.overlayPaddingH,
        disableAutoEdge = !this.enableAutoEdge
    )
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowOverlayButtonSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<OverlayButtonSelectorState>(context, {
        OverlayButtonSelectorState()
    }) {

    fun initFrom(settings: Action.ShowOverlay) {
        logger.d("initFrom: $settings")
        updateState {
            copy(
                id = settings.id,
                buttons = settings.settings.map { it.toButton() },
                autoCollapse = settings.autoCollapse,
                tag = settings.tag,
                maxWidth = settings.maxWidth,
                maxHeight = settings.maxHeight,
                buttonMinWidth = settings.buttonMinWidth,
                backgroundAlpha = settings.backgroundAlpha,
                backgroundColor = settings.backgroundColor,
                enableGlobalDrag = settings.enableGlobalDrag,
                overlayPaddingH = settings.overlayPaddingH,
                overlayPaddingV = settings.overlayPaddingV,
                enableAutoEdge = !settings.disableAutoEdge
            )
        }
    }

    fun updateIcon(index: Int, icon: String) {
        updateState {
            copy(
                buttons = buttons.toMutableList().apply {
                    val current = get(index)
                    set(index, current.copy(icon = icon))
                }
            )
        }
    }

    fun updateLabel(index: Int, label: String) {
        updateState {
            copy(
                buttons = buttons.toMutableList().apply {
                    val current = get(index)
                    set(index, current.copy(label = label))
                }
            )
        }
    }

    fun updateTag(tag: String) {
        updateState {
            copy(
                tag = tag
            )
        }
    }

    fun setAutoCollapse(autoCollapse: Boolean) {
        updateState {
            copy(
                autoCollapse = autoCollapse
            )
        }
    }

    fun setMaxWidth(max: Int) {
        updateState { copy(maxWidth = max) }
    }

    fun setMaxHeight(max: Int) {
        updateState { copy(maxHeight = max) }
    }

    fun setButtonMinWidth(max: Int) {
        updateState { copy(buttonMinWidth = max) }
    }

    fun setOverlayPaddingV(padding: Int) {
        updateState { copy(overlayPaddingV = padding) }
    }

    fun setOverlayPaddingH(padding: Int) {
        updateState { copy(overlayPaddingH = padding) }
    }

    fun setEnableGlobalDrag(enable: Boolean) {
        updateState { copy(enableGlobalDrag = enable) }
    }

    fun setEnableAutoEdge(enable: Boolean) {
        updateState { copy(enableAutoEdge = enable) }
    }

    fun addButton() {
        updateState {
            copy(buttons = buttons.toMutableList().apply { add(Button()) })
        }
        logger.d("addButton after, items size: ${state.value.buttons.size}")
    }

    fun removeButton(index: Int) {
        updateState {
            copy(buttons = buttons.toMutableList().apply { removeAt(index) })
        }
        logger.d("removeButton after, items size: ${state.value.buttons.size}")
    }

    fun applySort(sorted: List<Button>) {
        updateState { copy(buttons = sorted) }
    }

    fun setButtonListExpanded(expanded: Boolean) {
        updateState { copy(isButtonListExpanded = expanded) }
    }
}
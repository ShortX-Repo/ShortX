package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.core.proto.settings.OverlayOrientation
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
    val orientation: OverlayOrientation = OverlayOrientation.Port,
    val id: String = defaultNewActionId()
)

data class Button(
    val id: String = newButtonId(),
    val icon: String = BuildProp.DEFAULT_OVERLAY_BUTTON_ICON,
    val label: String = "",
    val actionHandle: ActionContentStateHandler = ActionContentStateHandler()
)

fun Button.toOverlayButtonSetting(): OverlayButtonSetting {
    return OverlayButtonSetting.newBuilder()
        .setId(id.fallbackOnEmpty { newButtonId() })
        .setIcon(icon).setLabel(label)
        .addAllActions(actionHandle.state.value.actions.map { it.toProtoAction().pack_() }).build()
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
        })
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
                orientation = settings.orientation
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

    fun setOrientation(orientation: OverlayOrientation) {
        updateState {
            copy(orientation = orientation)
        }
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
}
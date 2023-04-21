package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.action.OverlayButtonSetting
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.util.*
import javax.inject.Inject

data class OverlayButtonSelectorState(
    val buttons: List<Button> = emptyList(),
    val autoCollapse: Boolean = false,

    val tag: String = "",
    val id: String = defaultNewActionId()
)

data class Button(
    val icon: String = BuildProp.DEFAULT_OVERLAY_BUTTON_ICON,
    val label: String = "",
    val actions: List<Action> = emptyList()
)

fun Button.toOverlayButtonSetting(): OverlayButtonSetting {
    return OverlayButtonSetting.newBuilder().setIcon(icon).setLabel(label)
        .addAllActions(actions.map { it.toProtoAction().pack_() }).build()
}

fun OverlayButtonSetting.toButton(): Button {
    return Button(
        icon = icon,
        label = label,
        actions = actionsList.mapNotNull {
            it.toAction(shortXManager = shortXManager)
        })
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowOverlayButtonSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<OverlayButtonSelectorState>(context, OverlayButtonSelectorState()) {

    fun initFrom(settings: Action.ShowOverlay) {
        logger.d("initFrom: $settings")
        updateState {
            copy(
                id = settings.id,
                buttons = settings.settings.map { it.toButton() },
                autoCollapse = settings.autoCollapse,
                tag = settings.tag
            )
        }
    }

    fun addAction(index: Int, action: Action) {
        logger.d("addAction: $index $action current state size: ${state.value.buttons.size}")
        updateState {
            copy(
                buttons = buttons.toMutableList().apply {
                    val current = get(index)
                    set(
                        index,
                        current.copy(actions = current.actions.toMutableList().apply {
                            add(action)
                        })
                    )
                }
            )
        }
    }

    fun removeAction(index: Int, action: Action) {
        updateState {
            copy(
                buttons = buttons.toMutableList().apply {
                    val current = get(index)
                    set(
                        index,
                        current.copy(actions = current.actions.toMutableList().apply {
                            remove(action)
                        })
                    )
                }
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

    fun addButton() {
        updateState {
            copy(
                buttons = buttons.toMutableList().apply {
                    add(Button())
                }
            )
        }
        logger.d("addButton after, items size: ${state.value.buttons.size}")
    }
}
package tornaco.apps.shortx.ui.addrule.action.selector.piemenu

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.action.PieMenuItem
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

private fun newItemId() = "PIE-${UUID.randomUUID()}"

data class PieMenuItemEditorState(
    val id: String = newItemId(),
    val icon: String = "",
    val label: String = "",
    val actionHandle: ActionContentStateHandler = ActionContentStateHandler(),
)

fun PieMenuItemEditorState.toProto(): PieMenuItem {
    val actions = actionHandle.state.value.actions
    val builder = PieMenuItem.newBuilder()
        .setLabel(label)
        .setIconDescription(icon)
    if (actions.isNotEmpty()) {
        builder.setAction(actions.first().toProtoAction().pack_())
    }
    return builder.build()
}

fun PieMenuItem.toEditorState(): PieMenuItemEditorState {
    return PieMenuItemEditorState(
        id = newItemId(),
        icon = iconDescription,
        label = label,
        actionHandle = ActionContentStateHandler().apply {
            if (hasAction()) {
                val action = action.toAction(shortXManager)
                if (action != null) {
                    updateState { copy(actions = listOf(action)) }
                }
            }
        },
    )
}

data class ShowPieMenuSelectorState(
    val items: List<PieMenuItemEditorState> = emptyList(),
    val pieColorArgb: Int = 0xFFCDD7DE.toInt(),
    val iconColorArgb: Int = 0xFF1C1B1F.toInt(),
    val scalePercent: Int = 100,
    val arcSpanDegrees: Int = 160,
    val ringCount: Int = 1,
    val labelSizeSp: Int = 11,
    val id: String = defaultNewActionId(),
    val evaluatingActions: List<String> = emptyList(),
    val validationMessage: String? = null,
)

fun ShowPieMenuSelectorState.toAction(): Action.ShowPieMenu {
    return Action.ShowPieMenu(
        id = id,
        items = items.map { it.toProto() },
        pieColorArgb = pieColorArgb,
        iconColorArgb = iconColorArgb,
        scalePercent = scalePercent,
        arcSpanDegrees = arcSpanDegrees,
        ringCount = ringCount,
        labelSizeSp = labelSizeSp,
    )
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowPieMenuSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<ShowPieMenuSelectorState>(context, { ShowPieMenuSelectorState() }) {

    fun initFrom(action: Action.ShowPieMenu) {
        updateState {
            copy(
                id = action.id,
                items = action.items.map { it.toEditorState() },
                pieColorArgb = action.pieColorArgb,
                iconColorArgb = action.iconColorArgb,
                scalePercent = action.scalePercent,
                arcSpanDegrees = action.arcSpanDegrees,
                ringCount = action.ringCount,
                labelSizeSp = action.labelSizeSp,
                evaluatingActions = shortXManager.getEvaluatingActions(),
                validationMessage = null,
            )
        }
    }

    fun addItem() {
        updateState { copy(items = items + PieMenuItemEditorState(), validationMessage = null) }
    }

    fun removeItem(index: Int) {
        updateState { copy(items = items.toMutableList().apply { removeAt(index) }) }
    }

    fun updateIcon(index: Int, icon: String) {
        updateState { copy(items = items.replaceAt(index) { copy(icon = icon) }) }
    }

    fun updateLabel(index: Int, label: String) {
        updateState { copy(items = items.replaceAt(index) { copy(label = label) }) }
    }

    fun updatePieColor(color: Int) {
        updateState { copy(pieColorArgb = color) }
    }

    fun updateIconColor(color: Int) {
        updateState { copy(iconColorArgb = color) }
    }

    fun updateScale(scale: Int) {
        updateState { copy(scalePercent = scale) }
    }

    fun updateArcSpan(degrees: Int) {
        updateState { copy(arcSpanDegrees = degrees) }
    }

    fun updateRingCount(count: Int) {
        updateState { copy(ringCount = count.coerceIn(1, 3)) }
    }

    fun updateLabelSize(size: Int) {
        updateState { copy(labelSizeSp = size.coerceIn(8, 18)) }
    }

    fun validate(): Boolean {
        val message = when {
            state.value.items.isEmpty() -> "ui.action.show.pie.menu.empty"
            state.value.items.size > 12 -> "ui.action.show.pie.menu.too.many"
            else -> null
        }
        updateState { copy(validationMessage = message) }
        return message == null
    }
}

private fun <T> List<T>.replaceAt(index: Int, transform: T.() -> T): List<T> {
    return toMutableList().apply { set(index, get(index).transform()) }
}

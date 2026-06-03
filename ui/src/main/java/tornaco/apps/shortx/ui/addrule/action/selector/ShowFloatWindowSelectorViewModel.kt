package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.core.proto.common.UiThemeMode
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class FloatWindowSelectorState(
    val buttons: List<Button> = emptyList(),

    val tag: String = "ShortX",
    val id: String = defaultNewActionId(),
    val evaluatingActions: List<String> = emptyList(),

    val panelRadiusScale: Float = 1f,
    val themeMode: UiThemeMode = UiThemeMode.UiThemeMode_System,
    val isButtonListExpanded: Boolean = true
)

fun FloatWindowSelectorState.toAction(): Action.ShowFloatWindow {
    return Action.ShowFloatWindow(
        id = this.id,
        tag = this.tag,
        panelRadiusScale = this.panelRadiusScale,
        themeMode = this.themeMode,
        settings = this.buttons.map { it.toOverlayButtonSetting() }
    )
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowFloatWindowSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<FloatWindowSelectorState>(context, {
        FloatWindowSelectorState()
    }) {

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

    fun initFrom(settings: Action.ShowFloatWindow) {
        logger.d("initFrom: $settings")
        updateState {
            copy(
                id = settings.id,
                buttons = settings.settings.map { it.toButton() },
                tag = settings.tag,
                panelRadiusScale = settings.panelRadiusScale,
                themeMode = settings.themeMode,
                evaluatingActions = shortXManager.getEvaluatingActions()
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

    fun updateTag(tag: String) {
        updateState {
            copy(
                tag = tag
            )
        }
    }

    fun setRadiusScale(scale: Float) {
        updateState { copy(panelRadiusScale = scale) }
    }

    fun setThemeMode(themeMode: UiThemeMode) {
        updateState { copy(themeMode = themeMode) }
    }

    fun addButton() {
        updateState {
            copy(buttons = buttons.toMutableList().apply {
                add(
                    Button(
                        icon = Remix.Animals.dog
                    )
                )
            })
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

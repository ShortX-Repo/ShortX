package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class ShowStatusBarChipSelectorState(
    val text: String = "",
    val icon: String = "",
    val evaluatingActions: List<String> = emptyList(),
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowStatusBarChipSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<ShowStatusBarChipSelectorState>(
        context,
        { ShowStatusBarChipSelectorState() }) {

    val clickActionHandler = ActionContentStateHandler()
    val longClickActionHandler = ActionContentStateHandler()

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

    fun initFrom(settings: Action.ShowStatusBarChip) {
        updateState { copy(evaluatingActions = tornaco.apps.shortx.core.shortXManager.getEvaluatingActions()) }
        updateState {
            copy(text = settings.text, icon = settings.icon.orEmpty())
        }
        clickActionHandler.init(settings.clickActions)
        longClickActionHandler.init(settings.longClickActions)
    }

    fun updateText(text: String) {
        updateState {
            copy(text = text)
        }
    }

    fun updateIcon(icon: String) {
        updateState {
            copy(icon = icon)
        }
    }
}
package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.core.proto.common.UiThemeMode
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class BiometricVerifySelectorState(
    val id: String = defaultNewActionId(),
    val title: String = "ShortX",
    val subtitle: String = "",
    val themeMode: UiThemeMode = UiThemeMode.UiThemeMode_System,
    val evaluatingActions: List<String> = emptyList()
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class BiometricVerifySelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<BiometricVerifySelectorState>(
        context, {
            BiometricVerifySelectorState()
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

    val allowedActionHandler: ActionContentStateHandler = ActionContentStateHandler()
    val deniedActionHandler: ActionContentStateHandler = ActionContentStateHandler()

    init {
        shortXManager.registerActionEvaluateListener(actionEvaluateListener)
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterActionEvaluateListener(actionEvaluateListener)
    }

    fun initFrom(settings: Action.BiometricVerify?) {
        logger.d("initFrom: $settings")
        allowedActionHandler.resetState()
        deniedActionHandler.resetState()

        settings?.let {
            updateState {
                copy(
                    id = it.id,
                    title = it.title,
                    subtitle = it.subtitle,
                    themeMode = it.themeMode,
                    evaluatingActions = shortXManager.getEvaluatingActions()
                )
            }
            allowedActionHandler.init(it.allowActions)
            deniedActionHandler.init(it.denyActions)
        }
    }

    fun getAction(): Action.BiometricVerify {
        return with(state.value) {
            Action.BiometricVerify(
                id = id,
                title = title,
                subtitle = subtitle,
                themeMode = themeMode,
                allowActions = allowedActionHandler.actions,
                denyActions = deniedActionHandler.actions,
            )
        }
    }

    fun updateTitle(title: String) {
        updateState { copy(title = title) }
    }

    fun updateSubtitle(title: String) {
        updateState { copy(subtitle = title) }
    }

    fun updateThemeMode(themeMode: UiThemeMode) {
        updateState { copy(themeMode = themeMode) }
    }
}

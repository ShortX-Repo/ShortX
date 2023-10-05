package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.action.DialogAction
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.util.*
import javax.inject.Inject


data class ShowDialogSelectorState(
    val id: String = defaultNewActionId(),
    val title: String = "",
    val message: String = "",
    val cancelable: Boolean = true,
    val positiveText: String? = null,
    val negativeText: String? = null,
    val neutralText: String? = null,
    val evaluatingActions: List<String> = emptyList()
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowDialogSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<ShowDialogSelectorState>(
        context, { ShowDialogSelectorState() }
    ) {

    val positiveActionContent = ActionContentStateHandler()
    val negativeActionContent = ActionContentStateHandler()
    val neutralActionContent = ActionContentStateHandler()

    override fun resetState() {
        super.resetState()
        positiveActionContent.init(emptyList())
        negativeActionContent.init(emptyList())
        neutralActionContent.init(emptyList())
    }

    fun initFrom(settings: Action.ShowAlertDialog) {
        updateState {
            copy(
                id = settings.id,
                title = settings.title,
                message = settings.message,
                cancelable = settings.cancelable,
                positiveText = settings.positiveText,
                negativeText = settings.negativeText,
                neutralText = settings.neutralText,
                evaluatingActions = shortXManager.getEvaluatingActions()
            )
        }
        positiveActionContent.init(settings.positiveActions)
        negativeActionContent.init(settings.negativeActions)
        neutralActionContent.init(settings.neutralActions)
    }

    fun updateTitle(title: String) {
        updateState {
            copy(title = title)
        }
    }

    fun updateMessage(message: String) {
        updateState {
            copy(message = message)
        }
    }

    fun updateCancelable(cancelable: Boolean) {
        updateState {
            copy(cancelable = cancelable)
        }
    }

    fun updateButtonText(text: String, action: DialogAction) {
        when (action) {
            DialogAction.Positive -> {
                updateState {
                    copy(positiveText = text)
                }
            }

            DialogAction.Negative -> {
                updateState {
                    copy(negativeText = text)
                }
            }

            DialogAction.Neutral -> {
                updateState {
                    copy(neutralText = text)
                }
            }

            DialogAction.UNRECOGNIZED, DialogAction.None -> {}
        }
    }
}
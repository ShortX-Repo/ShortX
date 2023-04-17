package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.action.DialogAction
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.util.*
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowDialogSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<Action.ShowAlertDialog>(context) {

    override fun initState(): Action.ShowAlertDialog {
        return Action.ShowAlertDialog(
            title = "",
            message = ""
        )
    }

    fun initFrom(settings: Action.ShowAlertDialog) {
        updateState(settings)
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

    fun removePositiveAction(action: Action) {
        updateState {
            copy(positiveActions = positiveActions.toMutableList().apply {
                remove(action)
            })
        }
    }

    fun removeNegativeAction(action: Action) {
        updateState {
            copy(negativeActions = negativeActions.toMutableList().apply {
                remove(action)
            })
        }
    }

    fun removeNeutralAction(action: Action) {
        updateState {
            copy(neutralActions = neutralActions.toMutableList().apply {
                remove(action)
            })
        }
    }

    fun addPositiveAction(action: Action) {
        updateState {
            copy(positiveActions = positiveActions.toMutableList().apply {
                val existingIndex = indexOfFirst { it.id == action.id }
                if (existingIndex >= 0) {
                    set(existingIndex, action)
                } else {
                    add(action)
                }
            })
        }
    }

    fun addNegativeAction(action: Action) {
        updateState {
            copy(negativeActions = negativeActions.toMutableList().apply {
                val existingIndex = indexOfFirst { it.id == action.id }
                if (existingIndex >= 0) {
                    set(existingIndex, action)
                } else {
                    add(action)
                }
            })
        }
    }

    fun addNeutralAction(action: Action) {
        updateState {
            copy(neutralActions = neutralActions.toMutableList().apply {
                val existingIndex = indexOfFirst { it.id == action.id }
                if (existingIndex >= 0) {
                    set(existingIndex, action)
                } else {
                    add(action)
                }
            })
        }
    }
}
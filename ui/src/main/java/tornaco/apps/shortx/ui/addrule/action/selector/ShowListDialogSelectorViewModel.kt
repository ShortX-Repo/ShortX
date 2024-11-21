package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.action.ShowListDialogDataType
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class ShowListDialogSelectorState(
    val id: String = defaultNewActionId(),
    val title: String = "",
    val message: String = "",
    val cancelable: Boolean = true,
    val data: String = "A,B,C",
    val isMultipleChoice: Boolean = false,
    val needConfirmAction: Boolean = false,
    val dataType: ShowListDialogDataType = ShowListDialogDataType.ShowListDialogDataType_String,
    val delimiters: List<String> = listOf(",")
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowListDialogSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<ShowListDialogSelectorState>(
        context, { ShowListDialogSelectorState() }
    ) {

    fun initFrom(settings: Action.ShowListDialog?) {
        settings?.let {
            updateState {
                copy(
                    id = settings.id,
                    title = settings.title,
                    message = settings.message,
                    cancelable = settings.cancelable,
                    data = settings.data,
                    isMultipleChoice = settings.isMultipleChoice,
                    delimiters = settings.delimiters,
                    dataType = settings.dataType,
                    needConfirmAction = settings.needConfirmAction
                )
            }
        }
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

    fun updateDataType(dataType: ShowListDialogDataType) {
        updateState {
            copy(dataType = dataType)
        }
    }

    fun updateNeedConfirmAction(needConfirmAction: Boolean) {
        updateState {
            copy(needConfirmAction = needConfirmAction)
        }
    }

    fun updateData(data: String) {
        updateState {
            copy(data = data)
        }
    }


    fun updateIsMultipleChoice(isMultipleChoice: Boolean) {
        updateState {
            copy(isMultipleChoice = isMultipleChoice)
        }
    }

    fun addDelimiters(str: String) {
        updateState {
            copy(
                delimiters = delimiters.toMutableList().apply {
                    add(str)
                }
            )
        }
    }

    fun removeDelimiters(str: String) {
        updateState {
            copy(
                delimiters = delimiters.toMutableList().apply {
                    remove(str)
                }
            )
        }
    }
}
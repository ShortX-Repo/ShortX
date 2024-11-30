package tornaco.apps.shortx.ui.addrule.action.selector.dialog

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.common.ChoiceProp
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.base.SortItem
import tornaco.apps.shortx.ui.util.sortByIndex
import javax.inject.Inject

data class ShowChoiceDialogSelectorState(
    val id: String = defaultNewActionId(),
    val title: String = "",
    val message: String = "",
    val cancelable: Boolean = true,
    val choices: List<ChoiceProp> = emptyList(),
    val isMultipleChoice: Boolean = false,
    val style: DialogUiStyleSettings = DialogUiStyleSettings.newBuilder().setFontScale(1f).build(),
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowChoiceDialogSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<ShowChoiceDialogSelectorState>(
        context, { ShowChoiceDialogSelectorState() }
    ) {

    fun initFrom(settings: Action.ShowChoiceDialog?) {
        settings?.let {
            updateState {
                copy(
                    id = settings.id,
                    title = settings.title,
                    message = settings.message,
                    cancelable = settings.cancelable,
                    choices = settings.choices,
                    isMultipleChoice = settings.isMultipleChoice,
                    style = settings.style,
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

    fun addItem(item: ChoiceProp) {
        updateState {
            copy(choices = this.choices.toMutableList().apply {
                add(item)
            })
        }
    }

    fun removeItem(index: Int) {
        updateState {
            copy(
                choices = this.choices.toMutableList().apply {
                    removeAt(index)
                }
            )
        }
    }

    fun updateItemLabel(label: String, index: Int) {
        updateState {
            copy(
                choices = this.choices.toMutableList().apply {
                    val current = get(index)
                    val updated = ChoiceProp.newBuilder(current).setLabel(label).build()
                    set(index, updated)
                }
            )
        }
    }

    fun updateItemValue(value: String, index: Int) {
        updateState {
            copy(
                choices = this.choices.toMutableList().apply {
                    val current = get(index)
                    val updated = ChoiceProp.newBuilder(current).setValue(value).build()
                    set(index, updated)
                }
            )
        }
    }

    fun updateItemDefSelection(bool: Boolean, index: Int) {
        updateState {
            copy(
                choices = this.choices.toMutableList().apply {
                    val current = get(index)
                    val updated =
                        ChoiceProp.newBuilder(current).setIsSelectedByDefault(bool).build()
                    set(index, updated)
                }
            )
        }
    }

    fun addItem() {
        updateState {
            copy(
                choices = this.choices.toMutableList().apply {
                    add(ChoiceProp.newBuilder().setLabel("Choice $size").build())
                }
            )
        }
    }

    fun applySort(dialogMenuItems: List<SortItem>) {
        val sortedItems = sortByIndex(state.value.choices, dialogMenuItems.map { it.index })
        updateState {
            copy(
                choices = sortedItems
            )
        }
    }

    fun updateIsMultipleChoice(isMultipleChoice: Boolean) {
        updateState {
            copy(isMultipleChoice = isMultipleChoice)
        }
    }

    fun updateStyle(it: DialogUiStyleSettings) {
        updateState { copy(style = it) }
    }
}
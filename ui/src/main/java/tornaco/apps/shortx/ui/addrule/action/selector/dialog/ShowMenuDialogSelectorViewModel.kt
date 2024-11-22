package tornaco.apps.shortx.ui.addrule.action.selector.dialog

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.action.DialogMenuItem
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.base.SortItem
import tornaco.apps.shortx.ui.util.sortByIndex
import javax.inject.Inject

data class MenuDialogUiItem(
    val title: String,
    val actionHandler: ActionContentStateHandler
)

data class ShowMenuDialogSelectorState(
    val id: String = defaultNewActionId(),
    val title: String = "",
    val message: String = "",
    val cancelable: Boolean = true,
    val style: DialogUiStyleSettings = DialogUiStyleSettings.getDefaultInstance(),
    val items: List<MenuDialogUiItem> = emptyList(),
    val evaluatingActions: List<String> = emptyList()
)

fun List<MenuDialogUiItem>.toProtoItems(): List<DialogMenuItem> {
    return map { uiItem ->
        DialogMenuItem.newBuilder()
            .setTitle(uiItem.title)
            .addAllClickActions(uiItem.actionHandler.actions.map {
                it.toProtoAction().pack_()
            })
            .build()
    }
}

fun List<DialogMenuItem>.toUiItems(shortXManager: ShortXManager): List<MenuDialogUiItem> {
    return map { dialogMenuItem ->
        MenuDialogUiItem(dialogMenuItem.title, ActionContentStateHandler().apply {
            init(dialogMenuItem.clickActionsList.mapNotNull {
                it.toAction(shortXManager)
            })
        })
    }
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowMenuDialogSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<ShowMenuDialogSelectorState>(
        context, { ShowMenuDialogSelectorState() }
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

    init {
        shortXManager.registerActionEvaluateListener(actionEvaluateListener)
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterActionEvaluateListener(actionEvaluateListener)
    }

    fun initFrom(settings: Action.ShowMenuDialog?) {
        settings?.let {
            updateState {
                copy(
                    id = settings.id,
                    title = settings.title,
                    message = settings.message,
                    cancelable = settings.cancelable,
                    items = settings.items.toUiItems(shortXManager),
                    style = settings.style,
                )
            }
        }

        updateState {
            copy(
                evaluatingActions = shortXManager.getEvaluatingActions()
            )
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

    fun addItem(item: MenuDialogUiItem) {
        updateState {
            copy(items = this.items.toMutableList().apply {
                add(item)
            })
        }
    }

    fun removeItem(index: Int) {
        updateState {
            copy(
                items = this.items.toMutableList().apply {
                    removeAt(index)
                }
            )
        }
    }

    fun updateItemTitle(title: String, index: Int) {
        updateState {
            copy(
                items = this.items.toMutableList().apply {
                    val current = get(index)
                    val updated = current.copy(title = title)
                    set(index, updated)
                }
            )
        }
    }

    fun addButton() {
        updateState {
            copy(
                items = this.items.toMutableList().apply {
                    add(MenuDialogUiItem("", ActionContentStateHandler()))
                }
            )
        }
    }

    fun applySort(dialogMenuItems: List<SortItem>) {
        val sortedItems = sortByIndex(state.value.items, dialogMenuItems.map { it.index })
        updateState {
            copy(
                items = sortedItems
            )
        }
    }

    fun updateStyle(it: DialogUiStyleSettings) {
        updateState { copy(style = it) }
    }
}
package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberTextInputState

@Composable
fun UiActions(addAction: (Action) -> Unit) {
    val toastSelectionState =
        rememberTextInputState(title = LocalI18N.current["ui.action.show.toast"],
            message = null,
            onSelected = {
                addAction(Action.ShowToast(it))
            })
    TextInputDialog(state = toastSelectionState)

    val i18N = LocalI18N.current
    val uiActionItems = listOf(
        Item(
            Action.TakeScreenshot::class,
            labelAndDescriptionForActionSelector(i18N, Action.TakeScreenshot::class),
            imageVectorForAction(Action.TakeScreenshot::class)
        ),

        Item(
            Action.ShowToast::class,
            labelAndDescriptionForActionSelector(i18N, Action.ShowToast::class),
            imageVectorForAction(Action.ShowToast::class)
        )

    )

    ActionList(
        actionItems = uiActionItems,
        itemSelected = {
            when (it) {
                Action.ShowToast::class -> {
                    toastSelectionState.show()
                }
                Action.TakeScreenshot::class -> {
                    addAction(Action.TakeScreenshot)
                }
            }
        })
}
package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.main.AppRoute

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
        ),

        Item(
            Action.ShowOverlay::class,
            labelAndDescriptionForActionSelector(i18N, Action.ShowOverlay::class),
            imageVectorForAction(Action.ShowOverlay::class)
        ),

        Item(
            Action.HideOverlay::class,
            labelAndDescriptionForActionSelector(i18N, Action.HideOverlay::class),
            imageVectorForAction(Action.HideOverlay::class)
        ),

        Item(
            Action.ShowAlertDialog::class,
            labelAndDescriptionForActionSelector(i18N, Action.ShowAlertDialog::class),
            imageVectorForAction(Action.ShowAlertDialog::class)
        ),
    )

    val navHost = LocalNavHostController.current
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
                Action.HideOverlay::class -> {
                    addAction(Action.HideOverlay)
                }
                Action.ShowOverlay::class -> {
                    navHost.removeData<Action>(KEY_OVERLAY_BUTTON_SETTINGS)
                    navHost.navigate(AppRoute.SelectShowOverlayButton.name)
                }
                Action.ShowAlertDialog::class -> {
                    navHost.removeData<Action>(KEY_DIALOG_SETTINGS)
                    navHost.navigate(AppRoute.SelectShowAlertDialog.name)
                }
            }
        })
}
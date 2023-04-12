package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TextInputState
import tornaco.apps.shortx.ui.base.rememberTextInputState

@Composable
fun UiActions(addAction: (Action) -> Unit) {
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
            Action.ShowDanmu::class,
            labelAndDescriptionForActionSelector(i18N, Action.ShowDanmu::class),
            imageVectorForAction(Action.ShowDanmu::class)
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
            Action.HideAllOverlay::class,
            labelAndDescriptionForActionSelector(i18N, Action.HideAllOverlay::class),
            imageVectorForAction(Action.HideAllOverlay::class)
        ),

        Item(
            Action.ShowAlertDialog::class,
            labelAndDescriptionForActionSelector(i18N, Action.ShowAlertDialog::class),
            imageVectorForAction(Action.ShowAlertDialog::class)
        ),
    )

    val subSelectorStates = uiActionSubSelectors(addAction)

    ActionList(
        actionItems = uiActionItems,
        itemSelected = { it ->
            when (it) {
                Action.ShowToast::class -> {
                    subSelectorStates.toastSelectionState.show()
                }
                Action.TakeScreenshot::class -> {
                    addAction(Action.TakeScreenshot())
                }
                Action.HideAllOverlay::class -> {
                    addAction(Action.HideAllOverlay())
                }
                Action.HideOverlay::class -> {
                    subSelectorStates.hideOverlayDialogState.show()
                }
                Action.ShowOverlay::class -> {
                    subSelectorStates.overlayButtonState.show()
                }
                Action.ShowAlertDialog::class -> {
                    subSelectorStates.showDialogSettingsState.show()
                }
                Action.ShowDanmu::class -> {
                    subSelectorStates.showDanmuSettingsState.show()
                }
            }
        })
}

data class UiActionSubSelectorStates(
    val toastSelectionState: TextInputState,
    val overlayButtonState: ShowOverlayButtonDialogState,
    val showDialogSettingsState: ShowDialogSettingsDialogState,
    val showDanmuSettingsState: ShowDanmuSettingsDialogState,
    val hideOverlayDialogState: HideOverlayDialogState,
)

@Composable
fun uiActionSubSelectors(addAction: (Action) -> Unit): UiActionSubSelectorStates {
    val toastSelectionState =
        rememberTextInputState(title = LocalI18N.current["ui.action.show.toast"],
            message = null,
            onSelected = {
                addAction(Action.ShowToast(message = it))
            })
    TextInputDialog(state = toastSelectionState)

    val overlayButtonState = rememberShowOverlayButtonDialogState(
        onSelected = { buttonSettings ->
            val action = Action.ShowOverlay(
                id = buttonSettings.id,
                autoCollapse = buttonSettings.autoCollapse,
                settings = buttonSettings.items.map { it.toOverlayButtonSetting() },
                tag = buttonSettings.tag
            )
            addAction(action)
        }
    )
    ShowOverlayButtonDialog(state = overlayButtonState)

    val showDanmuSettingsState = rememberShowDanmuSettingsDialogState {
        addAction(it)
    }
    ShowDanmuSettingsDialog(state = showDanmuSettingsState)

    val showDialogSettingsState = rememberShowDialogSettingsDialogState {
        addAction(it)
    }
    ShowDialogSettingsDialog(state = showDialogSettingsState)

    val hideOverlayDialogState = rememberHideOverlayDialogState {
        addAction(it)
    }
    HideOverlayDialog(state = hideOverlayDialogState)

    return UiActionSubSelectorStates(
        toastSelectionState = toastSelectionState,
        overlayButtonState = overlayButtonState,
        showDialogSettingsState = showDialogSettingsState,
        showDanmuSettingsState = showDanmuSettingsState,
        hideOverlayDialogState = hideOverlayDialogState,
    )
}
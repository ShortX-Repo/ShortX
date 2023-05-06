package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TextInputState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import kotlin.reflect.KClass

@Composable
fun uiActionSelectHandler(addAction: (Action) -> Unit): (KClass<out Action>) -> Boolean {
    val subSelectorStates = uiActionSubSelectors(addAction)

    return { it ->
        when (it) {
            Action.ShowToast::class -> {
                subSelectorStates.toastSelectionState.show()
                true
            }
            Action.TakeScreenshot::class -> {
                addAction(Action.TakeScreenshot())
                true
            }
            Action.HideAllOverlay::class -> {
                addAction(Action.HideAllOverlay())
                true
            }
            Action.HideOverlay::class -> {
                subSelectorStates.hideOverlayDialogState.show()
                true
            }
            Action.ShowOverlay::class -> {
                subSelectorStates.overlayButtonState.show()
                true
            }
            Action.ShowAlertDialog::class -> {
                subSelectorStates.showDialogSettingsState.show()
                true
            }
            Action.ShowDanmu::class -> {
                subSelectorStates.showDanmuSettingsState.show()
                true
            }
            Action.ExpandNotification::class -> {
                addAction(Action.ExpandNotification())
                true
            }
            Action.PostNotification::class -> {
                subSelectorStates.postNotificationDialogState.show()
                true
            }
            Action.FindAndClickViewByText::class -> {
                subSelectorStates.findAndClickViewByTextSelectorState.show()
                true
            }
            Action.WaitForIdle::class -> {
                addAction(Action.WaitForIdle())
                true
            }
            else -> false
        }
    }
}

@Composable
fun UiActions(addAction: (Action) -> Unit) {
    val i18N = LocalI18N.current

    val uiActionItems = listOf(
        Action.TakeScreenshot::class,

        Action.ShowToast::class,

        Action.ShowDanmu::class,

        Action.ExpandNotification::class,

        Action.PostNotification::class,

        Action.ShowOverlay::class,

        Action.HideOverlay::class,

        Action.HideAllOverlay::class,

        Action.ShowAlertDialog::class,

        Action.FindAndClickViewByText::class,

        Action.WaitForIdle::class,
    ).map {
        Item(
            it,
            labelAndDescriptionForActionSelector(i18N, it),
            imageVectorForAction(it)
        )
    }

    val handler = uiActionSelectHandler(addAction = addAction)
    ActionList(
        actionItems = uiActionItems,
        itemSelected = { it ->
            handler(it)
        })
}

data class UiActionSubSelectorStates(
    val toastSelectionState: TextInputState,
    val overlayButtonState: ShowOverlayButtonDialogState,
    val showDialogSettingsState: ShowDialogSettingsDialogState,
    val showDanmuSettingsState: ShowDanmuSettingsDialogState,
    val hideOverlayDialogState: HideOverlayDialogState,
    val postNotificationDialogState: PostNotificationDialogState,
    val findAndClickViewByTextSelectorState: FindAndClickViewByTextSelectorState
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
        onSelected = { action ->
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

    val postNotificationDialogState = rememberPostNotificationDialogState {
        addAction(it)
    }
    PostNotificationSelectorDialog(postNotificationDialogState)

    val findAndClickViewByTextSelectorState = rememberFindAndClickViewByTextSelectorState {
        addAction(it)
    }
    FindAndClickViewByTextSelectorDialog(state = findAndClickViewByTextSelectorState)

    return UiActionSubSelectorStates(
        toastSelectionState = toastSelectionState,
        overlayButtonState = overlayButtonState,
        showDialogSettingsState = showDialogSettingsState,
        showDanmuSettingsState = showDanmuSettingsState,
        hideOverlayDialogState = hideOverlayDialogState,
        postNotificationDialogState = postNotificationDialogState,
        findAndClickViewByTextSelectorState = findAndClickViewByTextSelectorState,
    )
}
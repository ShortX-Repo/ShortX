package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TextInputState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.main.SubscribeState
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly

@Composable
fun uiActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val subSelectorStates = uiActionSubSelectors(addAction)

    return { source ->
        when (source.clazz) {
            Action.ShowToast::class -> {
                subSelectorStates.toastSelectionState.show(source.getActionAs<Action.ShowToast>()?.message)
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
                subSelectorStates.hideOverlayDialogState.show(source.getActionAs())
                true
            }

            Action.ShowOverlay::class -> {
                subSelectorStates.overlayButtonState.show(
                    source.getActionAs()
                )
                true
            }

            Action.ShowAlertDialog::class -> {
                subSelectorStates.showDialogSettingsState.show(source.getActionAs())
                true
            }

            Action.ShowMenuDialog::class -> {
                subSelectorStates.showMenuDialogSettingsDialogState.show(source.getActionAs())
                true
            }

            Action.ShowDanmu::class -> {
                subSelectorStates.showDanmuSettingsState.show(source.getActionAs())
                true
            }

            Action.ExpandNotification::class -> {
                addAction(Action.ExpandNotification())
                true
            }

            Action.PostNotification::class -> {
                subSelectorStates.postNotificationDialogState.show(source.getActionAs())
                true
            }

            Action.FindAndClickViewByText::class -> {
                subSelectorStates.findAndClickViewByTextSelectorState.show(source.getActionAs())
                true
            }

            Action.InputTap::class -> {
                subSelectorStates.inputTapSelectorState.show(source.getActionAs())
                true
            }

            Action.InputSwipe::class -> {
                subSelectorStates.inputSwipeSelectorState.show(source.getActionAs())
                true
            }

            Action.WaitForIdle::class -> {
                addAction(Action.WaitForIdle())
                true
            }

            Action.StartGestureRecording::class -> {
                addAction(Action.StartGestureRecording())
                true
            }

            Action.StopGestureRecording::class -> {
                addAction(Action.StopGestureRecording())
                true
            }

            Action.ToggleGestureRecording::class -> {
                addAction(Action.ToggleGestureRecording())
                true
            }

            else -> false
        }
    }
}

@Composable
fun UiActions(keyword: String, subscribeState: SubscribeState, addAction: (Action) -> Unit) {
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
        Action.ShowMenuDialog::class,
        Action.FindAndClickViewByText::class,
        Action.InputTap::class,
        Action.InputSwipe::class,
        Action.WaitForIdle::class,
        Action.StartGestureRecording::class,
        Action.StopGestureRecording::class,
        Action.ToggleGestureRecording::class,
    ).map {
        SelectorItem(
            it,
            labelAndDescriptionForActionSelector(i18N, it),
            imageVectorForAction(it),
            isForSubscribedOnly = remember(subscribeState) {
                !subscribeState.isSubscribed && isForSubscribedOnly(it)
            },
            iconColor = colorForAction(it)
        )
    }.filter {
        keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
    }

    if (uiActionItems.isNotEmpty()) {
        ActionCategoryTitle(ActionCategory.Ui)
    }

    val handler = uiActionSelectHandler(addAction = addAction)
    SelectorList(
        items = uiActionItems,
        itemSelected = { it ->
            handler(ActionSelectSource.FromClass(it))
        })
}

data class UiActionSubSelectorStates(
    val toastSelectionState: TextInputState,
    val overlayButtonState: ShowOverlayButtonDialogState,
    val showDialogSettingsState: ShowDialogSettingsDialogState,
    val showMenuDialogSettingsDialogState: ShowMenuDialogSettingsDialogState,
    val showDanmuSettingsState: ShowDanmuSettingsDialogState,
    val hideOverlayDialogState: HideOverlayDialogState,
    val postNotificationDialogState: PostNotificationDialogState,
    val findAndClickViewByTextSelectorState: FindAndClickViewByTextSelectorState,
    val inputTapSelectorState: InputTapSelectorState,
    val inputSwipeSelectorState: InputSwipeSelectorState,
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

    val showMenuDialogState = rememberShowMenuDialogSettingsDialogState {
        addAction(it)
    }
    ShowMenuDialogSettingsDialog(state = showMenuDialogState)

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

    val inputTapSelectorState = rememberInputTapSelectorState {
        addAction(it)
    }
    InputTapSelectorDialog(state = inputTapSelectorState)

    val inputSwipeSelectorState = rememberInputSwipeSelectorState {
        addAction(it)
    }
    InputSwipeSelectorDialog(state = inputSwipeSelectorState)

    return UiActionSubSelectorStates(
        toastSelectionState = toastSelectionState,
        overlayButtonState = overlayButtonState,
        showDialogSettingsState = showDialogSettingsState,
        showMenuDialogSettingsDialogState = showMenuDialogState,
        showDanmuSettingsState = showDanmuSettingsState,
        hideOverlayDialogState = hideOverlayDialogState,
        postNotificationDialogState = postNotificationDialogState,
        findAndClickViewByTextSelectorState = findAndClickViewByTextSelectorState,
        inputTapSelectorState = inputTapSelectorState,
        inputSwipeSelectorState = inputSwipeSelectorState,
    )
}
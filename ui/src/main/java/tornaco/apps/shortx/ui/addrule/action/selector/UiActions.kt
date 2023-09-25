package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.newIntent
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.toBaseSelectorActionOrNull
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TextInputState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly

@Composable
fun uiActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val context = LocalContext.current

    val subSelectorStates = uiActionSubSelectors(addAction)

    val injectGestureSelectorState = rememberInjectGestureRecordDialogState(onSelected = {
        addAction(it)
    })
    InjectGestureRecordSelectorDialog(state = injectGestureSelectorState)

    val showTextFieldSelectorState = rememberShowTextFieldDialogSettingsDialogState {
        addAction(it)
    }
    ShowTextFieldDialogSettingsDialog(state = showTextFieldSelectorState)


    val showDialogLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.toBaseSelectorActionOrNull<Action.ShowAlertDialog>()
            action?.let {
                addAction(it)
            }
        }
    )

    val showOverlayLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.toBaseSelectorActionOrNull<Action.ShowOverlay>()
            action?.let {
                addAction(it)
            }
        }
    )

    val postNotificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.toBaseSelectorActionOrNull<Action.PostNotification>()
            action?.let {
                addAction(it)
            }
        }
    )

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
                showOverlayLauncher.launch(
                    newIntent(
                        context = context,
                        currentAction = source.getActionAs<Action.ShowOverlay>(),
                        clazz = ShowOverlayButtonSelectorActivity::class.java
                    )
                )
                true
            }

            Action.ShowAlertDialog::class -> {
                showDialogLauncher.launch(
                    newIntent(
                        context = context,
                        currentAction = source.getActionAs<Action.ShowAlertDialog>(),
                        clazz = ShowDialogSelectorActivity::class.java
                    )
                )
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
                postNotificationLauncher.launch(
                    newIntent(
                        context,
                        source.getActionAs<Action.PostNotification>(),
                        PostNotificationSelectorActivity::class.java
                    )
                )
                true
            }

            Action.FindAndClickViewByText::class -> {
                subSelectorStates.findAndClickViewByTextSelectorState.show(source.getActionAs())
                true
            }

            Action.FindAndClickViewById::class -> {
                subSelectorStates.findAndClickViewByIdSelectorState.show(source.getActionAs())
                true
            }

            Action.FindAndClickMatchedView::class -> {
                addAction(Action.FindAndClickMatchedView())
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

            Action.InjectGestureRecording::class -> {
                injectGestureSelectorState.show(source.getActionAs())
                true
            }

            Action.EnableUniversalCopy::class -> {
                addAction(Action.EnableUniversalCopy())
                true
            }

            Action.EnableViewIdViewer::class -> {
                addAction(Action.EnableViewIdViewer())
                true
            }

            Action.ShowTextFieldDialog::class -> {
                showTextFieldSelectorState.show(source.getActionAs())
                true
            }

            else -> false
        }
    }
}

@Composable
fun UiActions(keyword: String, subscribeState: LVLStateHolder.State, addAction: (Action) -> Unit) {
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
        Action.ShowTextFieldDialog::class,
        Action.FindAndClickViewByText::class,
        Action.FindAndClickViewById::class,
        Action.FindAndClickMatchedView::class,
        Action.InputTap::class,
        Action.InputSwipe::class,
        Action.WaitForIdle::class,
        Action.ToggleGestureRecording::class,
        Action.InjectGestureRecording::class,
        Action.EnableUniversalCopy::class,
        Action.EnableViewIdViewer::class,
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
        itemSelected = {
            handler(ActionSelectSource.FromClass(it))
        })
}

data class UiActionSubSelectorStates(
    val toastSelectionState: TextInputState,
    val showMenuDialogSettingsDialogState: ShowMenuDialogSettingsDialogState,
    val showDanmuSettingsState: ShowDanmuSettingsDialogState,
    val hideOverlayDialogState: HideOverlayDialogState,
    val findAndClickViewByTextSelectorState: FindAndClickViewByTextSelectorState,
    val findAndClickViewByIdSelectorState: FindAndClickViewByIdSelectorState,
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

    val showDanmuSettingsState = rememberShowDanmuSettingsDialogState {
        addAction(it)
    }
    ShowDanmuSettingsDialog(state = showDanmuSettingsState)

    val showMenuDialogState = rememberShowMenuDialogSettingsDialogState {
        addAction(it)
    }
    ShowMenuDialogSettingsDialog(state = showMenuDialogState)

    val hideOverlayDialogState = rememberHideOverlayDialogState {
        addAction(it)
    }
    HideOverlayDialog(state = hideOverlayDialogState)

    val findAndClickViewByTextSelectorState = rememberFindAndClickViewByTextSelectorState {
        addAction(it)
    }
    FindAndClickViewByTextSelectorDialog(state = findAndClickViewByTextSelectorState)

    val findAndClickViewByIdSelectorState = rememberFindAndClickViewByIdSelectorState {
        addAction(it)
    }
    FindAndClickViewByIdSelectorDialog(state = findAndClickViewByIdSelectorState)

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
        showMenuDialogSettingsDialogState = showMenuDialogState,
        showDanmuSettingsState = showDanmuSettingsState,
        hideOverlayDialogState = hideOverlayDialogState,
        findAndClickViewByTextSelectorState = findAndClickViewByTextSelectorState,
        findAndClickViewByIdSelectorState = findAndClickViewByIdSelectorState,
        inputTapSelectorState = inputTapSelectorState,
        inputSwipeSelectorState = inputSwipeSelectorState,
    )
}
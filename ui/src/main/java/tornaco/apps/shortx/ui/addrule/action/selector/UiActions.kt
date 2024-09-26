package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.proto.action.ContextMenuAction
import tornaco.apps.shortx.core.proto.action.ScrollViewToLocation
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.mapper.summaryKey
import tornaco.apps.shortx.ui.addrule.action.mapper.toAndroidContextMenuActionLabelKey
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.ActionCategory
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.base.rememberCategoryState
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.newIntent
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly

@Composable
fun uiActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val context = LocalContext.current

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

    val showStatusBarChipLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.ShowStatusBarChip>()
            action?.let {
                addAction(it)
            }
        }
    )

    val hideOverlayDialogState = rememberHideOverlayDialogState {
        addAction(it)
    }
    HideOverlayDialog(state = hideOverlayDialogState)

    val setStatusBarIconLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.SetStatusBarIcon>()
            action?.let {
                addAction(it)
            }
        }
    )

    val removeStatusBarIconState = rememberTextInputState(
        title = LocalI18N.current["ui.action.set.statusbar.icon.slot"],
        message = LocalI18N.current["ui.action.set.statusbar.icon.slot.summary"]
    ) {
        addAction(Action.RemoveStatusBarIcon(slot = it))
    }
    TextInputDialog(state = removeStatusBarIconState)

    val findTextAndClickState = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.FindAndClickViewByText>()
            action?.let {
                addAction(it)
            }
        }
    )

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
            val action = result.popupSelectorResult<Action.ShowAlertDialog>()
            action?.let {
                addAction(it)
            }
        }
    )

    val showMenuDialogLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.ShowMenuDialog>()
            action?.let {
                addAction(it)
            }
        }
    )

    val showOverlayLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.ShowOverlay>()
            action?.let {
                addAction(it)
            }
        }
    )

    val showFloatWindowLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.ShowFloatWindow>()
            action?.let {
                addAction(it)
            }
        }
    )

    val getNodeTextSelectorState = rememberTextInputState(
        title = LocalI18N.current["ui.action.get.text.from.node"],
        message = LocalI18N.current["ui.action.get.text.from.node.tip.get.all"],
        inputValidator = { InputValidateResult.OK },
        onSelected = {
            addAction(Action.GetTextFromScreenNode(nodeId = it))
        }
    )
    TextInputDialog(state = getNodeTextSelectorState)

    val scrollToSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.action.scroll.view.to"],
        menuItems = ScrollViewToLocation.entries
            .filterNot { it == ScrollViewToLocation.UNRECOGNIZED }
            .map {
                MenuDialogItem(
                    id = it.name,
                    title = LocalI18N.current[it.labelKey()],
                    summary = LocalI18N.current.get(it.summaryKey(), fallback = "")
                )
            },
        onItemSelected = { _, id ->
            addAction(Action.ScrollViewTo(location = ScrollViewToLocation.valueOf(id)))
        }
    )
    MenuDialog(state = scrollToSelectorState)

    val inputTextSelectorState = rememberTextInputState(
        title = LocalI18N.current["ui.action.input.text"],
        message = null,
        onSelected = {
            addAction(Action.InputText(text = it))
        })
    TextInputDialog(state = inputTextSelectorState)


    val performContextMenuSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.action.perform.context.menu.action"],
        menuItems = ContextMenuAction.entries
            .filterNot { it == ContextMenuAction.UNRECOGNIZED }
            .map {
                MenuDialogItem(
                    id = it.name,
                    title = LocalI18N.current[it.toAndroidContextMenuActionLabelKey()],
                )
            },
        onItemSelected = { _, id ->
            addAction(Action.PerformContextMenuAction(action = ContextMenuAction.valueOf(id)))
        }
    )
    MenuDialog(state = performContextMenuSelectorState)

    val screenFlashLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.ScreenFlash>()
            action?.let {
                addAction(it)
            }
        }
    )

    val displayImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.DisplayImage>()
            action?.let {
                addAction(it)
            }
        }
    )

    return { source ->
        when (source.clazz) {
            Action.ShowToast::class -> {
                toastSelectionState.show(source.getActionAs<Action.ShowToast>()?.message)
                true
            }

            Action.TakeScreenshot::class -> {
                addAction(Action.TakeScreenshot())
                true
            }

            Action.SelectScreenArea::class -> {
                addAction(Action.SelectScreenArea())
                true
            }

            Action.AreaScreenshot::class -> {
                addAction(Action.AreaScreenshot())
                true
            }

            Action.HideAllOverlay::class -> {
                addAction(Action.HideAllOverlay())
                true
            }

            Action.HideOverlay::class -> {
                hideOverlayDialogState.show(source.getActionAs())
                true
            }

            Action.HideFloatWindow::class -> {
                addAction(Action.HideFloatWindow(tags = emptyList()))
                true
            }

            Action.ShowOverlay::class -> {
                showOverlayLauncher.launch(
                    newIntent(
                        context = context,
                        currentData = source.getActionAs<Action.ShowOverlay>(),
                        clazz = ShowOverlayButtonSelectorActivity::class.java
                    )
                )
                true
            }

            Action.ShowFloatWindow::class -> {
                showFloatWindowLauncher.launch(
                    newIntent(
                        context = context,
                        currentData = source.getActionAs<Action.ShowFloatWindow>(),
                        clazz = ShowFloatWindowSelectorActivity::class.java
                    )
                )
                true
            }

            Action.ShowAlertDialog::class -> {
                showDialogLauncher.launch(
                    newIntent(
                        context = context,
                        currentData = source.getActionAs<Action.ShowAlertDialog>(),
                        clazz = ShowDialogSelectorActivity::class.java
                    )
                )
                true
            }

            Action.ShowMenuDialog::class -> {
                showMenuDialogLauncher.launch(
                    newIntent(
                        context = context,
                        currentData = source.getActionAs<Action.ShowMenuDialog>(),
                        clazz = ShowMenuDialogSelectorActivity::class.java
                    )
                )
                true
            }

            Action.ShowDanmu::class -> {
                showDanmuSettingsState.show(source.getActionAs())
                true
            }

            Action.ShowStatusBarChip::class -> {
                showStatusBarChipLauncher.launch(
                    newIntent(
                        context = context,
                        currentData = source.getActionAs<Action.ShowStatusBarChip>(),
                        clazz = ShowStatusBarChipSelectorActivity::class.java
                    )
                )
                true
            }

            Action.HideStatusBarClip::class -> {
                addAction(Action.HideStatusBarClip())
                true
            }

            Action.SetStatusBarIcon::class -> {
                setStatusBarIconLauncher.launch(
                    newIntent(
                        context = context,
                        currentData = source.getActionAs<Action.SetStatusBarIcon>(),
                        clazz = SetStatusBarIconSelectorActivity::class.java
                    )
                )
                true
            }

            Action.RemoveStatusBarIcon::class -> {
                removeStatusBarIconState.show(source.getActionAs<Action.RemoveStatusBarIcon>()?.slot)
                true
            }

            Action.ScreenFlash::class -> {
                screenFlashLauncher.launch(
                    newIntent(
                        context = context,
                        currentData = source.getActionAs<Action.ScreenFlash>(),
                        clazz = ScreenFlashSelectorActivity::class.java
                    )
                )
                true
            }

            Action.FindAndClickViewByText::class -> {
                findTextAndClickState.launch(
                    newIntent(
                        context = context,
                        currentData = source.getActionAs<Action.FindAndClickViewByText>(),
                        clazz = FindAndClickViewByTextSelectorActivity::class.java
                    )
                )
                true
            }

            Action.FindAndClickViewById::class -> {
                findAndClickViewByIdSelectorState.show(source.getActionAs())
                true
            }

            Action.FindAndClickMatchedView::class -> {
                addAction(Action.FindAndClickMatchedView())
                true
            }

            Action.InputTap::class -> {
                inputTapSelectorState.show(source.getActionAs())
                true
            }

            Action.InputSwipe::class -> {
                inputSwipeSelectorState.show(source.getActionAs())
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

            Action.GetTextFromScreenNode::class -> {
                getNodeTextSelectorState.show(source.getActionAs<Action.GetTextFromScreenNode>()?.nodeId)
                true
            }

            Action.ScrollViewTo::class -> {
                scrollToSelectorState.show()
                true
            }

            Action.InputText::class -> {
                inputTextSelectorState.show(source.getActionAs<Action.InputText>()?.text)
                true
            }

            Action.PerformContextMenuAction::class -> {
                performContextMenuSelectorState.show()
                true
            }

            Action.ShowDrawBoard::class -> {
                addAction(Action.ShowDrawBoard())
                true
            }

            Action.ShowClipboardView::class -> {
                addAction(Action.ShowClipboardView())
                true
            }

            Action.DisplayImage::class -> {
                displayImageLauncher.launch(
                    newIntent(
                        context = context,
                        currentData = source.getActionAs<Action.DisplayImage>(),
                        clazz = DisplayImageSelectorActivity::class.java
                    )
                )
                true
            }

            else -> false
        }
    }
}

@Composable
fun UiActions(
    keyword: String,
    subscribeState: LVLStateHolder.State,
    selectedFilter: ActionCategory,
    addAction: (Action) -> Unit
) {
    val i18N = LocalI18N.current

    val uiActionItems = listOf(
        Action.TakeScreenshot::class,
        Action.AreaScreenshot::class,
        Action.SelectScreenArea::class,
        Action.ShowToast::class,
        Action.ShowDanmu::class,
        Action.ShowStatusBarChip::class,
        Action.HideStatusBarClip::class,
        Action.SetStatusBarIcon::class,
        Action.RemoveStatusBarIcon::class,
        Action.ShowOverlay::class,
        Action.HideOverlay::class,
        Action.HideAllOverlay::class,
        Action.ShowFloatWindow::class,
        Action.HideFloatWindow::class,
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
        Action.GetTextFromScreenNode::class,
        Action.ScrollViewTo::class,
        Action.InputText::class,
        Action.PerformContextMenuAction::class,
        Action.ShowDrawBoard::class,
        Action.ShowClipboardView::class,
        Action.ScreenFlash::class,
        Action.DisplayImage::class,
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

    val categoryState = rememberCategoryState(categoryTitle = i18N[ActionCategory.Ui.i18NKey])
    if (uiActionItems.isNotEmpty() && selectedFilter == ActionCategory.All) {
        ActionCategory(i18N[ActionCategory.Ui.i18NKey], uiActionItems.size, categoryState)
    }

    val isVisible =
        (categoryState.isExpand && selectedFilter == ActionCategory.All) || selectedFilter == ActionCategory.Ui

    val handler = uiActionSelectHandler(addAction = addAction)
    AnimatedVisibility(visible = isVisible) {
        SelectorList(
            items = uiActionItems,
            itemSelected = {
                handler(ActionSelectSource.FromClass(it))
            })
    }
}


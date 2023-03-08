package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.*
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.main.AppRoute

@Composable
fun ExtActions(addAction: (Action) -> Unit) {
    val shellCommandSelectorState = rememberShellCommandSelectorState { cmd ->
        addAction(Action.ShellCommand(cmd))
    }
    ShellCommandSelector(state = shellCommandSelectorState)

    val inputTextSelectorState = rememberInputTextSelectorState(onSelected = {
        addAction(Action.InputText(it))
    })
    InputTextSelector(state = inputTextSelectorState)

    val delaySelectionState =
        rememberTextInputState(title = LocalI18N.current["ui.action.delay"],
            message = LocalI18N.current["ui.action.delay.input.message"],
            onSelected = {
                addAction(Action.Delay(it.toLongOrNull() ?: 0))
            })
    TextInputDialog(state = delaySelectionState)

    val mvelSelectorState = rememberMVELSelectorState(
        title = LocalI18N.current["ui.title.mvel"]
    ) { expression ->
        addAction(Action.MVEL(expression))
    }
    MVELSelector(state = mvelSelectorState)


    // For Overlay button selector
    val navHost = LocalNavHostController.current

    val i18N = LocalI18N.current
    val actionItems = listOf(
        Item(
            Action.ShellCommand::class,
            labelAndDescriptionForActionSelector(i18N, Action.ShellCommand::class),
            imageVectorForAction(Action.ShellCommand::class)
        ),

        Item(
            Action.InputText::class,
            labelAndDescriptionForActionSelector(i18N, Action.InputText::class),
            imageVectorForAction(Action.InputText::class)
        ),
        Item(
            Action.Delay::class,
            labelAndDescriptionForActionSelector(i18N, Action.Delay::class),
            imageVectorForAction(Action.Delay::class)
        ),

        Item(
            Action.MVEL::class,
            labelAndDescriptionForActionSelector(i18N, Action.MVEL::class),
            imageVectorForAction(Action.MVEL::class)
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
            Action.Brk::class,
            labelAndDescriptionForActionSelector(i18N, Action.Brk::class),
            imageVectorForAction(Action.Brk::class)
        ),
    )

    ActionList(
        actionItems = actionItems,
        itemSelected = {
            when (it) {
                Action.ShellCommand::class -> {
                    shellCommandSelectorState.show()
                }
                Action.InputText::class -> {
                    inputTextSelectorState.show()
                }
                Action.Delay::class -> {
                    delaySelectionState.show()
                }
                Action.MVEL::class -> {
                    mvelSelectorState.show()
                }
                Action.Brk::class -> {
                    addAction(Action.Brk)
                }
                Action.HideOverlay::class -> {
                    addAction(Action.HideOverlay)
                }
                Action.ShowOverlay::class -> {
                    navHost.removeData<Action>(KEY_OVERLAY_BUTTON_SETTINGS)
                    navHost.navigate(AppRoute.SelectShowOverlayButton.name)
                }
            }
        })
}
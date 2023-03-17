package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberTextInputState

@Composable
fun ExtActions(addAction: (Action) -> Unit) {
    val shellCommandSelectorState = rememberShellCommandSelectorState { cmd ->
        addAction(Action.ShellCommand(command = cmd))
    }
    ShellCommandSelector(state = shellCommandSelectorState)

    val inputTextSelectorState = rememberInputTextSelectorState(onSelected = {
        addAction(Action.InputText(text = it))
    })
    InputTextSelector(state = inputTextSelectorState)

    val delaySelectionState =
        rememberTextInputState(title = LocalI18N.current["ui.action.delay"],
            message = LocalI18N.current["ui.action.delay.input.message"],
            onSelected = {
                addAction(Action.Delay(time = it.toLongOrNull() ?: 0))
            })
    TextInputDialog(state = delaySelectionState)

    val mvelSelectorState = rememberMVELSelectorState(
        title = LocalI18N.current["ui.title.mvel"]
    ) { expression ->
        addAction(Action.MVEL(expression = expression))
    }
    MVELSelector(state = mvelSelectorState)

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
            Action.Brk::class,
            labelAndDescriptionForActionSelector(i18N, Action.Brk::class),
            imageVectorForAction(Action.Brk::class)
        ),
    )

    ActionList(
        actionItems = actionItems,
        itemSelected = { it ->
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
                    addAction(Action.Brk())
                }
            }
        })
}
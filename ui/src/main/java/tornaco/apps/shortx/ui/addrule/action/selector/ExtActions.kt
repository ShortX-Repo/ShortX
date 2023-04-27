package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.rule.toProtoType
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.code.CodeEditorDialog
import tornaco.apps.shortx.ui.base.code.rememberCodeEditorState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.gvar.NewGlobalVarDialog
import tornaco.apps.shortx.ui.gvar.rememberNewGlobalVarState
import java.util.*

@Composable
fun ExtActions(addAction: (Action) -> Unit) {
    val shellCommandSelectorState = rememberShellCommandSelectorState { cmd ->
        addAction(cmd)
    }
    ShellCommandSelector(state = shellCommandSelectorState)

    val inputTextSelectorState = rememberTextSelectorState(onSelected = {
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

    val mvelSelectorState = rememberCodeEditorState(
        title = LocalI18N.current["ui.title.mvel"]
    ) { expression ->
        addAction(Action.MVEL(expression = expression.src))
    }
    CodeEditorDialog(state = mvelSelectorState)

    val writeGVSelectorState = rememberWriteGVDialogState {
        addAction(it)
    }
    WriteGVDialog(writeGVSelectorState)

    val fromDASelectorState = rememberFromDADialogState {
        addAction(it)
    }
    FromDASelectorDialog(state = fromDASelectorState)

    val ifThenElseDialogState = rememberIfThenElseSelectorDialogDialogState {
        addAction(it)
    }
    IfThenElseSelectorDialogDialog(state = ifThenElseDialogState)

    val addGlobalVarDialogState =
        rememberNewGlobalVarState {
            addAction(
                Action.CreateGV(
                    globalVar = GlobalVar.newBuilder().setName(it.first)
                        .setId(UUID.randomUUID().toString())
                        .setType(it.second.toProtoType())
                        .build()
                )
            )
        }
    NewGlobalVarDialog(addGlobalVarDialogState)

    val i18N = LocalI18N.current
    val actionItems = listOf(
        Action.FromDA::class,
        Action.ShellCommand::class,
        Action.InputText::class,
        Action.Delay::class,
        Action.MVEL::class,
        Action.Brk::class,
        Action.WriteGV::class,
        Action.CreateGV::class,
        Action.IfThenElse::class,
    ).map {
        Item(
            type = it,
            labelAndDescription = labelAndDescriptionForActionSelector(i18N, it),
            icon = imageVectorForAction(it)
        )
    }

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
                Action.WriteGV::class -> {
                    writeGVSelectorState.show()
                }
                Action.CreateGV::class -> {
                    addGlobalVarDialogState.show()
                }
                Action.FromDA::class -> {
                    fromDASelectorState.show()
                }
                Action.IfThenElse::class -> {
                    ifThenElseDialogState.show()
                }
            }
        })
}
package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.rule.action.su.shellResToString
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.code.CodeEditorDialog
import tornaco.apps.shortx.ui.base.code.rememberCodeEditorState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.gvar.NewGlobalVarDialog
import tornaco.apps.shortx.ui.gvar.rememberNewGlobalVarState
import tornaco.apps.shortx.ui.gvar.toGlobalVar
import tornaco.apps.shortx.ui.pkgset.PkgSetListViewModel

@Composable
fun extActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val isShellInjectionEnabled = remember { shortXManager.isInjectedShellEnabled() }
    val shellCommandSelectorState =
        rememberCodeEditorState(title = LocalI18N.current["ui.action.shell"],
            message = if (isShellInjectionEnabled) null else LocalI18N.current["ui.action.shell.summary"],
            execute = {
                shortXManager.executeShellCommand(it).shellResToString()
            },
            onSelected = {
                addAction(Action.ShellCommand(command = it.src, note = it.note))
            })
    CodeEditorDialog(state = shellCommandSelectorState)

    val inputTextSelectorState = rememberTextSelectorState(onSelected = {
        addAction(Action.InputText(text = it))
    })
    InputTextSelector(state = inputTextSelectorState)

    val delaySelectionState =
        rememberTextInputState(title = LocalI18N.current["ui.action.delay"],
            message = LocalI18N.current["ui.action.delay.input.message"],
            showSymbolButton = false,
            onSelected = {
                addAction(Action.Delay(time = it.toLongOrNull() ?: 0))
            })
    TextInputDialog(state = delaySelectionState)

    val mvelSelectorState = rememberCodeEditorState(
        title = LocalI18N.current["ui.title.mvel"],
        execute = {
            shortXManager.executeMVEL(it)
        }
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

    val whileLoopSelectorState = rememberWhileLoopDialogState {
        addAction(it)
    }
    WhileLoopDialog(state = whileLoopSelectorState)

    val addGlobalVarDialogState =
        rememberNewGlobalVarState {
            addAction(
                Action.CreateGV(
                    globalVar = it.toGlobalVar()
                )
            )
        }
    NewGlobalVarDialog(addGlobalVarDialogState)

    val i18N = LocalI18N.current
    val vm = hiltViewModel<PkgSetListViewModel>()
    val addPkgSetDialogState =
        rememberTextInputState(
            title = i18N["ui.pkg.set.add.title"],
            message = i18N["ui.pkg.set.add.input.label"],
            showSymbolButton = false,
            inputValidator = {
                vm.validateLabel(it, i18N)
            },
            onSelected = {
                addAction(Action.CreatePkgSet(label = it))
            })
    TextInputDialog(addPkgSetDialogState)

    val httpRequestDialogState = rememberHttpRequestDialogState(onSelected = {
        addAction(it)
    })
    HttpRequestDialog(state = httpRequestDialogState)

    return { source ->
        when (source.clazz) {
            Action.ShellCommand::class -> {
                shellCommandSelectorState.show(source.getActionAs<Action.ShellCommand>()?.command)
                true
            }

            Action.InputText::class -> {
                inputTextSelectorState.show(source.getActionAs<Action.InputText>()?.text)
                true
            }

            Action.Delay::class -> {
                delaySelectionState.show(source.getActionAs<Action.Delay>()?.time?.toString())
                true
            }

            Action.MVEL::class -> {
                mvelSelectorState.show(source.getActionAs<Action.MVEL>()?.expression)
                true
            }

            Action.Brk::class -> {
                addAction(Action.Brk())
                true
            }

            Action.WriteGV::class -> {
                writeGVSelectorState.show()
                true
            }

            Action.CreateGV::class -> {
                addGlobalVarDialogState.show()
                true
            }

            Action.FromDA::class -> {
                fromDASelectorState.show()
                true
            }

            Action.IfThenElse::class -> {
                ifThenElseDialogState.show(source.getActionAs())
                true
            }

            Action.WhileLoop::class -> {
                whileLoopSelectorState.show(source.getActionAs())
                true
            }

            Action.CreatePkgSet::class -> {
                addPkgSetDialogState.show(source.getActionAs<Action.CreatePkgSet>()?.label)
                true
            }

            Action.HttpRequest::class -> {
                httpRequestDialogState.show(source.getActionAs())
                true
            }

            else -> {
                false
            }
        }

    }
}

@Composable
fun ExtActions(keyword: String, addAction: (Action) -> Unit) {
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
        Action.CreatePkgSet::class,
        Action.IfThenElse::class,
        Action.WhileLoop::class,
        Action.HttpRequest::class,
    ).map {
        SelectorItem(
            type = it,
            labelAndDescription = labelAndDescriptionForActionSelector(i18N, it),
            icon = imageVectorForAction(it),
            iconColor = colorForAction(it)
        )
    }.filter {
        keyword.isBlank() || it.labelAndDescription.first.contains(keyword)
    }

    if (actionItems.isNotEmpty()) {
        ActionCategoryTitle(ActionCategory.Advanced)
    }

    val itemSelected = extActionSelectHandler(addAction = addAction)

    SelectorList(
        items = actionItems,
        itemSelected = { it ->
            itemSelected(ActionSelectSource.FromClass(it))
        }
    )
}
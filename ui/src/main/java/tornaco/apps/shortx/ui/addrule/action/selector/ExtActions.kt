package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.action.BreakActionExecuteScope
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.selector.http.HttpRequestSelectorActivity
import tornaco.apps.shortx.ui.addrule.base.ActionCategory
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.base.rememberCategoryState
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.newIntent
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.toBaseSelectorActionOrNull
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.code.CodeEditorDialog
import tornaco.apps.shortx.ui.base.code.rememberCodeEditorState
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.pkgset.PkgSetListViewModel
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly

@Composable
fun extActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val i18N = LocalI18N.current
    val context = LocalContext.current

    val isShellInjectionEnabled = remember { shortXManager.isInjectedShellEnabled() }
    val shellCommandSelectorState =
        rememberShellCommandCodeEditorState(title = LocalI18N.current["ui.action.shell"],
            message = if (isShellInjectionEnabled) null else LocalI18N.current["ui.action.shell.summary"],
            execute = {
                shortXManager.executeShellCommand(it)
            },
            onSelected = { code, single ->
                addAction(
                    Action.ShellCommand(
                        command = code.src,
                        note = code.note,
                        singleShot = single
                    )
                )
            })
    ShellCommandCodeEditorDialog(state = shellCommandSelectorState)

    val inputTextSelectorState = rememberTextInputState(
        title = LocalI18N.current["ui.action.input.text"],
        message = null,
        onSelected = {
            addAction(Action.InputText(text = it))
        })
    TextInputDialog(state = inputTextSelectorState)

    val delaySelectionState =
        rememberDelayState(
            canUseAlarm = true,
            onSelected = {
                addAction(it)
            })
    DelaySelectorDialog(state = delaySelectionState)

    val mvelSelectorState = rememberCodeEditorState(
        title = LocalI18N.current["ui.title.mvel"],
        guideTextAndLink = LocalI18N.current["ui.user.guide"] to BuildProp.WIKI_URL_MVEL,
        execute = {
            shortXManager.executeMVEL(it)
        }
    ) { expression ->
        addAction(Action.MVEL(expression = expression.src))
    }
    CodeEditorDialog(state = mvelSelectorState)

    val jsSelectorState = rememberCodeEditorState(
        title = LocalI18N.current["ui.title.js"],
        execute = {
            shortXManager.executeJS(it)
        }
    ) { expression ->
        addAction(Action.ExecuteJS(expression = expression.src))
    }
    CodeEditorDialog(state = jsSelectorState)

    val fromDASelectorState = rememberFromDADialogState {
        addAction(it)
    }
    FromDASelectorDialog(state = fromDASelectorState)

    val setRuleEnabledState = rememberSetRuleEnabledDialogState(
        onSelected = {
            addAction(it)
        },
        title = LocalI18N.current["ui.action.set.rule.enabled"],
    )
    SetRuleEnabledSelectorDialog(state = setRuleEnabledState)


    val ifThenElseLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.toBaseSelectorActionOrNull<Action.IfThenElse>()
            action?.let {
                addAction(it)
            }
        }
    )

    val whileLoopSelectorState = rememberWhileLoopDialogState {
        addAction(it)
    }
    WhileLoopDialog(state = whileLoopSelectorState)

    val vm = hiltViewModel<PkgSetListViewModel>()
    val addPkgSetDialogState =
        rememberTextInputState(
            title = i18N["ui.pkg.set.add.title"],
            message = i18N["ui.pkg.set.add.input.label"],
            showSymbolButton = false,
            inputValidator = {
                vm.validateLabel(label = it, i18N = i18N, errorIfExists = false)
            },
            onSelected = {
                addAction(Action.CreatePkgSet(label = it))
            })
    TextInputDialog(addPkgSetDialogState)

    val httpLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.toBaseSelectorActionOrNull<Action.HttpRequest>()
            action?.let {
                addAction(it)
            }
        }
    )

    val brkScopeSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.action.brk.scope"],
        menuItems = BreakActionExecuteScope.values()
            .filterNot { it == BreakActionExecuteScope.UNRECOGNIZED }.map {
                MenuDialogItem(id = it.name, title = LocalI18N.current[it.labelKey()])
            },
        onItemSelected = { data, id ->
            val scope = BreakActionExecuteScope.valueOf(id)
            addAction(Action.Brk(scope = scope))
        }
    )
    MenuDialog(state = brkScopeSelectorState)

    return { source ->
        when (source.clazz) {
            Action.ShellCommand::class -> {
                shellCommandSelectorState.show(
                    source.getActionAs<Action.ShellCommand>()?.command,
                    source.getActionAs<Action.ShellCommand>()?.singleShot ?: false
                )
                true
            }

            Action.InputText::class -> {
                inputTextSelectorState.show(source.getActionAs<Action.InputText>()?.text)
                true
            }

            Action.Delay::class -> {
                delaySelectionState.show(source.getActionAs())
                true
            }

            Action.MVEL::class -> {
                mvelSelectorState.show(source.getActionAs<Action.MVEL>()?.expression)
                true
            }

            Action.ExecuteJS::class -> {
                jsSelectorState.show(source.getActionAs<Action.ExecuteJS>()?.expression)
                true
            }

            Action.Brk::class -> {
                brkScopeSelectorState.show(currentSelection = listOfNotNull(source.getActionAs<Action.Brk>()?.scope?.name))
                true
            }

            Action.FromDA::class -> {
                fromDASelectorState.show()
                true
            }

            Action.SetRuleEnabled::class -> {
                setRuleEnabledState.show(source.getActionAs())
                true
            }

            Action.IfThenElse::class -> {
                ifThenElseLauncher.launch(
                    newIntent(
                        context,
                        source.getActionAs<Action.IfThenElse>(),
                        IfThenElseSelectorActivity::class.java
                    )
                )
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
                httpLauncher.launch(
                    newIntent(
                        context,
                        source.getActionAs<Action.HttpRequest>(),
                        HttpRequestSelectorActivity::class.java
                    )
                )
                true
            }

            else -> {
                false
            }
        }

    }
}

@Composable
fun ExtActions(keyword: String, subscribeState: LVLStateHolder.State, addAction: (Action) -> Unit) {
    val i18N = LocalI18N.current
    val actionItems = listOf(
        Action.FromDA::class,
        Action.SetRuleEnabled::class,
        Action.ShellCommand::class,
        Action.InputText::class,
        Action.Delay::class,
        Action.MVEL::class,
        Action.Brk::class,
        Action.CreatePkgSet::class,
        Action.IfThenElse::class,
        Action.WhileLoop::class,
        Action.HttpRequest::class,
        Action.ExecuteJS::class,
    ).map {
        SelectorItem(
            type = it,
            labelAndDescription = labelAndDescriptionForActionSelector(i18N, it),
            icon = imageVectorForAction(it),
            isForSubscribedOnly = remember(subscribeState) {
                !subscribeState.isSubscribed && isForSubscribedOnly(it)
            },
            iconColor = colorForAction(it)
        )
    }.filter {
        keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
    }

    val categoryState = rememberCategoryState(categoryTitle = i18N[ActionCategory.Advanced.i18NKey])
    if (actionItems.isNotEmpty()) {
        ActionCategory(i18N[ActionCategory.Advanced.i18NKey], actionItems.size, categoryState)
    }

    val itemSelected = extActionSelectHandler(addAction = addAction)

    AnimatedVisibility(visible = categoryState.isExpand) {
        SelectorList(
            items = actionItems,
            itemSelected = { it ->
                itemSelected(ActionSelectSource.FromClass(it))
            }
        )
    }
}
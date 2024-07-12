package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.action.BreakActionExecuteScope
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.selector.http.HttpRequestSelectorActivity
import tornaco.apps.shortx.ui.addrule.action.selector.ws.WSConnectSelectorActivity
import tornaco.apps.shortx.ui.addrule.action.selector.ws.WSSendSelectorActivity
import tornaco.apps.shortx.ui.addrule.base.ActionCategory
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.base.rememberCategoryState
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.newIntent
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.code.ParcelableCode
import tornaco.apps.shortx.ui.base.code.ShellCommandEditorActivity
import tornaco.apps.shortx.ui.base.code.js.JSEditorActivity
import tornaco.apps.shortx.ui.base.code.mvel.MVELEditorActivity
import tornaco.apps.shortx.ui.base.code.toParcelableCodeOrNull
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.pkgset.PkgSetListViewModel
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly

@Composable
fun extActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val i18N = LocalI18N.current
    val context = LocalContext.current

    val shellCommandLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.ShellCommand>()
            action?.let {
                addAction(it)
            }
        }
    )

    val delaySelectionState =
        rememberDelayState(
            canUseAlarm = true,
            onSelected = {
                addAction(it)
            })
    DelaySelectorDialog(state = delaySelectionState)

    val mvelEditorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<ParcelableCode>()
            action?.let {
                addAction(Action.MVEL(expression = it.expression))
            }
        }
    )

    val jsEditorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<ParcelableCode>()
            action?.let {
                addAction(Action.ExecuteJS(expression = it.expression))
            }
        }
    )

    val fromDASelectorState = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.FromDA>()
            action?.let {
                addAction(it)
            }
        }
    )

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
            val action = result.popupSelectorResult<Action.IfThenElse>()
            action?.let {
                addAction(it)
            }
        }
    )

    val switchCaseLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.SwitchCase>()
            action?.let {
                addAction(it)
            }
        }
    )

    val waitUtilLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.WaitUtilConditionMatch>()
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
            val action = result.popupSelectorResult<Action.HttpRequest>()
            action?.let {
                addAction(it)
            }
        }
    )

    val wsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.WebSocketConnect>()
            action?.let {
                addAction(it)
            }
        }
    )

    val wsSendLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.WebSocketSend>()
            action?.let {
                addAction(it)
            }
        }
    )

    val brkScopeSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.action.brk.scope"],
        menuItems = BreakActionExecuteScope.entries
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
                shellCommandLauncher.launch(
                    newIntent(
                        context,
                        source.getActionAs<Action.ShellCommand>(),
                        ShellCommandEditorActivity::class.java
                    )
                )
                true
            }

            Action.Delay::class -> {
                delaySelectionState.show(source.getActionAs<Action.Delay>())
                true
            }

            Action.MVEL::class -> {
                mvelEditorLauncher.launch(
                    newIntent(
                        context,
                        source.getActionAs<Action.MVEL>()?.expression?.toParcelableCodeOrNull(),
                        MVELEditorActivity::class.java
                    )
                )
                true
            }

            Action.ExecuteJS::class -> {
                jsEditorLauncher.launch(
                    newIntent(
                        context,
                        source.getActionAs<Action.ExecuteJS>()?.expression.toParcelableCodeOrNull(),
                        JSEditorActivity::class.java
                    )
                )
                true
            }

            Action.Brk::class -> {
                brkScopeSelectorState.show(currentSelection = listOfNotNull(source.getActionAs<Action.Brk>()?.scope?.name))
                true
            }

            Action.FromDA::class -> {
                fromDASelectorState.launch(
                    newIntent(
                        context,
                        source.getActionAs<Action.FromDA>(),
                        FromDASelectorActivity::class.java
                    )
                )
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

            Action.SwitchCase::class -> {
                switchCaseLauncher.launch(
                    newIntent(
                        context,
                        source.getActionAs<Action.SwitchCase>(),
                        SwitchCaseSelectorActivity::class.java
                    )
                )
                true
            }

            Action.WaitUtilConditionMatch::class -> {
                waitUtilLauncher.launch(
                    newIntent(
                        context,
                        source.getActionAs<Action.WaitUtilConditionMatch>(),
                        WaitUtilConditionMatchSelectorActivity::class.java
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

            Action.WebSocketConnect::class -> {
                wsLauncher.launch(
                    newIntent(
                        context,
                        source.getActionAs<Action.WebSocketConnect>(),
                        WSConnectSelectorActivity::class.java
                    )
                )
                true
            }

            Action.WebSocketSend::class -> {
                wsSendLauncher.launch(
                    newIntent(
                        context,
                        source.getActionAs<Action.WebSocketSend>(),
                        WSSendSelectorActivity::class.java
                    )
                )
                true
            }

            Action.FindImagePositionFromScreen::class -> {
                addAction(
                    Action.FindImagePositionFromScreen(
                        imageBase64 = testData
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
fun ExtActions(
    keyword: String,
    subscribeState: LVLStateHolder.State,
    selectedFilter: ActionCategory,
    addAction: (Action) -> Unit
) {
    val i18N = LocalI18N.current
    val actionItems = listOf(
        Action.FromDA::class,
        Action.SetRuleEnabled::class,
        Action.ShellCommand::class,
        Action.Delay::class,
        Action.MVEL::class,
        Action.Brk::class,
        Action.CreatePkgSet::class,
        Action.IfThenElse::class,
        Action.SwitchCase::class,
        Action.WhileLoop::class,
        Action.WaitUtilConditionMatch::class,
        Action.HttpRequest::class,
        Action.WebSocketConnect::class,
        Action.WebSocketSend::class,
        Action.ExecuteJS::class,
        Action.FindImagePositionFromScreen::class,
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
    if (actionItems.isNotEmpty() && selectedFilter == ActionCategory.All) {
        ActionCategory(i18N[ActionCategory.Advanced.i18NKey], actionItems.size, categoryState)
    }

    val itemSelected = extActionSelectHandler(addAction = addAction)

    val isVisible =
        (categoryState.isExpand && selectedFilter == ActionCategory.All) || selectedFilter == ActionCategory.Advanced
    AnimatedVisibility(visible = isVisible) {
        SelectorList(
            items = actionItems,
            itemSelected = { it ->
                itemSelected(ActionSelectSource.FromClass(it))
            }
        )
    }
}


private val testData = """
 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
""".trimIndent()
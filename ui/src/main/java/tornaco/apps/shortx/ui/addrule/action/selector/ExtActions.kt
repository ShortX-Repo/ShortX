package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.proto.action.BreakActionExecuteScope
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.selector.cv.FindColorSelectorActivity
import tornaco.apps.shortx.ui.addrule.action.selector.cv.FindImageSelectorActivity
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
import tornaco.apps.shortx.ui.config.config
import tornaco.apps.shortx.ui.pkgset.PkgSetListViewModel
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly

@Composable
fun extActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val i18N = LocalI18N.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    val ocrDetectLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.OcrDetect>()
            action?.let {
                addAction(it)
            }
        }
    )

    val findImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.FindImagePosition>()
            action?.let {
                addAction(it)
            }
        }
    )

    val findColorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.FindPointsByColor>()
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
            val bs = BreakActionExecuteScope.valueOf(id)
            addAction(Action.Brk(scope = bs))
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

            Action.FindImagePosition::class -> {
                findImageLauncher.launch(
                    newIntent(
                        context,
                        source.getActionAs<Action.FindImagePosition>(),
                        FindImageSelectorActivity::class.java
                    )
                )
                true
            }

            Action.FindPointsByColor::class -> {
                findColorLauncher.launch(
                    newIntent(
                        context,
                        source.getActionAs<Action.FindPointsByColor>(),
                        FindColorSelectorActivity::class.java
                    )
                )
                true
            }

            Action.OcrDetect::class -> {
                ocrDetectLauncher.launch(
                    newIntent(
                        context,
                        source.getActionAs<Action.OcrDetect>(),
                        OCRSelectorActivity::class.java
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
    val actionItems = listOfNotNull(
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
        if (config.isFeatureCVFindImageEnabled || BuildPropInternal.APP_IS_DEBUG) {
            Action.FindImagePosition::class
        } else {
            null
        },
        Action.FindPointsByColor::class,
        Action.OcrDetect::class,
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
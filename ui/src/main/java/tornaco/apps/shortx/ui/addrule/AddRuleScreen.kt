@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)

package tornaco.apps.shortx.ui.addrule

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.rule.AsyncMode
import tornaco.apps.shortx.core.proto.rule.Quit_EvaluatePolicy
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addda.FuncParamList
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.ActionContentState
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.condition.ConditionContent
import tornaco.apps.shortx.ui.addrule.condition.ConditionContentState
import tornaco.apps.shortx.ui.addrule.condition.ConditionContentStateHandler
import tornaco.apps.shortx.ui.addrule.condition.asConditionHandler
import tornaco.apps.shortx.ui.addrule.fact.FactContentState
import tornaco.apps.shortx.ui.addrule.fact.FactContentStateHandler
import tornaco.apps.shortx.ui.addrule.fact.FactSelectors
import tornaco.apps.shortx.ui.addrule.fact.asFactHandler
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.DropdownButtonLayout
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.ErrorCard
import tornaco.apps.shortx.ui.base.ExpandableSectionBar
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.FieldTitleWithTip
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.MediumTitleWithTip
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SECTION_BAR_CORNER_SIZE
import tornaco.apps.shortx.ui.base.SECTION_BAR_H_PADDING
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchBar
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.WarningCard
import tornaco.apps.shortx.ui.base.handleSideEffect
import tornaco.apps.shortx.ui.base.itemWithPadding
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.record.EvaRecordSetting
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry

@AndroidEntryPoint
class AddRuleActivity : BaseActivity() {
    companion object {
        private const val KEY_RULE_ID = "ruleId"
        private const val KEY_RULE_SET_ID = "ruleSetId"

        @JvmStatic
        fun edit(context: Context, ruleId: String) {
            val starter = Intent(context, AddRuleActivity::class.java)
                .putExtra(KEY_RULE_ID, ruleId)
            context.startActivity(starter)
        }

        @JvmStatic
        fun create(context: Context, ruleSetId: String? = null) {
            val starter = Intent(context, AddRuleActivity::class.java)
                .putExtra(KEY_RULE_SET_ID, ruleSetId)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        val ruleId = intent.getStringExtra(KEY_RULE_ID)
        val ruleSetId = intent.getStringExtra(KEY_RULE_SET_ID)
        AddRuleScreen(
            ruleId = ruleId,
            ruleSetId = ruleSetId,
            screenTitle = if (ruleId != null) {
                LocalI18N.current["ui.title.edit.rule"]
            } else {
                LocalI18N.current["ui.title.add.rule"]
            }
        ) {
            finish()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleScreen(
    ruleId: String? = null,
    ruleSetId: String? = null,
    rule: Rule? = null,
    url: String? = null,
    screenTitle: String,
    canSaveWithoutChanges: Boolean = false,
    readOnly: Boolean = false,
    saveIconName: String = Remix.System.check_line,
    back: (Boolean) -> Unit
) {
    val vm = hiltViewModel<AddRuleViewModel>()
    val addRuleState by vm.state.collectAsState()

    val context = LocalContext.current
    val i18n = LocalI18N.current
    val savedMessage = i18n["ui.toast.rule.saved"]
    val showRuleSavedToast = {
        Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
    }
    val showParamAlreadyExistsToast: (String) -> Unit = {
        Toast.makeText(context, "$it already exists", Toast.LENGTH_SHORT).show()
    }

    val actionHandler = remember(vm) { vm.actionContentStateHandler }
    val actionContentState by actionHandler.state.collectAsState()

    val actionOnEnabledHandler = remember(vm) { vm.actionOnEnabledStateHandler }
    val actionOnEnabledState by actionOnEnabledHandler.state.collectAsState()

    val actionOnDisabledHandler = remember(vm) { vm.actionOnDisabledStateHandler }
    val actionOnDisabledState by actionOnDisabledHandler.state.collectAsState()

    val actionOnDeletedHandler = remember(vm) { vm.actionOnDeletedStateHandler }
    val actionOnDeletedState by actionOnDeletedHandler.state.collectAsState()

    val actionOnStoppedHandler = remember(vm) { vm.actionOnStoppedStateHandler }
    val actionOnStoppedState by actionOnStoppedHandler.state.collectAsState()

    val actionOnUpdatedHandler = remember(vm) { vm.actionOnUpdatedStateHandler }
    val actionOnUpdatedState by actionOnUpdatedHandler.state.collectAsState()

    val conditionHandler = remember(vm) { vm.conditionContentStateHandler }
    val conditionContentState by conditionHandler.state.collectAsState()

    val factHandler = remember(vm) { vm.factContentStateHandler }
    val factContentState by factHandler.state.collectAsState()

    val quitFactHandler = remember(vm) { vm.quitFactContentStateHandler }
    val quitFactContentState by quitFactHandler.state.collectAsState()
    val quitConditionHandler = remember(vm) { vm.quitConditionContentStateHandler }
    val quitConditionContentState by quitConditionHandler.state.collectAsState()
    val quitActionHandler = remember(vm) { vm.quitActionStateHandler }
    val quitActionState by quitActionHandler.state.collectAsState()

    val shortXProps = LocalShortXProps.current

    LaunchedEffect(vm) {
        vm.initRuleSetIdIfNonNull(ruleSetId)
        vm.initFromRuleId(ruleId)
        vm.initFromRule(rule)
        vm.initFromUrl(url)
    }

    vm.handleSideEffect {
        when (it) {
            is AddRuleEffect.AddParamFailNameExists -> {
                showParamAlreadyExistsToast(it.name)
            }

            is AddRuleEffect.RuleSaved -> {
                showRuleSavedToast()
            }
        }
    }

    val backConfirmDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = i18n["ui.edit.discard.dialog.title"],
        messageHint = {
            i18n["ui.edit.discard.dialog.message"]
        },
        confirmButton = i18n["ui.edit.discard.dialog.discard"],
        dismissButton = i18n["ui.edit.discard.dialog.stay"],
        state = backConfirmDialogState,
        data = "",
        onConfirm = {
            back(false)
        })

    val hasAnyChanges = remember(
        addRuleState,
        factContentState,
        actionContentState,
        conditionContentState,
        actionOnEnabledState,
        actionOnDisabledState,
        actionOnDeletedState,
        quitFactContentState,
        quitConditionContentState,
        quitActionState
    ) {
        addRuleState.ruleChanged
                || factContentState.isChanged
                || actionContentState.isChanged
                || conditionContentState.isChanged
                || actionOnEnabledState.isChanged
                || actionOnDisabledState.isChanged
                || actionOnDeletedState.isChanged
                || quitFactContentState.isChanged
                || quitConditionContentState.isChanged
                || quitActionState.isChanged
    }

    BackHandler {
        if (hasAnyChanges) {
            backConfirmDialogState.show()
        } else {
            back(false)
        }
    }

    ShortXAppBarScaffold(
        title = screenTitle,
        onBackPressed = {
            if (hasAnyChanges) {
                backConfirmDialogState.show()
            } else {
                back(false)
            }
        },
        actions = {
            IconButton(
                enabled = canSaveWithoutChanges || hasAnyChanges,
                onClick = {
                    vm.save(shortXProps.firstOrNull())
                    back(true)
                }) {
                RemixIcon(remixName = saveIconName)
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        verticalScroll = false
    ) {
        when (addRuleState.onlineDALoadState) {
            is LoadState.Error -> {
                LoadError(modifier = Modifier.fillMaxSize()) {
                    vm.initFromUrl(url)
                }
            }

            LoadState.Loading -> {
                Loading(modifier = Modifier.fillMaxSize())
            }

            is LoadState.Success -> {
                val lazyListState = rememberLazyListState()
                // 监听第一个可见项的索引
                val currentStickyHeaderIndex by remember {
                    derivedStateOf {
                        // 判断当前 sticky header 的索引（可能是第一个可见项）
                        lazyListState.firstVisibleItemIndex
                    }
                }
                LazyColumn(state = lazyListState) {
                    ruleContent(
                        currentStickyHeaderIndex = currentStickyHeaderIndex,
                        addRuleState = addRuleState,
                        vm = vm,
                        readOnly = readOnly,
                        factContentState = factContentState,
                        factHandler = factHandler,
                        conditionContentState = conditionContentState,
                        conditionHandler = conditionHandler,
                        actionContentState = actionContentState,
                        actionHandler = actionHandler,
                        quitFactContentState = quitFactContentState,
                        quitFactHandler = quitFactHandler,
                        quitConditionContentState = quitConditionContentState,
                        quitConditionHandler = quitConditionHandler,
                        quitActionState = quitActionState,
                        quitActionHandler = quitActionHandler,
                        actionOnEnabledState = actionOnEnabledState,
                        actionOnEnabledHandler = actionOnEnabledHandler,
                        actionOnDisabledState = actionOnDisabledState,
                        actionOnDisabledHandler = actionOnDisabledHandler,
                        actionOnDeletedState = actionOnDeletedState,
                        actionOnDeletedHandler = actionOnDeletedHandler,
                        actionOnStoppedState = actionOnStoppedState,
                        actionOnStoppedHandler = actionOnStoppedHandler,
                        actionOnUpdatedState = actionOnUpdatedState,
                        actionOnUpdatedHandler = actionOnUpdatedHandler
                    )
                }
            }
        }
    }
}

private fun LazyListScope.ruleContent(
    currentStickyHeaderIndex: Int,
    addRuleState: AddRuleState,
    vm: AddRuleViewModel,
    readOnly: Boolean,
    factContentState: FactContentState,
    factHandler: FactContentStateHandler,
    conditionContentState: ConditionContentState,
    conditionHandler: ConditionContentStateHandler,
    actionContentState: ActionContentState,
    actionHandler: ActionContentStateHandler,
    quitFactContentState: FactContentState,
    quitFactHandler: FactContentStateHandler,
    quitConditionContentState: ConditionContentState,
    quitConditionHandler: ConditionContentStateHandler,
    quitActionState: ActionContentState,
    quitActionHandler: ActionContentStateHandler,
    actionOnEnabledState: ActionContentState,
    actionOnEnabledHandler: ActionContentStateHandler,
    actionOnDisabledState: ActionContentState,
    actionOnDisabledHandler: ActionContentStateHandler,
    actionOnDeletedState: ActionContentState,
    actionOnDeletedHandler: ActionContentStateHandler,
    actionOnStoppedState: ActionContentState,
    actionOnStoppedHandler: ActionContentStateHandler,
    actionOnUpdatedState: ActionContentState,
    actionOnUpdatedHandler: ActionContentStateHandler
) {
    // 0
    itemWithPadding {
        AnimatedVisibility(visible = !addRuleState.securityCheckRes) {
            Column {
                ErrorCard(
                    modifier = Modifier
                        .padding(vertical = 16.dp),
                    iconName = Remix.UserAndFaces.skull_2_fill,
                    title = LocalI18N.current["ui.rule.action.secure.warning.title"],
                    warnings = listOf(LocalI18N.current["ui.rule.action.secure.warning.message"])
                )
                LargeSpacer()
            }
        }
    }

    // 1
    itemWithPadding {
        AnimatedVisibility(visible = addRuleState.hasSMSAction) {
            Column {
                WarningCard(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = LocalI18N.current["ui.rule.contains.send.sms.message"],
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
                StandardSpacer()
            }
        }
    }

    // 2
    itemWithPadding {
        TitleAndDescription(state = addRuleState, vm = vm, readOnly = readOnly)
        SectionSpacer()
    }

    // 3
    itemWithPadding {
        FactSelectors(
            state = factContentState,
            handler = factHandler.asFactHandler(),
            readOnly = readOnly
        )
        SectionSpacer()
    }

    // 4
    itemWithPadding {
        ConditionContent(
            state = conditionContentState,
            readOnly = readOnly,
            handler = conditionHandler.asConditionHandler()
        )
        SectionSpacer()
    }

    // 5
    itemWithPadding {
        ActionContent(
            srcId = addRuleState.id,
            actions = actionContentState.actions,
            actionAsyncMode = actionContentState.asyncMode,
            canSelectActionAsyncMode = true,
            evaluatingActions = addRuleState.evaluatingActions,
            handler = actionHandler.asActionHandler(),
            readOnly = readOnly,
            warnings = actionContentState.actionWarningMessages
        )
        SectionSpacer()
    }

    // 6
    itemWithPadding {
        EvaRecordSetting(id = addRuleState.id)
        SectionSpacer()
    }

    // 7
    itemWithPadding {
        AnimatedVisibility(
            addRuleState.parameters.isNotEmpty()
                    && !addRuleState.showAdvancedOptions
        ) {
            Column {
                FuncParamList(
                    params = addRuleState.parameters,
                    add = { param, override ->
                        vm.addParameter(param, override)
                    },
                    remove = {
                        vm.removeParameter(it)
                    })
                SectionSpacer()
            }
        }
        StandardSpacer()
    }


    // Adv
    // 8
    stickyHeader {
        val cornerDp by animateDpAsState(
            if (currentStickyHeaderIndex >= 8) 0.dp else SECTION_BAR_CORNER_SIZE.dp,
            label = "Dp"
        )
        val paddingDp by animateDpAsState(
            if (currentStickyHeaderIndex >= 8) 0.dp else SECTION_BAR_H_PADDING.dp,
            label = "Dp"
        )
        ExpandableSectionBar(
            isExpand = addRuleState.showAdvancedOptions,
            text = LocalI18N.current["ui.advanced.options"],
            iconName = Remix.System.star_fill,
            shape = RoundedCornerShape(cornerDp),
            modifier = Modifier.padding(horizontal = paddingDp)
        ) {
            vm.setShowAdvancedOptions(!addRuleState.showAdvancedOptions)
        }
        StandardSpacer()
    }
    // 9
    itemWithPadding {
        AnimatedVisibility(visible = addRuleState.showAdvancedOptions) {
            Column {
                // Async Mode
                MediumTitleWithTip(
                    text = LocalI18N.current["ui.rule.async.mode"],
                    tipText = LocalI18N.current["ui.rule.async.mode.description"]
                )
                SmallSpacer()

                // AsyncMode
                val asyncModeDropdownState = rememberDropdownSelectorState()
                Box(modifier = Modifier.fillMaxWidth()) {
                    DropdownButtonLayout(
                        LocalI18N.current[addRuleState.asyncMode.labelKey()],
                        asyncModeDropdownState
                    )
                    DropdownSelector(
                        state = asyncModeDropdownState,
                        items = AsyncMode.entries
                            .filterNot { it == AsyncMode.UNRECOGNIZED }.map {
                                DropdownItem(
                                    labelLines = listOf(
                                        LocalI18N.current[it.labelKey()]
                                    ),
                                    data = it
                                )
                            },
                        onSelect = {
                            vm.setAsyncMode(it.data)
                        })
                }
                StandardSpacer()

                // Conflict
                ConflictStrategy(addRuleState.conflictStrategy) {
                    vm.setConflictStrategy(it)
                }
                StandardSpacer()

                FuncParamList(
                    params = addRuleState.parameters,
                    add = { param, override ->
                        vm.addParameter(param, override)
                    },
                    remove = {
                        vm.removeParameter(it)
                    })
                SectionSpacer()
            }
        }
    }

    // Quit START
    // 10
    stickyHeader {
        val cornerDp by animateDpAsState(
            if (currentStickyHeaderIndex >= 10) 0.dp else SECTION_BAR_CORNER_SIZE.dp,
            label = "Dp"
        )
        val paddingDp by animateDpAsState(
            if (currentStickyHeaderIndex >= 10) 0.dp else SECTION_BAR_H_PADDING.dp,
            label = "Dp"
        )
        SwitchBar(
            isChecked = addRuleState.isQuitEnabled,
            text = LocalI18N.current["ui.rule.quit.title"],
            iconName = Remix.System.logout_circle_fill,
            cornerSize = cornerDp,
            modifier = Modifier.padding(horizontal = paddingDp),
            onCheckChange = {
                vm.toggleQuitEnable()
            }
        )
        StandardSpacer()
    }
    // 11
    itemWithPadding {
        AnimatedVisibility(visible = addRuleState.isQuitEnabled) {
            Column {
                StandardSpacer()
                TipBody(LocalI18N.current["ui.rule.quit.description"])
                StandardSpacer()
                // Policy
                QuitEvaluatePolicy(addRuleState.quitEvaluatePolicy) {
                    vm.setQuitEvaluatePolicy(it)
                }

                StandardSpacer()
                FactSelectors(
                    header = {
                        FieldTitle(
                            modifier = Modifier.padding(bottom = 12.dp),
                            text = "${LocalI18N.current["ui.rule.quit.title"]} - ${LocalI18N.current["ui.fact.title"]}"
                        )
                    },
                    state = quitFactContentState,
                    handler = quitFactHandler.asFactHandler(),
                    readOnly = readOnly
                )
                SectionSpacer()

                ConditionContent(
                    header = {
                        FieldTitle(
                            modifier = Modifier.padding(bottom = 12.dp),
                            text = "${LocalI18N.current["ui.rule.quit.title"]} - ${LocalI18N.current["ui.conditions.title"]}"
                        )
                    },
                    state = quitConditionContentState,
                    readOnly = readOnly,
                    handler = quitConditionHandler.asConditionHandler()
                )

                SectionSpacer()

                ActionContent(
                    header = {
                        FieldTitle(
                            modifier = Modifier.padding(bottom = 6.dp),
                            text = "${LocalI18N.current["ui.rule.quit.title"]} - ${LocalI18N.current["ui.actions.title"]}"
                        )
                    },
                    summary = {
                        TipBody(text = LocalI18N.current["ui.rule.quit.action.description"])
                    },
                    srcId = addRuleState.id,
                    actions = quitActionState.actions,
                    actionAsyncMode = quitActionState.asyncMode,
                    canSelectActionAsyncMode = true,
                    evaluatingActions = addRuleState.evaluatingActions,
                    warnings = emptyMap(),
                    handler = quitActionHandler.asActionHandler()
                )

                SectionSpacer()
            }
        }
    }
    // Quit END

    // Hooks START
    // 12
    stickyHeader {
        val cornerDp by animateDpAsState(
            if (currentStickyHeaderIndex >= 12) 0.dp else SECTION_BAR_CORNER_SIZE.dp,
            label = "Dp"
        )
        val paddingDp by animateDpAsState(
            if (currentStickyHeaderIndex >= 12) 0.dp else SECTION_BAR_H_PADDING.dp,
            label = "Dp"
        )
        // Hooks
        ExpandableSectionBar(
            isExpand = addRuleState.expandHooks,
            text = "Hooks",
            iconName = Remix.Document.file_list_fill,
            shape = RoundedCornerShape(cornerDp),
            modifier = Modifier.padding(horizontal = paddingDp)
        ) {
            vm.setExpandHooks(!addRuleState.expandHooks)
        }
        StandardSpacer()
    }
    // 13
    itemWithPadding {
        AnimatedVisibility(addRuleState.expandHooks) {
            Column {
                ActionContent(
                    header = {
                        FieldTitleWithTip(
                            LocalI18N.current["ui.actions.hook.on.enabled.title"],
                            LocalI18N.current["ui.actions.hook.on.enabled.description"]
                        )
                    },
                    srcId = addRuleState.id,
                    actions = actionOnEnabledState.actions,
                    actionAsyncMode = actionOnEnabledState.asyncMode,
                    canSelectActionAsyncMode = true,
                    evaluatingActions = addRuleState.evaluatingActions,
                    handler = actionOnEnabledHandler.asActionHandler(),
                    readOnly = readOnly,
                    warnings = actionOnEnabledState.actionWarningMessages
                )
                SectionSpacer()

                ActionContent(
                    header = {
                        FieldTitleWithTip(
                            LocalI18N.current["ui.actions.hook.on.disabled.title"],
                            LocalI18N.current["ui.actions.hook.on.disabled.description"]
                        )
                    },
                    srcId = addRuleState.id,
                    actions = actionOnDisabledState.actions,
                    actionAsyncMode = actionOnDisabledState.asyncMode,
                    canSelectActionAsyncMode = true,
                    evaluatingActions = addRuleState.evaluatingActions,
                    handler = actionOnDisabledHandler.asActionHandler(),
                    readOnly = readOnly,
                    warnings = actionOnDisabledState.actionWarningMessages
                )
                SectionSpacer()

                ActionContent(
                    header = {
                        FieldTitleWithTip(
                            LocalI18N.current["ui.actions.hook.on.deleted.title"],
                            LocalI18N.current["ui.actions.hook.on.deleted.description"]
                        )
                    },
                    srcId = addRuleState.id,
                    actions = actionOnDeletedState.actions,
                    actionAsyncMode = actionOnDeletedState.asyncMode,
                    canSelectActionAsyncMode = true,
                    evaluatingActions = addRuleState.evaluatingActions,
                    handler = actionOnDeletedHandler.asActionHandler(),
                    readOnly = readOnly,
                    warnings = actionOnDeletedState.actionWarningMessages
                )
                SectionSpacer()

                ActionContent(
                    header = {
                        FieldTitleWithTip(
                            LocalI18N.current["ui.actions.hook.on.stopped.title"],
                            LocalI18N.current["ui.actions.hook.on.stopped.description"]
                        )
                    },
                    srcId = addRuleState.id,
                    actions = actionOnStoppedState.actions,
                    actionAsyncMode = actionOnStoppedState.asyncMode,
                    canSelectActionAsyncMode = true,
                    evaluatingActions = addRuleState.evaluatingActions,
                    handler = actionOnStoppedHandler.asActionHandler(),
                    readOnly = readOnly,
                    warnings = actionOnStoppedState.actionWarningMessages
                )
                SectionSpacer()

                ActionContent(
                    header = {
                        FieldTitleWithTip(
                            LocalI18N.current["ui.actions.hook.on.updated.title"],
                            LocalI18N.current["ui.actions.hook.on.updated.description"]
                        )
                    },
                    srcId = addRuleState.id,
                    actions = actionOnUpdatedState.actions,
                    actionAsyncMode = actionOnUpdatedState.asyncMode,
                    canSelectActionAsyncMode = true,
                    evaluatingActions = addRuleState.evaluatingActions,
                    handler = actionOnUpdatedHandler.asActionHandler(),
                    readOnly = readOnly,
                    warnings = actionOnUpdatedState.actionWarningMessages
                )
                SectionSpacer()
            }
        }
    }
    // Hooks END

    // GEEKS START
    // 14
    stickyHeader {
        ExpandableSectionBar(
            isExpand = addRuleState.showGeeksOptions,
            text = LocalI18N.current["ui.geeks.options"],
            iconName = Remix.UserAndFaces.aliens_fill,
            modifier = Modifier.padding(horizontal = SECTION_BAR_H_PADDING.dp)
        ) {
            vm.setShowGeeksOptions(!addRuleState.showGeeksOptions)
        }
        StandardSpacer()
    }
    itemWithPadding {
        AnimatedVisibility(visible = addRuleState.showGeeksOptions) {
            Column {
                RuleInstanceIdGenSelector(addRuleState.instanceIdGen, readOnly) {
                    vm.setInsGen(it)
                }
            }
        }
    }
    // GEEKS END

    itemWithPadding {
        SectionSpacer()
        SuggestedNavEntry()
    }
}

@Composable
fun ConflictStrategy(
    conflictStrategy: ConflictStrategy,
    setConflictStrategy: (ConflictStrategy) -> Unit
) {
    MediumTitleWithTip(
        text = LocalI18N.current["ui.rule.conflict.strategy"],
        tipText = LocalI18N.current["ui.rule.conflict.strategy.description"]
    )
    SmallSpacer()

    val conflictStrategyModeDropdownState = rememberDropdownSelectorState()
    Box(modifier = Modifier.fillMaxWidth()) {
        DropdownButtonLayout(
            LocalI18N.current[conflictStrategy.labelKey()],
            conflictStrategyModeDropdownState
        )
        DropdownSelector(
            state = conflictStrategyModeDropdownState,
            items = ConflictStrategy.entries
                .filterNot { it == ConflictStrategy.UNRECOGNIZED }.map {
                    DropdownItem(
                        labelLines = listOf(
                            LocalI18N.current[it.labelKey()]
                        ),
                        data = it
                    )
                },
            onSelect = {
                setConflictStrategy(it.data)
            })
    }
}

@Composable
fun QuitEvaluatePolicy(
    policy: Quit_EvaluatePolicy,
    setPolicy: (Quit_EvaluatePolicy) -> Unit
) {
    FieldTitle(text = LocalI18N.current["ui.rule.quit.eva.policy"])
    SmallSpacer()

    val dropState = rememberDropdownSelectorState()
    Box(modifier = Modifier.fillMaxWidth()) {
        DropdownButtonLayout(
            LocalI18N.current["ui.rule.quit.eva.policy.${policy.name.lowercase()}"],
            dropState
        )
        DropdownSelector(
            state = dropState,
            items = Quit_EvaluatePolicy.entries
                .filterNot { it == Quit_EvaluatePolicy.UNRECOGNIZED }.map {
                    DropdownItem(
                        labelLines = listOf(
                            LocalI18N.current["ui.rule.quit.eva.policy.${it.name.lowercase()}"],
                            LocalI18N.current["ui.rule.quit.eva.policy.description.${it.name.lowercase()}"]
                        ),
                        data = it
                    )
                },
            onSelect = {
                setPolicy(it.data)
            })
    }
}

@Composable
private fun TitleAndDescription(
    state: AddRuleState,
    vm: AddRuleViewModel,
    readOnly: Boolean
) {
    var showDescription by remember(state) { mutableStateOf(state.description.isNotEmpty()) }
    val dropdownRotate by animateFloatAsState(
        targetValue = if (showDescription) 180f else 0f,
        label = "dropdownRotate"
    )

    ShortXTextField(
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = LocalI18N.current["ui.rule.title"])
        },
        value = state.title,
        readOnly = readOnly,
        showSymbolButton = false,
        trailingIcon = {
            IconButton(onClick = { showDescription = !showDescription }) {
                RemixIcon(
                    modifier = Modifier.rotate(dropdownRotate),
                    remixName = Remix.Arrows.arrow_drop_down_fill
                )
            }
        },
        onValueChange = { title ->
            vm.setTitle(title)
        })
    AnimatedVisibility(visible = showDescription) {
        ShortXTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            label = {
                Text(text = LocalI18N.current["ui.rule.description"])
            },
            value = state.description,
            readOnly = readOnly,
            showSymbolButton = true,
            onValueChange = { description ->
                vm.setDescription(description)
            })
    }
}

fun AsyncMode.labelKey(): String {
    return when (this) {
        AsyncMode.AsyncMode_Async -> "ui.rule.async.mode.async"
        AsyncMode.AsyncMode_Sync -> "ui.rule.async.mode.sync"
        AsyncMode.AsyncMode_Sync_InRule -> "ui.rule.async.mode.sync_in_rule"
        AsyncMode.UNRECOGNIZED -> ""
    }
}

fun ConflictStrategy.labelKey(): String {
    return when (this) {
        ConflictStrategy.ConflictStrategy_ExecuteBoth -> {
            "ui.rule.conflict.strategy.both"
        }

        ConflictStrategy.ConflictStrategy_ReplaceOld -> {
            "ui.rule.conflict.strategy.replace"
        }

        ConflictStrategy.ConflictStrategy_SkipNew -> {
            "ui.rule.conflict.strategy.skip"
        }

        ConflictStrategy.UNRECOGNIZED -> {
            ""
        }
    }
}
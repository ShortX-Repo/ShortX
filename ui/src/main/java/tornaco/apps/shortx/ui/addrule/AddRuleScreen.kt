package tornaco.apps.shortx.ui.addrule

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.proto.rule.AsyncMode
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.res.Remix
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
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.FieldTitleWithTip
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.MediumTitleWithTip
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.SplitLines
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class AddRuleActivity : BaseActivity() {
    companion object {
        private const val KEY_RULE_ID = "ruleId"

        @JvmStatic
        fun start(context: Context, ruleId: String?) {
            val starter = Intent(context, AddRuleActivity::class.java)
                .putExtra(KEY_RULE_ID, ruleId)
            context.startActivity(starter)
        }

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, AddRuleActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        val ruleId = intent.getStringExtra(KEY_RULE_ID)
        AddRuleScreen(
            ruleId = ruleId,
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
    val savedMessage = LocalI18N.current["ui.toast.rule.saved"]
    val showToast = {
        Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
    }

    val actionHandler = remember(vm) { vm.actionContentStateHandler }
    val actionContentState by actionHandler.state.collectAsState()

    val actionOnEnabledHandler = remember(vm) { vm.actionOnEnabledStateHandler }
    val actionOnEnabledState by actionOnEnabledHandler.state.collectAsState()

    val actionOnDisabledHandler = remember(vm) { vm.actionOnDisabledStateHandler }
    val actionOnDisabledState by actionOnDisabledHandler.state.collectAsState()

    val actionOnDeletedHandler = remember(vm) { vm.actionOnDeletedStateHandler }
    val actionOnDeletedState by actionOnDeletedHandler.state.collectAsState()

    val conditionHandler = remember(vm) { vm.conditionContentStateHandler }
    val conditionContentState by conditionHandler.state.collectAsState()

    val factHandler = remember(vm) { vm.factContentStateHandler }
    val factContentState by factHandler.state.collectAsState()

    val quitFactHandler = remember(vm) { vm.quitFactContentStateHandler }
    val quitFactContentState by quitFactHandler.state.collectAsState()
    val quitConditionHandler = remember(vm) { vm.quitConditionContentStateHandler }
    val quitConditionContentState by quitConditionHandler.state.collectAsState()


    LaunchedEffect(vm) {
        vm.initFromRuleId(ruleId)
        vm.initFromRule(rule)
        vm.initFromUrl(url)
        vm.ruleSaved.collectLatest {
            showToast()
        }
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    ShortXMediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = screenTitle) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back(false)
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(
                                enabled = canSaveWithoutChanges
                                        || addRuleState.ruleChanged
                                        || factContentState.isChanged
                                        || actionContentState.isChanged
                                        || conditionContentState.isChanged
                                        || actionOnEnabledState.isChanged
                                        || actionOnDisabledState.isChanged
                                        || actionOnDeletedState.isChanged
                                        || quitFactContentState.isChanged
                                        || quitConditionContentState.isChanged,
                                onClick = {
                                    vm.save()
                                    back(true)
                                }) {
                                RemixIcon(remixName = saveIconName)
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }) { paddingValues ->
                val scrollState = rememberScrollState()
                ClickOutsideClearFocusBox {
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                            .animateContentSize()
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
                                RuleContent(
                                    addRuleState,
                                    vm,
                                    readOnly,
                                    factContentState,
                                    factHandler,
                                    conditionContentState,
                                    conditionHandler,
                                    actionContentState,
                                    actionHandler,
                                    quitFactContentState,
                                    quitFactHandler,
                                    quitConditionContentState,
                                    quitConditionHandler,
                                    actionOnEnabledState,
                                    actionOnEnabledHandler,
                                    actionOnDisabledState,
                                    actionOnDisabledHandler,
                                    actionOnDeletedState,
                                    actionOnDeletedHandler
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RuleContent(
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
    actionOnEnabledState: ActionContentState,
    actionOnEnabledHandler: ActionContentStateHandler,
    actionOnDisabledState: ActionContentState,
    actionOnDisabledHandler: ActionContentStateHandler,
    actionOnDeletedState: ActionContentState,
    actionOnDeletedHandler: ActionContentStateHandler
) {
    TitleAndDescription(state = addRuleState, vm = vm, readOnly = readOnly)
    SectionSpacer()

    FactSelectors(
        state = factContentState,
        handler = factHandler.asFactHandler(),
        readOnly = readOnly
    )
    SectionSpacer()

    ConditionContent(
        state = conditionContentState,
        readOnly = readOnly,
        handler = conditionHandler.asConditionHandler()
    )
    SectionSpacer()

    ActionContent(
        actions = actionContentState.actions,
        handler = actionHandler.asActionHandler(),
        readOnly = readOnly,
        warnings = actionContentState.actionWarningMessages
    )
    SectionSpacer()

    TextButton(onClick = {
        vm.setShowAdvancedOptions(!addRuleState.showAdvancedOptions)

    }) {
        Text(text = LocalI18N.current["ui.advanced.options"])
    }
    StandardSpacer()
    AnimatedVisibility(visible = addRuleState.showAdvancedOptions) {
        Column {
            // Async Mode
            MediumTitleWithTip(
                text = LocalI18N.current["ui.rule.async.mode"],
                tipText = LocalI18N.current["ui.rule.async.mode.description"]
            )
            SmallSpacer()

            val typeDropdownState = rememberDropdownSelectorState()
            Box(modifier = Modifier.fillMaxWidth()) {
                FilledTonalButton(onClick = {
                    typeDropdownState.open()
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = LocalI18N.current[addRuleState.asyncMode.labelKey()])
                        StandardSpacer()
                        RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                    }
                }
                DropdownSelector(
                    state = typeDropdownState,
                    items = AsyncMode.values()
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
            SplitLines(modifier = Modifier.padding(vertical = 30.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MediumTitleWithTip(
                    LocalI18N.current["ui.rule.quit.title"],
                    LocalI18N.current["ui.rule.quit.description"]
                )
                Switch(checked = addRuleState.isQuitEnabled, onCheckedChange = {
                    vm.toggleQuitEnable()
                })
            }
            AnimatedVisibility(visible = addRuleState.isQuitEnabled) {
                Column {
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
                }
            }

            SplitLines(modifier = Modifier.padding(vertical = 30.dp))

            MediumTitle(text = "Hooks")
            SmallSpacer()
            ActionContent(
                header = {
                    FieldTitleWithTip(
                        LocalI18N.current["ui.actions.hook.on.enabled.title"],
                        LocalI18N.current["ui.actions.hook.on.enabled.description"]
                    )
                },
                actions = actionOnEnabledState.actions,
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
                actions = actionOnDisabledState.actions,
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
                actions = actionOnDeletedState.actions,
                handler = actionOnDeletedHandler.asActionHandler(),
                readOnly = readOnly,
                warnings = actionOnDeletedState.actionWarningMessages
            )
            SectionSpacer()
        }
    }

    SectionSpacer()
    SuggestedNavEntry()
}

@Composable
private fun TitleAndDescription(
    state: AddRuleState,
    vm: AddRuleViewModel,
    readOnly: Boolean
) {
    ShortXTextField(
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = LocalI18N.current["ui.rule.title"])
        },
        value = state.title,
        readOnly = readOnly,
        showSymbolButton = false,
        onValueChange = { title ->
            vm.setTitle(title)
        })
    Spacer(modifier = Modifier.size(16.dp))
    ShortXTextField(modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = LocalI18N.current["ui.rule.description"])
        },
        value = state.description,
        readOnly = readOnly,
        showSymbolButton = false,
        onValueChange = { description ->
            vm.setDescription(description)
        })
}

fun AsyncMode.labelKey(): String {
    return when (this) {
        AsyncMode.AsyncMode_Async -> "ui.rule.async.mode.async"
        AsyncMode.AsyncMode_Sync -> "ui.rule.async.mode.sync"
        AsyncMode.AsyncMode_Sync_InRule -> "ui.rule.async.mode.sync_in_rule"
        AsyncMode.UNRECOGNIZED -> ""
    }
}
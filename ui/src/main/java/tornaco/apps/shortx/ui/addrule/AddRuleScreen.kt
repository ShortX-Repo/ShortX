package tornaco.apps.shortx.ui.addrule

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.condition.*
import tornaco.apps.shortx.ui.addrule.fact.*
import tornaco.apps.shortx.ui.base.*
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

    LaunchedEffect(vm) {
        vm.initFromRuleId(ruleId)
        vm.initFromRule(rule)
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
                                        || actionOnDeletedState.isChanged,
                                onClick = {
                                    vm.save()
                                    back(true)
                                }) {
                                RemixIcon(remixName = saveIconName)
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(it)
                        .padding(horizontal = 16.dp)
                        .animateContentSize()
                ) {

                    TitleAndDescription(state = addRuleState, vm = vm, readOnly = readOnly)
                    SectionSpacer()

                    FactSelectors(
                        state = factContentState,
                        handler = factHandler.asFactHandler(),
                        readOnly = readOnly
                    )
                    SectionSpacer()

                    AnimatedVisibility(visible = addRuleState.showConditionSection) {
                        ConditionContent(
                            state = conditionContentState,
                            readOnly = readOnly,
                            handler = conditionHandler.asConditionHandler()
                        )
                        SectionSpacer()
                    }

                    ActionContent(
                        actions = actionContentState.actions,
                        handler = actionHandler.asActionHandler(),
                        readOnly = readOnly,
                        warnings = actionContentState.actionWarningMessages
                    )
                    SectionSpacer()

                    val scope = rememberCoroutineScope()
                    TextButton(onClick = {
                        vm.setShowAdvancedOptions(!addRuleState.showAdvancedOptions)

                        if (!addRuleState.showAdvancedOptions) {
                            scope.launch {
                                delay(100)
                                scrollState.animateScrollTo(Int.MAX_VALUE)
                            }
                        }
                    }) {
                        Text(text = LocalI18N.current["ui.advanced.options"])
                    }
                    StandardSpacer()

                    AnimatedVisibility(visible = addRuleState.showAdvancedOptions) {
                        Column {
                            ActionContent(
                                header = {
                                    MediumTitleWithTip(
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
                                    MediumTitleWithTip(
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
                                    MediumTitleWithTip(
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
            }
        }
    }
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
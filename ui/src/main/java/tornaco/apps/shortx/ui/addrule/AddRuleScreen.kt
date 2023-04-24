package tornaco.apps.shortx.ui.addrule

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
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.condition.*
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.fact.*
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.theme.ShortXTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleScreen(ruleId: String? = null, back: () -> Unit) {
    val vm = hiltViewModel<AddRuleViewModel>()
    val addRuleState by vm.state.collectAsState()

    val context = LocalContext.current
    val savedMessage = LocalI18N.current["ui.toast.rule.saved"]
    val showToast = {
        Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
    }

    val actionHandler = remember(vm) { vm.actionContentStateHandler }
    val actionContentState by actionHandler.state.collectAsState()

    val conditionHandler = remember(vm) { vm.conditionContentStateHandler }
    val conditionContentState by conditionHandler.state.collectAsState()

    LaunchedEffect(vm) {
        vm.initFromRuleId(ruleId)
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
                    MediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = if (addRuleState.isEditRule) LocalI18N.current["ui.title.edit.rule"] else LocalI18N.current["ui.title.add.rule"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(
                                enabled = addRuleState.ruleChanged || actionContentState.isChanged || conditionContentState.isChanged,
                                onClick = {
                                    vm.save()
                                    back()
                                }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(it)
                        .padding(horizontal = 16.dp)
                        .animateContentSize()
                ) {

                    TitleAndDescription(addRuleState, vm)
                    Spacer(modifier = Modifier.size(24.dp))

                    FactSelectors(vm, addRuleState)
                    Spacer(modifier = Modifier.size(24.dp))

                    AnimatedVisibility(visible = addRuleState.showConditionSection) {
                        val conditions = conditionContentState.conditions
                        val conditionOp = conditionContentState.conditionOp
                        ConditionSelectors(
                            conditions = conditions,
                            warnings = conditionContentState.conditionWarningMessages,
                            conditionOp = conditionOp,
                            handler = conditionHandler.asConditionHandler()
                        )
                        Spacer(modifier = Modifier.size(24.dp))
                    }

                    ActionContent(
                        actions = actionContentState.actions,
                        handler = actionHandler.asActionHandler(),
                        warnings = actionContentState.actionWarningMessages
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TitleAndDescription(
    state: AddRuleState,
    vm: AddRuleViewModel,
) {
    OutlinedTextField(modifier = Modifier.fillMaxWidth(), label = {
        Text(text = LocalI18N.current["ui.rule.title"])
    }, value = state.title, onValueChange = { title ->
        vm.setTitle(title)
    })
    Spacer(modifier = Modifier.size(16.dp))
    OutlinedTextField(modifier = Modifier.fillMaxWidth(), label = {
        Text(text = LocalI18N.current["ui.rule.description"])
    }, value = state.description, onValueChange = { description ->
        vm.setDescription(description)
    })
}

class ConditionHandler(
    val addCondition: (Condition) -> Unit,
    val removeCondition: (Condition) -> Unit,
    val setConditionOp: (Op) -> Unit,
)
@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.toBaseSelectorCurrentDataOrNull
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme


@AndroidEntryPoint
class SwitchCaseSelectorActivity : BaseSelectorActivity<Action.SwitchCase>() {
    @Composable
    override fun BaseSelectorActivity<Action.SwitchCase>.SelectorContent(currentData: Action.SwitchCase?) {
        SwitchCaseSelectorDialogSelector(currentData)
    }
}

class SwitchCaseSelectorViewModel : StateViewModel<SwitchCaseSelectorDialogDialogState>(
    initState = {
        SwitchCaseSelectorDialogDialogState()
    }
) {
    private val logger = Logger("SwitchCaseSelectorViewModel")

    private val actionEvaluateListener = object : IActionEvaluateListener.Stub() {
        override fun onStart(id: String) {
            logger.d("actionEvaluateListener#onStart: $id")
            updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }
        }

        override fun onComplete(id: String) {
            logger.d("actionEvaluateListener#onComplete: $id")
            updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }
        }
    }

    init {
        shortXManager.registerActionEvaluateListener(actionEvaluateListener)
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterActionEvaluateListener(actionEvaluateListener)
    }

    fun initFrom(currentSettings: Action.SwitchCase? = null) {
        updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }

        currentSettings?.let {
            updateState {
                copy(
                    id = currentSettings.id,
                    note = currentSettings.note,
                    cases = it.cases,
                    deft = it.deft
                )
            }
        }
    }

    fun addOrUpdateCase(newCase: Action.SwitchCase.Case) {
        val existingIndex = state.value.cases.indexOfFirst { it.id == newCase.id }
        logger.d("addOrUpdateCase, new id: ${newCase.id} existingIndex: $existingIndex")
        updateState {
            copy(cases = cases.toMutableList().apply {
                if (existingIndex >= 0) {
                    set(existingIndex, newCase)
                } else {
                    add(newCase)
                }
            })
        }
    }

    fun removeCase(case: Action.SwitchCase.Case) {
        updateState {
            copy(cases = cases.toMutableList().apply {
                removeIf { it.id == case.id }
            })
        }
    }
}

data class SwitchCaseSelectorDialogDialogState(
    val id: String = defaultNewActionId(),
    val note: String = "",
    val evaluatingActions: List<String> = emptyList(),
    val cases: List<Action.SwitchCase.Case> = emptyList(),
    val deft: Action.SwitchCase.Default = Action.SwitchCase.Default(),
)

@Composable
private fun BaseSelectorActivity<Action.SwitchCase>.SwitchCaseSelectorDialogSelector(currentAction: Action.SwitchCase?) {
    val vm = viewModel<SwitchCaseSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }
    val dialogState by vm.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = {
                            Text(
                                LocalI18N.current.get(
                                    "ui.action.switch.case",
                                    fallback = "Switch case"
                                )
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                cancelAndFinish()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                setResultAndFinish(
                                    Action.SwitchCase(
                                        id = dialogState.id,
                                        note = dialogState.note,
                                        cases = dialogState.cases,
                                        deft = dialogState.deft
                                    )
                                )
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) { paddingValues ->
                val context = LocalContext.current

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp)
                        .padding(paddingValues)
                ) {
                    item {
                        SmallTitle(
                            text = LocalI18N.current.get(
                                "ui.action.switch.case.case",
                                fallback = "Case"
                            ),
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .padding(horizontal = 16.dp)
                        )
                    }

                    items(dialogState.cases) {
                        val caseEditLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartActivityForResult(),
                            onResult = { result ->
                                val case =
                                    result.toBaseSelectorCurrentDataOrNull<Action.SwitchCase.Case>()
                                case?.let { updatedCase ->
                                    vm.addOrUpdateCase(updatedCase)
                                }
                            }
                        )

                        ListItem(titleLines = listOf(
                            it.description.fallbackOnEmpty(LocalI18N.current["ui.common.unnamed"])
                        ),
                            onClick = {
                                caseEditLauncher.launch(
                                    BaseSelectorActivity.newIntent(
                                        context,
                                        it,
                                        SwitchCaseCaseSelectorActivity::class.java
                                    )
                                )
                            },
                            action1 = {
                                IconButton(onClick = {
                                    vm.removeCase(it)
                                }) {
                                    RemixIcon(remixName = Remix.System.close_fill)
                                }
                            }
                        )
                    }

                    item {
                        val newCaseLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartActivityForResult(),
                            onResult = { result ->
                                val case =
                                    result.toBaseSelectorCurrentDataOrNull<Action.SwitchCase.Case>()
                                case?.let { updatedCase ->
                                    vm.addOrUpdateCase(updatedCase)
                                }
                            }
                        )

                        Button(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = 16.dp),
                            onClick = {
                                newCaseLauncher.launch(
                                    BaseSelectorActivity.newIntent(
                                        context,
                                        null,
                                        SwitchCaseCaseSelectorActivity::class.java
                                    )
                                )
                            }) {
                            RemixIcon(
                                modifier = Modifier.size(16.dp),
                                remixName = Remix.System.add_line,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            TinySpacer()
                            Text(
                                LocalI18N.current["ui.common.add"] + " " + LocalI18N.current.get(
                                    "ui.action.switch.case.case",
                                    fallback = "Case"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
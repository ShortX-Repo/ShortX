@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
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
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.theme.ErrorRed
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R


@AndroidEntryPoint
class SwitchCaseSelectorActivity : BaseSelectorActivity<Action.SwitchCase>() {
    @Composable
    override fun BaseSelectorActivity<Action.SwitchCase>.SelectorContent(currentData: Action.SwitchCase?) {
        SwitchCaseSelectorDialogSelector(currentData)
    }
}

class SwitchCaseSelectorViewModel : StateViewModel<SwitchCaseSelectorDialogDialogState, Unit>(
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
            copy(
                isChanged = true,
                cases = cases.toMutableList().apply {
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
            }, isChanged = true)
        }
    }

    fun applySort(cases: List<Action.SwitchCase.Case>) {
        updateState { copy(isChanged = true, cases = cases) }
    }
}

data class SwitchCaseSelectorDialogDialogState(
    val id: String = defaultNewActionId(),
    val note: String = "",
    val evaluatingActions: List<String> = emptyList(),
    val cases: List<Action.SwitchCase.Case> = emptyList(),
    val deft: Action.SwitchCase.Default = Action.SwitchCase.Default(),
    val isChanged: Boolean = false
) {
    fun toAction(): Action.SwitchCase {
        return Action.SwitchCase(
            id = this.id,
            note = this.note,
            cases = this.cases,
            deft = this.deft,
        )
    }
}

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
                                    I18N_R.UI_ACTION_SWITCH_CASE,
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
                            IconButton(
                                enabled = dialogState.isChanged,
                                onClick = {
                                    setResult(dialogState.toAction())
                                }) {
                                RemixIcon(remixName = Remix.Device.save_2_line)
                            }
                            IconButton(
                                enabled = dialogState.isChanged,
                                onClick = {
                                    setResultAndFinish(dialogState.toAction())
                                }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) { paddingValues ->
                val context = LocalContext.current

                val caseSortDialogState = rememberSwitchCaseSortState(cases = dialogState.cases) {
                    vm.applySort(it)
                }
                SwitchCaseSortDialog(state = caseSortDialogState)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp)
                        .padding(paddingValues)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SmallTitle(
                                text = LocalI18N.current.get(
                                    I18N_R.UI_ACTION_SWITCH_CASE_CASE,
                                    fallback = "Case"
                                ),
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .padding(horizontal = 16.dp)
                            )

                            AnimatedVisibility(visible = dialogState.cases.size > 1) {
                                IconButton(onClick = {
                                    caseSortDialogState.show()
                                }) {
                                    RemixIcon(Remix.Editor.sort_asc)
                                }
                            }
                        }
                    }

                    items(dialogState.cases) {
                        val caseEditLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartActivityForResult(),
                            onResult = { result ->
                                val case =
                                    result.popupSelectorResult<Action.SwitchCase.Case>()
                                case?.let { updatedCase ->
                                    vm.addOrUpdateCase(updatedCase)
                                }
                            }
                        )

                        ListItem(
                            icon = {
                                ColoredIcon(onClick = {
                                    vm.addOrUpdateCase(it.copy(isDisabled = !it.isDisabled))
                                }) {
                                    RemixIcon(remixName = Remix.Development.git_commit_fill)
                                }
                            },
                            titleLines = listOf(
                                it.description.fallbackOnEmpty(LocalI18N.current[I18N_R.UI_COMMON_UNNAMED])
                            ),
                            textDecoration = if (!it.isDisabled) TextDecoration.None else TextDecoration.LineThrough,
                            titleColor = if (!it.isDisabled) Color.Unspecified else ErrorRed,
                            textColor = if (!it.isDisabled) Color.Unspecified else ErrorRed,
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
                                    result.popupSelectorResult<Action.SwitchCase.Case>()
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
                                LocalI18N.current[I18N_R.UI_COMMON_ADD] + " " + LocalI18N.current.get(
                                    I18N_R.UI_ACTION_SWITCH_CASE_CASE,
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
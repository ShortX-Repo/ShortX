@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.asContextDataKey
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.util.ClipboardUtils

@AndroidEntryPoint
class ForEachSelectorActivity : BaseSelectorActivity<Action.ForEach>() {
    @Composable
    override fun BaseSelectorActivity<Action.ForEach>.SelectorContent(currentData: Action.ForEach?) {
        ForEachSelectorDialogSelector(currentData)
    }
}

class ForEachSelectorViewModel : StateViewModel<ForEachSelectorDialogDialogState, Unit>(
    initState = {
        ForEachSelectorDialogDialogState()
    }
) {
    private val logger = Logger("ForEachSelectorViewModel")

    val actionHandler = ActionContentStateHandler()

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

    fun initFrom(currentSettings: Action.ForEach? = null) {
        updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }

        currentSettings?.let {
            updateState {
                copy(
                    id = currentSettings.id,
                    note = currentSettings.note,
                    data = currentSettings.data,
                    delimiters = currentSettings.delimiters
                )
            }

            actionHandler.init(
                actions = currentSettings.actions,
                actionAsyncMode = currentSettings.actionAsyncMode
            )
        }
    }

    fun setData(data: String) {
        updateState {
            copy(data = data)
        }
    }

    fun setDelimiters(delimiters: String) {
        updateState {
            copy(delimiters = listOf(delimiters))
        }
    }
}

data class ForEachSelectorDialogDialogState(
    val id: String = defaultNewActionId(),
    val note: String = "",
    val data: String = "",
    val delimiters: List<String> = listOf(","),
    val evaluatingActions: List<String> = emptyList(),
)

@Composable
private fun BaseSelectorActivity<Action.ForEach>.ForEachSelectorDialogSelector(currentAction: Action.ForEach?) {
    val vm = viewModel<ForEachSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }
    val dialogState by vm.state.collectAsState()

    val actionHandler = remember(vm) { vm.actionHandler }
    val actionState by actionHandler.state.collectAsState()

    Scaffold(modifier = Modifier,
        topBar = {
            ShortXTopAppBar(
                modifier = Modifier,
                title = { Text(LocalI18N.current["ui.action.for.each"]) },
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
                            Action.ForEach(
                                actions = actionState.actions,
                                data = dialogState.data,
                                delimiters = dialogState.delimiters,
                                actionAsyncMode = actionState.asyncMode
                            )
                        )
                    }) {
                        RemixIcon(remixName = Remix.System.check_line)
                    }
                },
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues)
                .animateContentSize()
                .verticalScroll(rememberScrollState())
        ) {
            FieldTitle(text = LocalI18N.current["ui.action.for.each.data"])
            SmallSpacer()
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dialogState.data,
                onValueChange = {
                    vm.setData(it)
                })
            StandardSpacer()

            FieldTitle(text = LocalI18N.current["ui.action.for.each.delimiters"])
            SmallSpacer()
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dialogState.delimiters.firstOrNull().orEmpty(),
                onValueChange = {
                    vm.setDelimiters(it)
                })
            StandardSpacer()

            val context = LocalContext.current
            tornaco.apps.shortx.ui.addrule.action.ActionContent(
                header = {
                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current["ui.action.for.each.action"]
                    )
                },
                summary = {
                    Column {
                        TextButton(onClick = {
                            ClipboardUtils.copyToClipboard(
                                context,
                                "key",
                                ContextDataMapping.ForEach.foreachIndex.name.asContextDataKey()
                            )
                        }) {
                            Text(
                                text = LocalI18N.current["ui.action.for.each.action.context.data.loop.index", mapOf(
                                    "index" to "{${ContextDataMapping.ForEach.foreachIndex.name}}"
                                )]
                            )
                        }
                        SmallSpacer()
                        TextButton(onClick = {
                            ClipboardUtils.copyToClipboard(
                                context,
                                "key",
                                ContextDataMapping.ForEach.foreachData.name.asContextDataKey()
                            )
                        }) {
                            Text(
                                text = LocalI18N.current["ui.action.for.each.action.context.data.loop.data",
                                    mapOf(
                                        "data" to "{${ContextDataMapping.ForEach.foreachData.name}}"
                                    )]
                            )
                        }
                    }
                },
                actions = actionState.actions,
                evaluatingActions = dialogState.evaluatingActions,
                handler = actionHandler.asActionHandler(),
                warnings = actionState.actionWarningMessages,
                canSelectActionAsyncMode = true,
                actionAsyncMode = actionState.asyncMode
            )
            Spacer(modifier = Modifier.size(24.dp))

            SuggestedNavEntry()
        }
    }
}
@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.CustomContextDataKey
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.ContextDataInfoDialog
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.dats
import tornaco.apps.shortx.ui.addrule.rememberContextDataInfoDialogState
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LinkText
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.pkgset.PkgSetSelectorDialog
import tornaco.apps.shortx.ui.pkgset.rememberPkgSetSelectorState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme


@AndroidEntryPoint
class ForEachPkgSetSelectorActivity : BaseSelectorActivity<Action.ForEachPkgSet>() {
    @Composable
    override fun BaseSelectorActivity<Action.ForEachPkgSet>.SelectorContent(currentData: Action.ForEachPkgSet?) {
        ForEachPkgSetSelectorDialogSelector(currentData)
    }
}

class ForEachPkgSetSelectorViewModel : StateViewModel<ForEachPkgSetSelectorDialogDialogState>(
    initState = {
        ForEachPkgSetSelectorDialogDialogState()
    }
) {
    private val logger = Logger("ForEachPkgSetSelectorViewModel")

    val actionHandler: ActionContentStateHandler = ActionContentStateHandler()

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

    fun initFrom(currentSettings: Action.ForEachPkgSet? = null) {
        updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }

        currentSettings?.let {
            updateState {
                copy(id = currentSettings.id, note = currentSettings.note, pkgSet = it.pkgSet)
            }
            actionHandler.init(currentSettings.actions)
        }
    }
}

data class ForEachPkgSetSelectorDialogDialogState(
    val id: String = defaultNewActionId(),
    val note: String = "",
    val evaluatingActions: List<String> = emptyList(),
    val pkgSet: PkgSet? = null
)

@Composable
private fun BaseSelectorActivity<Action.ForEachPkgSet>.ForEachPkgSetSelectorDialogSelector(
    currentAction: Action.ForEachPkgSet?
) {
    val vm = viewModel<ForEachPkgSetSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentAction)
    }
    val dialogState by vm.state.collectAsState()

    val actionContentStateHandler = remember(vm) { vm.actionHandler }
    val actionState by actionContentStateHandler.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val i18N = LocalI18N.current
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(i18N["ui.action.for.each.pkg.set"]) },
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
                                    Action.ForEachPkgSet(
                                        id = dialogState.id,
                                        note = dialogState.note,
                                        actions = actionState.actions,
                                        pkgSet = dialogState.pkgSet ?: PkgSet.getDefaultInstance()
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

                    val pkgSetSelectorState = rememberPkgSetSelectorState {
                        vm.updateState {
                            copy(pkgSet = it)
                        }
                    }
                    PkgSetSelectorDialog(state = pkgSetSelectorState)

                    FieldTitle(
                        text = i18N["ui.pkg.set.title"]
                    )
                    MediumSpacer()
                    FilledTonalButton(onClick = {
                        pkgSetSelectorState.show()
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = dialogState.pkgSet?.label
                                    ?: i18N["ui.common.unselected"]
                            )
                            StandardSpacer()
                            RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                        }
                    }
                    StandardSpacer()

                    val contextDataInfoDialogState = rememberContextDataInfoDialogState()
                    ContextDataInfoDialog(state = contextDataInfoDialogState)

                    LinkText(text = i18N["ui.action.for.each.pkg.set.context.data.tip"]) {
                        contextDataInfoDialogState.show(
                            objName = i18N["ui.action.for.each.pkg.set"],
                            contextData = dats<ContextDataMapping.ForEachPkgSet>(
                                CustomContextDataKey.getDefaultInstance()
                            ),
                            canSetCustomKey = false,
                        )
                    }
                    StandardSpacer()

                    tornaco.apps.shortx.ui.addrule.action.ActionContent(
                        actions = actionState.actions,
                        evaluatingActions = dialogState.evaluatingActions,
                        handler = actionContentStateHandler.asActionHandler(),
                        warnings = actionState.actionWarningMessages
                    )

                    SectionSpacer()
                    SectionSpacer()
                    SuggestedNavEntry()
                }
            }
        }
    }
}
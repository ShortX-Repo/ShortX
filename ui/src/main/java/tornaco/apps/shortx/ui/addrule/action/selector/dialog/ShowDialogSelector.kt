@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector.dialog

import android.app.Activity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.action.DialogAction
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.ActionHandler
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchRow
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme


@AndroidEntryPoint
class ShowDialogSelectorActivity : BaseSelectorActivity<Action.ShowAlertDialog>() {

    @Composable
    override fun BaseSelectorActivity<Action.ShowAlertDialog>.SelectorContent(currentData: Action.ShowAlertDialog?) {
        ShowDialogSelector(currentData)
    }

    @Composable
    private fun ShowDialogSelector(currentAction: Action.ShowAlertDialog?) {
        val vm = hiltViewModel<ShowDialogSelectorViewModel>()
        LaunchedEffect(vm) {
            if (currentAction == null) {
                vm.resetState()
            } else {
                vm.initFrom(currentAction)
            }
        }
        val showDialogSelectorState by vm.state.collectAsState()

        ShortXTheme {
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            ) {
                Scaffold(modifier = Modifier,
                    topBar = {
                        ShortXTopAppBar(
                            modifier = Modifier,
                            title = { Text(LocalI18N.current["ui.action.show.dialog"]) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    setResult(Activity.RESULT_CANCELED)
                                    finish()
                                }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    val action = Action.ShowAlertDialog(
                                        id = showDialogSelectorState.id,
                                        title = showDialogSelectorState.title,
                                        message = showDialogSelectorState.message,
                                        cancelable = showDialogSelectorState.cancelable,
                                        positiveText = showDialogSelectorState.positiveText,
                                        negativeText = showDialogSelectorState.negativeText,
                                        neutralText = showDialogSelectorState.neutralText,
                                        style = showDialogSelectorState.style,
                                        positiveActions = vm.positiveActionContent.state.value.actions,
                                        negativeActions = vm.negativeActionContent.state.value.actions,
                                        neutralActions = vm.neutralActionContent.state.value.actions,
                                    )
                                    setResultAndFinish(action)
                                }) {
                                    RemixIcon(remixName = Remix.System.check_line)
                                }

                            },
                        )
                    }) {
                    Content(paddingValues = it, vm = vm)
                }
            }
        }

    }

    @Composable
    private fun Content(paddingValues: PaddingValues, vm: ShowDialogSelectorViewModel) {
        val state by vm.state.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues)
                .animateContentSize()
                .verticalScroll(rememberScrollState())
        ) {
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = LocalI18N.current["ui.action.dialog.title"])
                },
                value = state.title,
                onValueChange = {
                    vm.updateTitle(it)
                })
            StandardSpacer()
            StandardSpacer()

            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),


                label = {
                    Text(text = LocalI18N.current["ui.action.dialog.message"])
                },
                value = state.message,
                onValueChange = {
                    vm.updateMessage(it)
                })
            StandardSpacer()
            StandardSpacer()


            PositiveButton(state, vm)
            StandardSpacer()

            NegativeButton(state, vm)
            StandardSpacer()

            NeutralButton(state, vm)
            StandardSpacer()

            DialogUiStyleSettings(style = state.style, update = {
                vm.updateStyle(it)
            })
            StandardSpacer()


            SwitchRow(
                text = LocalI18N.current["ui.action.dialog.cancelable"],
                isChecked = state.cancelable
            ) {
                vm.updateCancelable(it)
            }
            LargeSpacer()

            SuggestedNavEntry()
        }
    }


    @Composable
    private fun PositiveButton(
        state: ShowDialogSelectorState,
        vm: ShowDialogSelectorViewModel
    ) {
        val positiveAction by remember(vm) {
            vm.positiveActionContent
        }.state.collectAsState()
        ButtonSettings(
            headerTitle = LocalI18N.current["ui.actions.dialog.positive.button"],
            text = state.positiveText ?: "",
            onTextChange = {
                vm.updateButtonText(it, DialogAction.Positive)
            },
            actions = positiveAction.actions,
            evaluatingActions = state.evaluatingActions,
            warnings = positiveAction.actionWarningMessages,
            actionHandler = vm.positiveActionContent.asActionHandler()
        )
    }

    @Composable
    private fun NegativeButton(
        state: ShowDialogSelectorState,
        vm: ShowDialogSelectorViewModel
    ) {
        val negativeAction by remember(vm) {
            vm.negativeActionContent
        }.state.collectAsState()
        ButtonSettings(
            headerTitle = LocalI18N.current["ui.actions.dialog.negative.button"],
            text = state.negativeText ?: "",
            onTextChange = {
                vm.updateButtonText(it, DialogAction.Negative)
            },
            actions = negativeAction.actions,
            evaluatingActions = state.evaluatingActions,
            warnings = negativeAction.actionWarningMessages,
            actionHandler = vm.negativeActionContent.asActionHandler()
        )
    }


    @Composable
    private fun NeutralButton(
        state: ShowDialogSelectorState,
        vm: ShowDialogSelectorViewModel
    ) {
        val neutralAction by remember(vm) {
            vm.neutralActionContent
        }.state.collectAsState()
        ButtonSettings(
            headerTitle = LocalI18N.current["ui.actions.dialog.neutral.button"],
            text = state.neutralText ?: "",
            onTextChange = {
                vm.updateButtonText(it, DialogAction.Neutral)
            },
            actions = neutralAction.actions,
            evaluatingActions = state.evaluatingActions,
            warnings = neutralAction.actionWarningMessages,
            actionHandler = vm.neutralActionContent.asActionHandler()
        )
    }

    @Composable
    private fun ButtonSettings(
        headerTitle: String,
        text: String,
        onTextChange: (String) -> Unit,
        actions: List<Action>,
        evaluatingActions: List<String>,
        warnings: Map<String, List<String>>,
        actionHandler: ActionHandler
    ) {
        MediumTitle(headerTitle)
        ShortXTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = LocalI18N.current["ui.action.dialog.button.text"])
            },
            value = text,
            onValueChange = {
                onTextChange(it)
            })
        TinySpacer()
        TipRow(text = LocalI18N.current["ui.actions.dialog.button.optional"])
        TinySpacer()

        ActionContent(
            header = {
                SmallTitle(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = LocalI18N.current["ui.actions.title"]
                )
            },
            actions = actions,
            evaluatingActions = evaluatingActions,
            handler = actionHandler,
            warnings = warnings
        )
    }
}
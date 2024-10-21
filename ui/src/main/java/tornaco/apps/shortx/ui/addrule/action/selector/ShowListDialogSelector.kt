@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchRow
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry


@AndroidEntryPoint
class ShowListDialogSelectorActivity : BaseSelectorActivity<Action.ShowListDialog>() {

    @Composable
    override fun BaseSelectorActivity<Action.ShowListDialog>.SelectorContent(currentData: Action.ShowListDialog?) {
        ShowListDialogSelector(currentData)
    }
}

@Composable
private fun ShowListDialogSelectorActivity.ShowListDialogSelector(currentData: Action.ShowListDialog?) {
    val vm = hiltViewModel<ShowListDialogSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentData)
    }
    val dialogState by vm.state.collectAsState()
    Scaffold(modifier = Modifier,
        topBar = {
            ShortXTopAppBar(
                modifier = Modifier,
                title = { Text(LocalI18N.current["ui.action.show.list.dialog"]) },
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
                            Action.ShowListDialog(
                                title = dialogState.title,
                                message = dialogState.message,
                                cancelable = dialogState.cancelable,
                                data = dialogState.data,
                                isMultipleChoice = dialogState.isMultipleChoice,
                                delimiters = dialogState.delimiters
                            )
                        )
                    }) {
                        RemixIcon(remixName = Remix.System.check_line)
                    }
                },
            )
        }) { paddingValues ->
        ClickOutsideClearFocusBox {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues)
                    .animateContentSize()
                    .verticalScroll(rememberScrollState())
            ) {
                FieldTitle(text = LocalI18N.current["ui.action.dialog.title"])
                MediumSpacer()
                ShortXTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = dialogState.title,
                    onValueChange = {
                        vm.updateTitle(it)
                    })
                StandardSpacer()
                StandardSpacer()

                FieldTitle(text = LocalI18N.current["ui.action.show.list.dialog.data"])
                MediumSpacer()
                TipBody(LocalI18N.current["ui.action.show.list.dialog.data.summary"])

                MediumSpacer()
                ShortXTextField(dialogState.data, onValueChange = {
                    vm.updateData(it)
                })

                LargeSpacer()
                // Delimiters
                FieldTitle(text = LocalI18N.current["ui.action.show.list.dialog.delimiters"])
                MediumSpacer()
                TipBody(LocalI18N.current["ui.action.show.list.dialog.delimiters.summary"])
                dialogState.delimiters.forEach {
                    ListItem(titleLines = listOf(it), action1 = {
                        IconButton(onClick = { vm.removeDelimiters(it) }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    })
                }
                val addDelDialogState = rememberTextInputState(
                    title = LocalI18N.current["ui.common.add"],
                    message = null
                ) {
                    vm.addDelimiters(it)
                }
                TextInputDialog(addDelDialogState)
                StandardSpacer()
                androidx.compose.material3.Button(onClick = {
                    addDelDialogState.show()
                }) {
                    Text(LocalI18N.current["ui.common.add"])
                }


                SectionSpacer()

                SwitchRow(
                    text = LocalI18N.current["ui.action.dialog.cancelable"],
                    isChecked = dialogState.cancelable
                ) {
                    vm.updateCancelable(it)
                }

                LargeSpacer()

                SwitchRow(
                    text = LocalI18N.current["ui.action.show.list.dialog.is.multiple"],
                    isChecked = dialogState.isMultipleChoice
                ) {
                    vm.updateIsMultipleChoice(it)
                }

                LargeSpacer()

                SuggestedNavEntry()
            }
        }
    }
}
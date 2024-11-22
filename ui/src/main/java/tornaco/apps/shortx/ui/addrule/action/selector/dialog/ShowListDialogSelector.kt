@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector.dialog

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.action.ShowListDialogDataType
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
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
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
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
                                delimiters = dialogState.delimiters,
                                dataType = dialogState.dataType,
                                needConfirmAction = dialogState.needConfirmAction,
                                style = dialogState.style,
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
                LargeSpacer()

                // Type
                FieldTitle(text = LocalI18N.current["ui.action.show.list.dialog.data.type"])
                MediumSpacer()
                val dataTypeState = rememberDropdownSelectorState()
                Box {
                    FilledTonalButton(
                        onClick = {
                            dataTypeState.open()
                        }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (dialogState.dataType == ShowListDialogDataType.ShowListDialogDataType_String) "String" else "Json")
                            LargeSpacer()
                            LargeSpacer()
                            RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                        }
                    }
                    DropdownSelector(
                        state = dataTypeState,
                        items = listOf(
                            DropdownItem(
                                labelLines = listOf("String"),
                                data = ShowListDialogDataType.ShowListDialogDataType_String,
                            ),
                            DropdownItem(
                                labelLines = listOf("Json"),
                                data = ShowListDialogDataType.ShowListDialogDataType_Json,
                            )
                        )
                    ) {
                        vm.updateDataType(it.data)
                    }
                }
                LargeSpacer()

                // Data
                FieldTitle(text = LocalI18N.current["ui.action.show.list.dialog.data"])
                MediumSpacer()
                if (dialogState.dataType == ShowListDialogDataType.ShowListDialogDataType_String) {
                    // Summary for String type, tip for string split.
                    TipBody(LocalI18N.current["ui.action.show.list.dialog.data.summary"])
                } else {
                    TipBody(LocalI18N.current["ui.action.show.list.dialog.data.summary.json"])
                }

                MediumSpacer()
                ShortXTextField(dialogState.data, onValueChange = {
                    vm.updateData(it)
                })

                LargeSpacer()

                if (dialogState.dataType == ShowListDialogDataType.ShowListDialogDataType_String) {
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
                }
                LargeSpacer()

                DialogUiStyleSettings(style = dialogState.style, update = {
                    vm.updateStyle(it)
                })

                SectionSpacer()

                SwitchRow(
                    text = LocalI18N.current["ui.action.dialog.cancelable"],
                    isChecked = dialogState.cancelable
                ) {
                    vm.updateCancelable(it)
                }

                LargeSpacer()

                SwitchRow(
                    text = LocalI18N.current["ui.action.dialog.need.confirm.action"],
                    isChecked = dialogState.needConfirmAction
                ) {
                    vm.updateNeedConfirmAction(it)
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
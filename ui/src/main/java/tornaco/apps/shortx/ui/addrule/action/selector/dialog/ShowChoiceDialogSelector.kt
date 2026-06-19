@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.CommonSortDialog
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SortItem
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchRow
import tornaco.apps.shortx.ui.base.rememberCommonSortState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.core.i18n.I18N_R


@AndroidEntryPoint
class ShowChoiceDialogSelectorActivity : BaseSelectorActivity<Action.ShowChoiceDialog>() {

    @Composable
    override fun BaseSelectorActivity<Action.ShowChoiceDialog>.SelectorContent(currentData: Action.ShowChoiceDialog?) {
        ShowChoiceDialogSelector(currentData)
    }
}

@Composable
private fun ShowChoiceDialogSelectorActivity.ShowChoiceDialogSelector(currentData: Action.ShowChoiceDialog?) {
    val vm = hiltViewModel<ShowChoiceDialogSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentData)
    }
    val dialogState by vm.state.collectAsState()
    Scaffold(modifier = Modifier,
        topBar = {
            ShortXTopAppBar(
                modifier = Modifier,
                title = { Text(LocalI18N.current[I18N_R.UI_ACTION_SHOW_MENU_DIALOG]) },
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
                            Action.ShowChoiceDialog(
                                title = dialogState.title,
                                message = dialogState.message,
                                cancelable = dialogState.cancelable,
                                choices = dialogState.choices,
                                isMultipleChoice = dialogState.isMultipleChoice,
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
                FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_DIALOG_TITLE])
                MediumSpacer()
                ShortXTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = dialogState.title,
                    onValueChange = {
                        vm.updateTitle(it)
                    })
                StandardSpacer()
                StandardSpacer()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val sortDialogState = rememberCommonSortState(
                        apply = {
                            vm.applySort(it)
                        })
                    CommonSortDialog(
                        state = sortDialogState,
                        items = dialogState.choices.mapIndexed { index, menuDialogUiItem ->
                            SortItem(index, menuDialogUiItem.label)
                        })

                    FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_SHOW_MENU_DIALOG_ITEM])
                    // Sort
                    AnimatedVisibility(visible = dialogState.choices.size > 1) {
                        IconButton(onClick = {
                            sortDialogState.show()
                        }) {
                            RemixIcon(Remix.Editor.sort_asc)
                        }
                    }
                }
                MediumSpacer()
                Items(
                    state = dialogState,
                    updateItemLabel = { string, int ->
                        vm.updateItemLabel(string, int)
                    },
                    updateItemValue = { string, int ->
                        vm.updateItemValue(string, int)
                    },
                    removeItem = {
                        vm.removeItem(it)
                    },
                    defaultSelectItem = { index, bool ->
                        vm.updateItemDefSelection(bool, index)
                    }
                )
                StandardSpacer()

                // Add icon
                Button(onClick = {
                    vm.addItem()
                }) {
                    Text(text = LocalI18N.current[I18N_R.UI_ACTION_SHOW_MENU_DIALOG_ITEM_ADD])
                }

                SectionSpacer()

                SwitchRow(
                    text = LocalI18N.current[I18N_R.UI_ACTION_DIALOG_CANCELABLE],
                    isChecked = dialogState.cancelable
                ) {
                    vm.updateCancelable(it)
                }

                LargeSpacer()

                DialogUiStyleSettings(style = dialogState.style, update = {
                    vm.updateStyle(it)
                })
                LargeSpacer()

                SwitchRow(
                    text = LocalI18N.current[I18N_R.UI_ACTION_SHOW_CHOICE_DIALOG_IS_MULTIPLE],
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


@Composable
private fun Items(
    state: ShowChoiceDialogSelectorState,
    updateItemLabel: (String, Int) -> Unit,
    updateItemValue: (String, Int) -> Unit,
    removeItem: (Int) -> Unit,
    defaultSelectItem: (Int, Boolean) -> Unit,
) {
    state.choices.forEachIndexed { index, menuDialogUiItem ->
        ItemSettings(
            headerTitle = LocalI18N.current[I18N_R.UI_ACTION_SHOW_CHOICE_DIALOG_ITEM] + " ${index + 1}",
            label = menuDialogUiItem.label,
            value = menuDialogUiItem.value,
            isSelected = menuDialogUiItem.isSelectedByDefault,
            onLabelChanged = {
                updateItemLabel(it, index)
            },
            onValueChanged = {
                updateItemValue(it, index)
            },
            removeItem = {
                removeItem(index)
            },
            defaultSelectItem = {
                defaultSelectItem(index, it)
            }
        )

        LargeSpacer()
    }
}

@Composable
private fun ItemSettings(
    headerTitle: String,
    label: String,
    value: String,
    isSelected: Boolean,
    onLabelChanged: (String) -> Unit,
    onValueChanged: (String) -> Unit,
    removeItem: () -> Unit,
    defaultSelectItem: (Boolean) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(1f)) {
                MediumTitle(headerTitle)
            }

            IconButton(onClick = {
                removeItem()
            }) {
                RemixIcon(remixName = Remix.System.close_line)
            }
        }
        StandardSpacer()
        ShortXTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = LocalI18N.current[I18N_R.UI_ACTION_SHOW_CHOICE_DIALOG_ITEM_LABEL])
            },
            value = label,
            onValueChange = {
                onLabelChanged(it)
            }
        )
        StandardSpacer()
        ShortXTextField(
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = LocalI18N.current[I18N_R.UI_ACTION_SHOW_CHOICE_DIALOG_ITEM_VALUE])
            },
            value = value,
            onValueChange = {
                onValueChanged(it)
            }
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(LocalI18N.current[I18N_R.UI_ACTION_SHOW_CHOICE_DIALOG_ITEM_DEFAULT_SELECTED])
            StandardSpacer()
            Checkbox(checked = isSelected, onCheckedChange = {
                defaultSelectItem(!isSelected)
            })
        }
    }
}
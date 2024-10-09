@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

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
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.ActionHandler
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchRow
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry


@AndroidEntryPoint
class ShowMenuDialogSelectorActivity : BaseSelectorActivity<Action.ShowMenuDialog>() {

    @Composable
    override fun BaseSelectorActivity<Action.ShowMenuDialog>.SelectorContent(currentData: Action.ShowMenuDialog?) {
        ShowMenuDialogSelector(currentData)
    }
}

@Composable
private fun ShowMenuDialogSelectorActivity.ShowMenuDialogSelector(currentData: Action.ShowMenuDialog?) {
    val vm = hiltViewModel<ShowMenuDialogSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.initFrom(currentData)
    }
    val dialogState by vm.state.collectAsState()
    Scaffold(modifier = Modifier,
        topBar = {
            ShortXTopAppBar(
                modifier = Modifier,
                title = { Text(LocalI18N.current["ui.action.show.menu.dialog"]) },
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
                            Action.ShowMenuDialog(
                                title = dialogState.title,
                                message = dialogState.message,
                                cancelable = dialogState.cancelable,
                                items = dialogState.items.toProtoItems()
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val sortDialogState = rememberDialogMenuSortState(
                        apply = {
                            vm.applySort(it)
                        })
                    DialogMenuSortDialog(state = sortDialogState, menus = dialogState.items)

                    FieldTitle(text = LocalI18N.current["ui.action.show.menu.dialog.item"])
                    // Sort
                    AnimatedVisibility(visible = dialogState.items.size > 1) {
                        IconButton(onClick = {
                            sortDialogState.show()
                        }) {
                            RemixIcon(Remix.Editor.sort_asc)
                        }
                    }
                }
                MediumSpacer()
                Items(state = dialogState,
                    updateItemTitle = { string, int ->
                        vm.updateItemTitle(string, int)
                    },
                    removeItem = {
                        vm.removeItem(it)
                    }
                )
                StandardSpacer()

                // Add icon
                Button(onClick = {
                    vm.addButton()
                }) {
                    Text(text = LocalI18N.current["ui.action.show.menu.dialog.item.add"])
                }

                SectionSpacer()

                SwitchRow(
                    text = LocalI18N.current["ui.action.dialog.cancelable"],
                    isChecked = dialogState.cancelable
                ) {
                    vm.updateCancelable(it)
                }

                LargeSpacer()

                SuggestedNavEntry()
            }
        }
    }
}


@Composable
private fun Items(
    state: ShowMenuDialogSelectorState,
    updateItemTitle: (String, Int) -> Unit,
    removeItem: (Int) -> Unit,
) {
    state.items.forEachIndexed { index, menuDialogUiItem ->
        val actionState by menuDialogUiItem.actionHandler.state.collectAsState()
        ItemSettings(
            headerTitle = LocalI18N.current["ui.action.show.menu.dialog.item"] + " ${index + 1}",
            text = menuDialogUiItem.title,
            onTextChange = {
                updateItemTitle(it, index)
            },
            actions = actionState.actions,
            evaluatingActions = state.evaluatingActions,
            warnings = emptyMap(),
            actionHandler = menuDialogUiItem.actionHandler.asActionHandler(),
            removeItem = {
                removeItem(index)
            }
        )

        StandardSpacer()
    }
}

@Composable
private fun ItemSettings(
    headerTitle: String,
    text: String,
    onTextChange: (String) -> Unit,
    actions: List<Action>,
    evaluatingActions: List<String>,
    warnings: Map<String, List<String>>,
    actionHandler: ActionHandler,
    removeItem: () -> Unit
) {
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
    ShortXTextField(
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = LocalI18N.current["ui.action.show.menu.dialog.item.text"])
        },
        value = text,
        onValueChange = {
            onTextChange(it)
        })
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
@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.selector.SinglePkgAndUserSelectorDialog
import tornaco.apps.shortx.ui.addrule.action.selector.rememberSinglePkgAndUserSelectorDialogState
import tornaco.apps.shortx.ui.addrule.condition.mapper.opDescription
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogState
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXFilledTonalButton
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

class ConditionPkgAndUserListSelectorDialogState(
    val title: String,
    val onSelected: (List<StringPair>, Op) -> Unit
) : CommonDialogState() {
    var selectedPairs by mutableStateOf(emptyList<StringPair>())
    var op by mutableStateOf<Op>(Op.Any)

    fun show(current: List<StringPair>? = null, op: Op? = null) {
        this.selectedPairs = current ?: emptyList()
        this.op = op ?: Op.Any
        show()
    }

    fun selectOp(op: Op) {
        this.op = op
    }

    fun addItem(item: StringPair) {
        selectedPairs = selectedPairs.toMutableList().apply {
            add(item)
        }
    }

    fun remove(item: Int) {
        selectedPairs = selectedPairs.toMutableList().apply {
            removeAt(item)
        }
    }
}

@Composable
fun rememberConditionPkgAndUserListSelectorDialogState(
    title: String,
    onSelected: (List<StringPair>, Op) -> Unit
): ConditionPkgAndUserListSelectorDialogState {
    return remember {
        ConditionPkgAndUserListSelectorDialogState(title, onSelected)
    }
}

@Composable
fun ConditionPackageNameAndUserSelectorDialog(state: ConditionPkgAndUserListSelectorDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            DialogContent(state)
        }
    }
}

@Composable
private fun DialogContent(dialogState: ConditionPkgAndUserListSelectorDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(dialogState.title) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (dialogState.selectedPairs.isNotEmpty()) {
                                    dialogState.onSelected(
                                        dialogState.selectedPairs,
                                        dialogState.op
                                    )
                                    dialogState.dismiss()
                                }
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
                    dialogState.selectedPairs.forEachIndexed { index, it ->
                        ListItem(
                            titleLines = listOf(it.first, it.second),
                            icon = {
                                RemixIcon(remixName = Remix.Logos.flickr_line)
                            },
                            action1 = {
                                IconButton(onClick = {
                                    dialogState.remove(index)
                                }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            onClick = {
                            })
                    }


                    AnimatedVisibility(
                        visible = dialogState.selectedPairs.size > 1
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            MediumTitle(
                                text = LocalI18N.current[I18N_R.UI_CONDITION_SELECTOR_APP_ALL_ANY_OP_TITLE]
                            )
                            MediumSpacer()
                            val opSelectorDialogState: MenuDialogState<String> =
                                rememberAppAllAnyMenuDialogState(
                                    onItemSelected = { _, op ->
                                        dialogState.selectOp(op)
                                    })
                            MenuDialog(state = opSelectorDialogState)
                            ShortXFilledTonalButton(onClick = {
                                opSelectorDialogState.show()
                            }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = opDescription(dialogState.op, LocalI18N.current)[0])
                                    StandardSpacer()
                                    RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                                }
                            }
                            LargeSpacer()
                        }
                    }


                    val singlePkgAndUserSelectState = rememberSinglePkgAndUserSelectorDialogState(
                        title = dialogState.title,
                        onSelected = {
                            dialogState.addItem(it)
                        }
                    )
                    SinglePkgAndUserSelectorDialog(state = singlePkgAndUserSelectState)

                    androidx.compose.material3.Button(onClick = {
                        singlePkgAndUserSelectState.show()
                    }) {
                        Text(text = LocalI18N.current[I18N_R.UI_COMMON_ADD])
                    }
                    StandardSpacer()
                }
            }
        }
    }

}

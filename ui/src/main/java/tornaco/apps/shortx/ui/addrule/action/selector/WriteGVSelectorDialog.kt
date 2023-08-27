@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.action.WriteGlobalVarOp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.displayLabelKey
import tornaco.apps.shortx.core.rule.isForListOnly
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.gvar.GlobalVarSelectorDialog
import tornaco.apps.shortx.ui.gvar.rememberGlobalVarSelectorState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme


class WriteGVDialogState(
    val onSelected: (Action.WriteGV) -> Unit
) : CommonDialogState() {
    var name by mutableStateOf("")
    var valueString by mutableStateOf("")

    var op by mutableStateOf(WriteGlobalVarOp.WriteGlobalVarOp_AppendToLast)

    val isValid get() = name.isNotBlank()

    fun show(current: Action.WriteGV? = null) {
        this.name = current?.varName ?: ""
        this.valueString = current?.valueAsString ?: ""

        this.op = current?.op ?: WriteGlobalVarOp.WriteGlobalVarOp_AppendToLast

        show()
    }
}

@Composable
fun rememberWriteGVDialogState(
    onSelected: (Action.WriteGV) -> Unit
): WriteGVDialogState {
    return remember {
        WriteGVDialogState(onSelected)
    }
}

@Composable
fun WriteGVDialog(state: WriteGVDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            WriteGVSelector(state)
        }
    }
}

@Composable
private fun WriteGVSelector(dialogState: WriteGVDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.action.write.global.var"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (dialogState.isValid) {
                                    dialogState.onSelected(
                                        Action.WriteGV(
                                            varName = dialogState.name,
                                            valueAsString = dialogState.valueString,
                                            op = dialogState.op
                                        )
                                    )
                                    dialogState.dismiss()
                                }
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) {
                Content(paddingValues = it, dialogState = dialogState)
            }
        }
    }

}

@Composable
private fun Content(paddingValues: PaddingValues, dialogState: WriteGVDialogState) {
    val globalVarToWriteSelectorState = rememberGlobalVarSelectorState {
        dialogState.name = it.name
    }
    GlobalVarSelectorDialog(globalVarToWriteSelectorState)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)
            .animateContentSize()
            .verticalScroll(rememberScrollState())
    ) {

        FieldTitle(
            text = LocalI18N.current["ui.action.select.global.var"]
        )
        SmallSpacer()
        ShortXTextField(
            modifier = Modifier.fillMaxWidth(),
            value = dialogState.name,
            onValueChange = {
                dialogState.name = it
            },
            trailingIcon = {
                IconButton(onClick = {
                    globalVarToWriteSelectorState.show()
                }) {
                    RemixIcon(modifier = Modifier, remixName = Remix.System.search_2_line)
                }
            })

        StandardSpacer()

        FieldTitle(
            text = LocalI18N.current["ui.global.var.value"]
        )
        SmallSpacer()
        ShortXTextField(
            modifier = Modifier.fillMaxWidth(),
            value = dialogState.valueString,
            onValueChange = {
                dialogState.valueString = it
            })
        StandardSpacer()

        FieldTitle(text = "Operation")
        val opDropdownState = rememberDropdownSelectorState()
        SmallSpacer()
        Box(modifier = Modifier.fillMaxWidth()) {
            FilledTonalButton(onClick = {
                opDropdownState.open()
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = dialogState.op.displayLabelKey())
                    StandardSpacer()
                    RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                }
            }
            DropdownSelector(
                state = opDropdownState,
                items = WriteGlobalVarOp.values()
                    .filterNot { it == WriteGlobalVarOp.UNRECOGNIZED }.map {
                        DropdownItem(
                            labelLines = listOfNotNull(
                                it.displayLabelKey(),
                                if (it.isForListOnly()) "For List only" else null
                            ),
                            data = it
                        )
                    },
                onSelect = {
                    dialogState.op = it.data
                })
        }


        SectionSpacer()
        SuggestedNavEntry()
    }
}
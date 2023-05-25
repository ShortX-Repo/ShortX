@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.gvar.GlobalVarSelectorDialog
import tornaco.apps.shortx.ui.gvar.rememberGlobalVarSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme


class WriteGVDialogState(
    val onSelected: (Action.WriteGV) -> Unit
) {
    var isShowing by mutableStateOf(false)

    fun show() {
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
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
    val vm = hiltViewModel<WriteGVSelectorViewModel>().apply { resetState() }
    val state = vm.state.collectAsState()

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
                                if (state.value.isValid()) {
                                    dialogState.onSelected(state.value)
                                    dialogState.dismiss()
                                }
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
private fun Content(paddingValues: PaddingValues, vm: WriteGVSelectorViewModel) {
    val globalVarToWriteSelectorState = rememberGlobalVarSelectorState {
        vm.selectGlobalVar(it.name)
    }
    GlobalVarSelectorDialog(globalVarToWriteSelectorState)

    val state by vm.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)
            .animateContentSize()
            .verticalScroll(rememberScrollState())
    ) {

        SmallTitle(text = LocalI18N.current["ui.action.select.global.var"])
        ShortXTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.varName,
            onValueChange = {
                vm.selectGlobalVar(it)
            },
            trailingIcon = {
                IconButton(onClick = {
                    globalVarToWriteSelectorState.show()
                }) {
                    RemixIcon(modifier = Modifier, remixName = Remix.System.search_2_line)
                }
            })

        StandardSpacer()

        SmallTitle(text = LocalI18N.current["ui.global.var.value"])
        ShortXTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.valueAsString,
            onValueChange = {
                vm.inputValue(it)
            })

    }
}
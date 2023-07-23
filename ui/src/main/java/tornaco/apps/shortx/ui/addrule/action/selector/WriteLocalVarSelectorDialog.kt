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
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme


class WriteLocalVarDialogState(
    val onSelected: (Action.WriteLocalVar) -> Unit
) : CommonDialogState() {
    var name by mutableStateOf("")
    var valueString by mutableStateOf("")

    val isValid get() = name.isNotBlank()
}

@Composable
fun rememberWriteLocalVarDialogState(
    onSelected: (Action.WriteLocalVar) -> Unit
): WriteLocalVarDialogState {
    return remember {
        WriteLocalVarDialogState(onSelected)
    }
}

@Composable
fun WriteLocalVarDialog(state: WriteLocalVarDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            WriteLocalVarSelector(state)
        }
    }
}

@Composable
private fun WriteLocalVarSelector(dialogState: WriteLocalVarDialogState) {

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.action.write.local.var"]) },
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
                                        Action.WriteLocalVar(
                                            varName = dialogState.name,
                                            valueAsString = dialogState.valueString
                                        )
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

                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current["ui.action.select.global.var"]
                    )
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = dialogState.name,
                        onValueChange = {
                            dialogState.name = it
                        })

                    StandardSpacer()

                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current["ui.global.var.value"]
                    )
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = dialogState.valueString,
                        onValueChange = {
                            dialogState.valueString = it
                        })

                }
            }
        }
    }

}

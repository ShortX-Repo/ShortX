@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.gvar.GlobalVarSelectorDialog
import tornaco.apps.shortx.ui.gvar.rememberGlobalVarSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

class GVSelectorDialogState(
    val onSelected: (String) -> Unit
) : CommonDialogState() {
    var selectedGVName: String by mutableStateOf("")

    fun selectGlobalVar(varName: String) {
        selectedGVName = varName
    }

    fun show(selectedVar: String? = null) {
        selectedGVName = selectedVar ?: ""
        show()
    }
}

@Composable
fun rememberGVSelectorState(
    onSelected: (String) -> Unit
): GVSelectorDialogState {
    return remember {
        GVSelectorDialogState(onSelected)
    }
}

@Composable
fun GVSelectorDialog(state: GVSelectorDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            GVSelector(state)
        }
    }
}

@Composable
private fun GVSelector(dialogState: GVSelectorDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current[I18N_R.UI_FACT_GLOBAL_VAR_CHANGED]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (dialogState.selectedGVName.isNotBlank()) {
                                    dialogState.dismiss()
                                    dialogState.onSelected.invoke(dialogState.selectedGVName)
                                }
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        }
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val globalVarToWriteSelectorState = rememberGlobalVarSelectorState {
                        dialogState.selectGlobalVar(it.name)
                    }
                    GlobalVarSelectorDialog(globalVarToWriteSelectorState)

                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = dialogState.selectedGVName,
                        label = {
                            Text(LocalI18N.current[I18N_R.UI_ACTION_SELECT_GLOBAL_VAR])
                        },
                        trailingIcon = {
                            IconButton(onClick = { globalVarToWriteSelectorState.show() }) {
                                RemixIcon(remixName = Remix.System.search_2_line)
                            }
                        },
                        onValueChange = { newValue ->
                            dialogState.selectGlobalVar(newValue)
                        })

                }
            }
        }
    }
}
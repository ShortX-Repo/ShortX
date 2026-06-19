@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.gvar.GlobalVarSelectorDialog
import tornaco.apps.shortx.ui.gvar.rememberGlobalVarSelectorState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R


class DeleteGVDialogState(
    val onSelected: (Action.DeleteGV) -> Unit
) : CommonDialogState() {
    var name by mutableStateOf("")
    val isValid get() = name.isNotBlank()

    fun show(current: Action.DeleteGV? = null) {
        this.name = current?.varName ?: ""
        show()
    }
}

@Composable
fun rememberDeleteGVDialogState(
    onSelected: (Action.DeleteGV) -> Unit
): DeleteGVDialogState {
    return remember {
        DeleteGVDialogState(onSelected)
    }
}

@Composable
fun DeleteGVDialog(state: DeleteGVDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            DeleteGVSelector(state)
        }
    }
}

@Composable
private fun DeleteGVSelector(dialogState: DeleteGVDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current[I18N_R.UI_ACTION_DELETE_GLOBAL_VAR]) },
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
                                        Action.DeleteGV(
                                            varName = dialogState.name,
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
private fun Content(paddingValues: PaddingValues, dialogState: DeleteGVDialogState) {
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

        SmallTitle(
            modifier = Modifier.padding(vertical = 12.dp),
            text = LocalI18N.current[I18N_R.UI_ACTION_SELECT_GLOBAL_VAR]
        )
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


        SectionSpacer()
        SuggestedNavEntry()
    }
}
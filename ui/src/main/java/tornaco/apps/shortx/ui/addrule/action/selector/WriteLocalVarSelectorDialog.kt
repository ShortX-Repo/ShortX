@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R


class WriteLocalVarDialogState(
    val onSelected: (Action.WriteLocalVar) -> Unit
) : CommonDialogState() {
    var name by mutableStateOf("")
    var valueString by mutableStateOf("")
    var autoCreateIfMissing by mutableStateOf(false)

    val isValid get() = name.isNotBlank()

    fun show(current: Action.WriteLocalVar? = null) {
        this.name = current?.varName ?: ""
        this.valueString = current?.valueAsString ?: ""
        this.autoCreateIfMissing = current?.autoCreateIfMissing ?: false
        show()
    }
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
                        title = { Text(text = LocalI18N.current[I18N_R.UI_ACTION_WRITE_LOCAL_VAR]) },
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
                                            valueAsString = dialogState.valueString,
                                            autoCreateIfMissing = dialogState.autoCreateIfMissing,
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
                        text = LocalI18N.current[I18N_R.UI_ACTION_SELECT_GLOBAL_VAR]
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
                        text = LocalI18N.current[I18N_R.UI_ACTION_VAR_AUTO_CREATE_TITLE]
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ShortXSwitch(
                            checked = dialogState.autoCreateIfMissing,
                            onCheckedChange = { dialogState.autoCreateIfMissing = it }
                        )
                        StandardSpacer()
                        Text(text = LocalI18N.current[I18N_R.UI_ACTION_VAR_AUTO_CREATE_SUMMARY])
                    }

                    StandardSpacer()

                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current[I18N_R.UI_GLOBAL_VAR_VALUE]
                    )
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth().imePadding(),
                        value = dialogState.valueString,
                        onValueChange = {
                            dialogState.valueString = it
                        })

                    SectionSpacer()
                    SectionSpacer()
                    SuggestedNavEntry()
                }
            }
        }
    }

}

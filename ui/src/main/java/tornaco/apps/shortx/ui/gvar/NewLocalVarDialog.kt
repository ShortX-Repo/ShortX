package tornaco.apps.shortx.ui.gvar


import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.gv.LocalVar
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.GlobalVarType
import tornaco.apps.shortx.core.rule.LOCAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.label
import tornaco.apps.shortx.core.rule.toProtoType
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.util.ClipboardUtils

class NewLocalVarState(
    val onSelected: (Pair<String, GlobalVarType>) -> Unit
) : CommonDialogState() {
    var name by mutableStateOf("")
    var type by mutableStateOf(GlobalVarType.String)

    fun show(current: Pair<String, GlobalVarType>? = null) {
        this.name = current?.first ?: ""
        this.type = current?.second ?: GlobalVarType.String

        show()
    }
}

@Composable
fun rememberNewLocalVarState(
    onSelected: (Pair<String, GlobalVarType>) -> Unit
): NewLocalVarState {
    return remember {
        NewLocalVarState(onSelected)
    }
}

fun Pair<String, GlobalVarType>.toLocalVar(): LocalVar =
    LocalVar
        .newBuilder()
        .setName(first)
        .setType(second.toProtoType())
        .build()

@Composable
fun NewLocalVarDialog(state: NewLocalVarState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.local.var.title"])
        }, buttons = {
            TextButton(onClick = {
                if (state.name.isNotBlank()) {
                    state.onSelected(state.name.trim() to state.type)
                    state.dismiss()
                }
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                val context = LocalContext.current
                val i18N = LocalI18N.current

                SmallTitle(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = LocalI18N.current["ui.local.var.name"]
                )
                SmallSpacer()
                TipBody(text = LocalI18N.current["ui.local.var.name.description"])
                SmallSpacer()
                ShortXTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = state.name,
                    showSymbolButton = false,
                    onValueChange = {
                        state.name = it
                    })
                SmallSpacer()
                TextButton(onClick = {
                    ClipboardUtils.copyToClipboard(
                        context,
                        "var",
                        "$LOCAL_VAR_REF_PREFIX${state.name}"
                    )
                    Toast.makeText(
                        context,
                        i18N["ui.common.copied.to.clipboard"],
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                    TipBody(text = LocalI18N.current["ui.global.var.access.help.message", mapOf("name" to "$LOCAL_VAR_REF_PREFIX${state.name}")])
                }


                LargeSpacer()

                SmallTitle(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = LocalI18N.current["ui.local.var.type"]
                )
                Box(modifier = Modifier) {
                    val typeDropdownState = rememberDropdownSelectorState()

                    DropdownSelector(
                        state = typeDropdownState,
                        items = GlobalVarType.values().map {
                            DropdownItem(
                                labelLines = listOf(it.label()),
                                data = it
                            )
                        }
                    ) {
                        state.type = it.data
                    }

                    FilledTonalButton(onClick = {
                        typeDropdownState.open()
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = state.type.label())
                            LargeSpacer()
                            LargeSpacer()
                            RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                        }
                    }
                }
            }
        })
    }
}
package tornaco.apps.shortx.ui.gvar


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Switch
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
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.GLOBAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.GlobalVarType
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
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.UnderlineSmallText
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.util.ClipboardUtils

data class NewGlobalVar(
    val name: String,
    val type: GlobalVarType,
    val isSecret: Boolean
)

class NewGlobalVarState(
    val onSelected: (NewGlobalVar) -> Unit
) : CommonDialogState() {
    var name by mutableStateOf("")
    var type by mutableStateOf(GlobalVarType.String)
    var isSecret by mutableStateOf(false)

    fun show(current: NewGlobalVar? = null) {
        this.name = current?.name ?: ""
        this.type = current?.type ?: GlobalVarType.String
        this.isSecret = current?.isSecret ?: false

        show()
    }
}

@Composable
fun rememberNewGlobalVarState(
    onSelected: (NewGlobalVar) -> Unit
): NewGlobalVarState {
    return remember {
        NewGlobalVarState(onSelected)
    }
}

fun NewGlobalVar.toGlobalVar(): GlobalVar =
    GlobalVar
        .newBuilder()
        .setName(name)
        .setType(type.toProtoType())
        .setIsSecret(isSecret)
        .build()

@Composable
fun NewGlobalVarDialog(state: NewGlobalVarState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.global.var.title"])
        }, buttons = {
            TextButton(onClick = {
                if (state.name.isNotBlank()) {
                    state.onSelected(
                        NewGlobalVar(
                            state.name.trim(),
                            state.type,
                            state.isSecret
                        )
                    )
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
                    text = LocalI18N.current["ui.global.var.name"]
                )
                SmallSpacer()
                TipBody(text = LocalI18N.current["ui.global.var.name.description"])
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
                        "$GLOBAL_VAR_REF_PREFIX${state.name}"
                    )
                    Toast.makeText(
                        context,
                        i18N["ui.common.copied.to.clipboard"],
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                    UnderlineSmallText(
                        text = LocalI18N.current["ui.global.var.access.help.message", mapOf(
                            "name" to "$GLOBAL_VAR_REF_PREFIX${state.name}"
                        )]
                    )
                }

                LargeSpacer()

                SmallTitle(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = LocalI18N.current["ui.global.var.type"]
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

                LargeSpacer()

                // Secret
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmallTitle(text = LocalI18N.current["ui.global.var.secrets.title"])
                    Switch(checked = state.isSecret, onCheckedChange = {
                        state.isSecret = it
                    })
                }
                SmallSpacer()
                TipRow(text = LocalI18N.current["ui.global.var.secrets.message"])
            }
        })
    }
}
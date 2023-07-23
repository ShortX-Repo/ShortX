package tornaco.apps.shortx.ui.gvar


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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.GlobalVarType
import tornaco.apps.shortx.core.rule.label
import tornaco.apps.shortx.core.rule.toProtoType
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState

class NewGlobalVarState(
    val onSelected: (Pair<String, GlobalVarType>) -> Unit
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    fun show() {
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberNewGlobalVarState(
    onSelected: (Pair<String, GlobalVarType>) -> Unit
): NewGlobalVarState {
    return remember {
        NewGlobalVarState(onSelected)
    }
}

fun Pair<String, GlobalVarType>.toGlobalVar(): GlobalVar =
    GlobalVar
        .newBuilder()
        .setName(first)
        .setType(second.toProtoType())
        .build()

@Composable
fun NewGlobalVarDialog(state: NewGlobalVarState) {
    var name by remember(state) {
        mutableStateOf("")
    }
    var type by remember(state) {
        mutableStateOf(GlobalVarType.String)
    }
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.global.var.title"])
        }, buttons = {
            TextButton(onClick = {
                if (name.isNotBlank()) {
                    state.onSelected(name.trim() to type)
                    state.dismiss()
                }
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {

                SmallTitle(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = LocalI18N.current["ui.global.var.name"]
                )
                TipBody(text = LocalI18N.current["ui.global.var.name.description"])
                ShortXTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = name,
                    showSymbolButton = false,
                    onValueChange = {
                        name = it
                    })

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
                        type = it.data
                    }

                    FilledTonalButton(onClick = {
                        typeDropdownState.open()
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = type.label())
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
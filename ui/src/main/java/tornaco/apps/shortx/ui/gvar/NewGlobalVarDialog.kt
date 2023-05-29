package tornaco.apps.shortx.ui.gvar


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.GlobalVarType
import tornaco.apps.shortx.core.rule.label
import tornaco.apps.shortx.core.rule.toProtoType
import tornaco.apps.shortx.ui.base.*

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

@OptIn(ExperimentalMaterial3Api::class)
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

                SmallTitle(text = LocalI18N.current["ui.global.var.name"])
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

                SmallTitle(text = LocalI18N.current["ui.global.var.type"])
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
package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.ui.base.ShortXDialog

class CurrentPkgSelectorState(val selected: (Pkg) -> Unit) {
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
fun rememberCurrentPkgSelectorState(selected: (Pkg) -> Unit): CurrentPkgSelectorState {
    return remember {
        CurrentPkgSelectorState(selected)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentPkgSelector(state: CurrentPkgSelectorState) {
    var pkg by remember(state) {
        mutableStateOf<Pkg?>(null)
    }
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            Text(text = "Key Event")
        }, buttons = {
            TextButton(onClick = {
                pkg?.let {
                    state.selected(it)
                    state.dismiss()
                }
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Tag, contentDescription = "Key Code")
                    },
                    shape = TextFieldDefaults.outlinedShape,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent),
                    label = {
                        Text(text = "Component")
                    },
                    value = pkg?.pkgName ?: "",
                    onValueChange = {
                        pkg = Pkg(it, 0)
                    })
            }
        })
    }
}
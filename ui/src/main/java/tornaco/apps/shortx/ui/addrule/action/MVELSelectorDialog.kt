package tornaco.apps.shortx.ui.addrule.action


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.ShortXDialog

class MVELSelectorState(val onSelected: (String) -> Unit) {
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
fun rememberMVELSelectorState(onSelected: (String) -> Unit): MVELSelectorState {
    return remember {
        MVELSelectorState(onSelected)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MVELSelector(state: MVELSelectorState) {
    var expressionString by remember(state) {
        mutableStateOf("")
    }
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = "MVEL expression")
        }, buttons = {
            TextButton(onClick = {
                if (expressionString.isNotBlank()) {
                    state.onSelected(expressionString)
                    state.dismiss()
                }
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Tag, contentDescription = "Expression")
                    },
                    shape = TextFieldDefaults.outlinedShape,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    ),
                    label = {
                        Text(text = "Expression")
                    },
                    value = expressionString,
                    onValueChange = {
                        expressionString = it
                    })
            }
        })
    }
}
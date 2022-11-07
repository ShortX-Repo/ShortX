package tornaco.apps.shortx.ui.main.actions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.core.action.StartActivity
import tornaco.apps.shortx.ui.R
import tornaco.apps.shortx.ui.base.ShortXDialog

class StartActivitySelectorState(val onActivitySelected: (StartActivity) -> Unit) {
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
fun rememberStartActivitySelectorState(onActivitySelected: (StartActivity) -> Unit): StartActivitySelectorState {
    return remember {
        StartActivitySelectorState(onActivitySelected)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartActivitySelector(state: StartActivitySelectorState) {
    var componentNameString by remember(state) {
        mutableStateOf("")
    }
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            Text(text = "Start Activity")
        }, buttons = {
            TextButton(onClick = {
                if (componentNameString.isNotBlank()) {
                    val startActivity = StartActivity(componentNameString)
                    state.onActivitySelected(startActivity)
                    state.dismiss()
                }
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.ic_baseline_category_24),
                            contentDescription = "")
                    },
                    shape = TextFieldDefaults.outlinedShape,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent),
                    label = {
                        Text(text = "Component")
                    },
                    value = componentNameString,
                    onValueChange = {
                        componentNameString = it
                    })

                TextField(
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent),
                    label = {
                        Text(text = "Extras")
                    },
                    value = "",
                    onValueChange = {})
            }
        })
    }
}
package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog

class OnOffAnyStateSelectorState(
    val title: String, val selected: (OnOffAny) -> Unit
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
fun rememberOnOffAnyStateSelectorState(
    title: String, selected: (OnOffAny) -> Unit
): OnOffAnyStateSelectorState {
    return remember {
        OnOffAnyStateSelectorState(title, selected)
    }
}

@Composable
fun OnOffAnyStateSelectorDialog(state: OnOffAnyStateSelectorState) {
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = state.title)
        }, buttons = {
            TextButton(onClick = {
                state.dismiss()
            }) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                listOf(OnOffAny.On, OnOffAny.Off, OnOffAny.Any).forEach {
                    ListItem(it.label(LocalI18N.current)) {
                        state.selected(it)
                        state.dismiss()
                    }
                }
            }
        })
    }
}
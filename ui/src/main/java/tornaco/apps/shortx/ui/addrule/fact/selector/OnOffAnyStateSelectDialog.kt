package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.ui.addrule.fact.mapper.label
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXTextButton

class OnOffAnyStateSelectorState(
    val title: String,
    val options: List<OnOffAny>,
    val selected: (OnOffAny) -> Unit
) : CommonDialogState()

@Composable
fun rememberOnOffAnyStateSelectorState(
    title: String,
    options: List<OnOffAny> = listOf(OnOffAny.On, OnOffAny.Off, OnOffAny.Any),
    selected: (OnOffAny) -> Unit
): OnOffAnyStateSelectorState {
    return remember {
        OnOffAnyStateSelectorState(title, options, selected)
    }
}

@Composable
fun OnOffAnyStateSelectorDialog(state: OnOffAnyStateSelectorState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = state.title)
        }, buttons = {
            ShortXTextButton(onClick = {
                state.dismiss()
            }) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                state.options.forEach {
                    ListItem(title = it.label(LocalI18N.current)) {
                        state.selected(it)
                        state.dismiss()
                    }
                }
            }
        })
    }
}
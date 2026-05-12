package tornaco.apps.shortx.ui.discover

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogMessage
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXTextButton
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.settings.pref.DiscoveryRepoSetting

class NewDiscoveryRepoState(
    val onSelected: (DiscoveryRepoSetting) -> Unit
) : CommonDialogState() {
    var alias by mutableStateOf("")
    var url by mutableStateOf("")

    fun show(current: DiscoveryRepoSetting? = null) {
        alias = current?.alias ?: ""
        url = current?.url ?: ""
        show()
    }
}

@Composable
fun rememberNewDiscoveryRepoState(
    onSelected: (DiscoveryRepoSetting) -> Unit
): NewDiscoveryRepoState {
    return remember {
        NewDiscoveryRepoState(onSelected)
    }
}

@Composable
fun NewDiscoveryRepoDialog(state: NewDiscoveryRepoState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.discover.source.add.custom"])
        }, buttons = {
            ShortXTextButton(
                enabled = state.alias.trim().isNotEmpty() && state.url.trim().isNotEmpty(),
                onClick = {
                    state.onSelected(
                        DiscoveryRepoSetting(
                            alias = state.alias.trim(),
                            url = state.url.trim()
                        )
                    )
                    state.dismiss()
                }
            ) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                val i18N = LocalI18N.current

                FieldTitle(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = i18N["ui.discover.source.add.custom.alias"]
                )
                SmallSpacer()
                DialogMessage(text = i18N["ui.discover.source.add.custom.alias.description"])
                SmallSpacer()
                ShortXTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.alias,
                    showSymbolButton = false,
                    onValueChange = {
                        state.alias = it
                    }
                )

                MediumSpacer()

                FieldTitle(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = i18N["ui.discover.source.add.custom.url"]
                )
                SmallSpacer()
                DialogMessage(text = i18N["ui.discover.source.add.custom.description"])
                SmallSpacer()
                ShortXTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.url,
                    showSymbolButton = false,
                    onValueChange = {
                        state.url = it
                    }
                )
            }
        })
    }
}
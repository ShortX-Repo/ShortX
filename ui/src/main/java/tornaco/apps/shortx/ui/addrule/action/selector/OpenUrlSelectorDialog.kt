@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.i18n.I18N_R
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer

class OpenUrlSelectorState(
    val onSelected: (Action.OpenUrl) -> Unit
) : CommonDialogState() {
    var url by mutableStateOf("")
    var browserPkg by mutableStateOf("")

    fun show(current: Action.OpenUrl? = null) {
        this.url = current?.url ?: ""
        this.browserPkg = current?.browserPkg ?: ""
        super.show()
    }
}

@Composable
fun rememberOpenUrlSelectorState(
    onSelected: (Action.OpenUrl) -> Unit
): OpenUrlSelectorState {
    return OpenUrlSelectorState(onSelected)
}

@Composable
fun OpenUrlSelectorDialog(state: OpenUrlSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            OpenUrlSelector(state)
        }
    }
}

@Composable
private fun OpenUrlSelector(state: OpenUrlSelectorState) {
    val i18N = LocalI18N.current
    Scaffold(
        topBar = {
            ShortXTopAppBar(
                title = { Text(text = i18N.get(key = I18N_R.UI_ACTION_OPEN_URL, fallback = "Open URL")) },
                navigationIcon = {
                    IconButton(onClick = { state.dismiss() }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        state.onSelected(
                            Action.OpenUrl(
                                url = state.url,
                                browserPkg = state.browserPkg
                            )
                        )
                        state.dismiss()
                    }) {
                        Icon(imageVector = Icons.Outlined.Check, contentDescription = "Done")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            ShortXTextField(
                value = state.url,
                onValueChange = { state.url = it },
                label = {
                    Text(
                        i18N.get(
                            key = I18N_R.UI_ACTION_OPEN_URL_URL,
                            fallback = "URL (supports variables)"
                        )
                    )
                },
                showSymbolButton = true,
                modifier = Modifier.fillMaxWidth()
            )

            StandardSpacer()

            ShortXTextField(
                value = state.browserPkg,
                onValueChange = { state.browserPkg = it },
                label = {
                    Text(
                        i18N.get(
                            key = I18N_R.UI_ACTION_OPEN_URL_BROWSER_PKG,
                            fallback = "Browser package name (optional, blank = system default)"
                        )
                    )
                },
                showSymbolButton = true,
                modifier = Modifier.fillMaxWidth()
            )

            StandardSpacer()
        }
    }
}

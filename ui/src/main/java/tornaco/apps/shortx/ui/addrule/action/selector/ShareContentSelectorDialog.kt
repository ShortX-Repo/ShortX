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
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer


class ShareContentSelectorState(
    val onSelected: (Action.ShareContent) -> Unit
) : CommonDialogState() {
    var text by mutableStateOf("")
    var filePath by mutableStateOf("")
    var mimeType by mutableStateOf("")
    var chooserTitle by mutableStateOf("")

    fun show(current: Action.ShareContent? = null) {
        this.text = current?.text ?: ""
        this.filePath = current?.filePath ?: ""
        this.mimeType = current?.mimeType ?: ""
        this.chooserTitle = current?.chooserTitle ?: ""
        super.show()
    }
}

@Composable
fun rememberShareContentSelectorState(
    onSelected: (Action.ShareContent) -> Unit
): ShareContentSelectorState {
    return ShareContentSelectorState(onSelected)
}

@Composable
fun ShareContentSelectorDialog(state: ShareContentSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShareContentSelector(state)
        }
    }
}

@Composable
private fun ShareContentSelector(state: ShareContentSelectorState) {
    val i18N = LocalI18N.current
    Scaffold(
        topBar = {
            ShortXTopAppBar(
                title = { Text(text = i18N.get(key = "ui.action.share.content", fallback = "Share content")) },
                navigationIcon = {
                    IconButton(onClick = { state.dismiss() }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        state.onSelected(
                            Action.ShareContent(
                                text = state.text,
                                filePath = state.filePath,
                                mimeType = state.mimeType,
                                chooserTitle = state.chooserTitle
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
                value = state.text,
                onValueChange = { state.text = it },
                label = { Text("Text (supports variables)") },
                showSymbolButton = true,
                modifier = Modifier.fillMaxWidth()
            )

            StandardSpacer()

            ShortXTextField(
                value = state.filePath,
                onValueChange = { state.filePath = it },
                label = { Text("File path (absolute, supports variables)") },
                showSymbolButton = true,
                modifier = Modifier.fillMaxWidth()
            )

            StandardSpacer()

            ShortXTextField(
                value = state.mimeType,
                onValueChange = { state.mimeType = it },
                label = { Text("MIME type (optional, auto-detected)") },
                showSymbolButton = true,
                modifier = Modifier.fillMaxWidth()
            )

            StandardSpacer()

            ShortXTextField(
                value = state.chooserTitle,
                onValueChange = { state.chooserTitle = it },
                label = { Text("Chooser title (optional)") },
                showSymbolButton = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

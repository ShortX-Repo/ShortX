package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TopBar
import tornaco.apps.shortx.ui.base.LocalI18N


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
            TopBar(
                title = i18N.get(key = "ui.action.share.content", fallback = "Share content"),
                onClose = { state.dismiss() },
                onConfirm = {
                    state.onSelected(
                        Action.ShareContent(
                            text = state.text,
                            filePath = state.filePath,
                            mimeType = state.mimeType,
                            chooserTitle = state.chooserTitle
                        )
                    )
                    state.dismiss()
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
                label = { "Text (supports variables)" },
                showSymbolButton = true,
                modifier = Modifier.fillMaxWidth()
            )

            StandardSpacer()

            ShortXTextField(
                value = state.filePath,
                onValueChange = { state.filePath = it },
                label = { "File path (absolute, supports variables)" },
                showSymbolButton = true,
                modifier = Modifier.fillMaxWidth()
            )

            StandardSpacer()

            ShortXTextField(
                value = state.mimeType,
                onValueChange = { state.mimeType = it },
                label = { "MIME type (optional, auto-detected)" },
                showSymbolButton = true,
                modifier = Modifier.fillMaxWidth()
            )

            StandardSpacer()

            ShortXTextField(
                value = state.chooserTitle,
                onValueChange = { state.chooserTitle = it },
                label = { "Chooser title (optional)" },
                showSymbolButton = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

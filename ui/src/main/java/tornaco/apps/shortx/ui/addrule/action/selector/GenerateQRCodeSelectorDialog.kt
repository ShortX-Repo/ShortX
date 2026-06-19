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
import tornaco.apps.shortx.core.i18n.I18N_R

class GenerateQRCodeSelectorState(
    val onSelected: (Action.GenerateQRCode) -> Unit
) : CommonDialogState() {
    var text by mutableStateOf("")
    var targetDirPath by mutableStateOf("")

    fun show(current: Action.GenerateQRCode? = null) {
        this.text = current?.text ?: ""
        this.targetDirPath = current?.targetDirPath ?: ""
        super.show()
    }
}

@Composable
fun rememberGenerateQRCodeSelectorState(
    onSelected: (Action.GenerateQRCode) -> Unit
): GenerateQRCodeSelectorState {
    return GenerateQRCodeSelectorState(onSelected)
}

@Composable
fun GenerateQRCodeSelectorDialog(state: GenerateQRCodeSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            GenerateQRCodeSelector(state)
        }
    }
}

@Composable
private fun GenerateQRCodeSelector(state: GenerateQRCodeSelectorState) {
    val i18N = LocalI18N.current
    Scaffold(
        topBar = {
            ShortXTopAppBar(
                title = { Text(text = i18N.get(key = I18N_R.UI_ACTION_GENERATE_QRCODE, fallback = "Generate QR code")) },
                navigationIcon = {
                    IconButton(onClick = { state.dismiss() }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        state.onSelected(
                            Action.GenerateQRCode(
                                text = state.text,
                                targetDirPath = state.targetDirPath
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
                label = {
                    Text(
                        i18N.get(
                            key = I18N_R.UI_ACTION_GENERATE_QRCODE_TEXT,
                            fallback = "Text to encode (supports variables)"
                        )
                    )
                },
                showSymbolButton = true,
                modifier = Modifier.fillMaxWidth()
            )

            StandardSpacer()

            ShortXTextField(
                value = state.targetDirPath,
                onValueChange = { state.targetDirPath = it },
                label = {
                    Text(
                        i18N.get(
                            key = I18N_R.UI_ACTION_GENERATE_QRCODE_TARGET_DIR,
                            fallback = "Target directory (optional, supports variables)"
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

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

class TakePhotoSelectorState(
    val onSelected: (Action.TakePhoto) -> Unit
) : CommonDialogState() {
    var targetFilePath by mutableStateOf("")

    fun show(current: Action.TakePhoto? = null) {
        this.targetFilePath = current?.targetFilePath ?: ""
        super.show()
    }
}

@Composable
fun rememberTakePhotoSelectorState(
    onSelected: (Action.TakePhoto) -> Unit
): TakePhotoSelectorState {
    return TakePhotoSelectorState(onSelected)
}

@Composable
fun TakePhotoSelectorDialog(state: TakePhotoSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            TakePhotoSelector(state)
        }
    }
}

@Composable
private fun TakePhotoSelector(state: TakePhotoSelectorState) {
    val i18N = LocalI18N.current
    Scaffold(
        topBar = {
            ShortXTopAppBar(
                title = { Text(text = i18N.get(key = I18N_R.UI_ACTION_TAKE_PHOTO, fallback = "Take photo")) },
                navigationIcon = {
                    IconButton(onClick = { state.dismiss() }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        state.onSelected(
                            Action.TakePhoto(
                                targetFilePath = state.targetFilePath
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
                value = state.targetFilePath,
                onValueChange = { state.targetFilePath = it },
                label = {
                    Text(
                        i18N.get(
                            key = I18N_R.UI_ACTION_TAKE_PHOTO_TARGET_PATH,
                            fallback = "Target file path (optional, supports variables)"
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

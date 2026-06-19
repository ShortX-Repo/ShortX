package tornaco.apps.shortx.ui.addrule

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.core.i18n.I18N_R


class ImagePreviewDialogState(
    val edit: () -> Unit
) : CommonDialogState() {
    var base64 by mutableStateOf("")

    fun show(
        base64: String,
    ) {
        this.base64 = base64

        super.show()
    }
}

@Composable
fun rememberImagePreviewDialogState(
    edit: () -> Unit
): ImagePreviewDialogState {
    return remember {
        ImagePreviewDialogState(edit)
    }
}

@Composable
fun ImagePreviewDialog(state: ImagePreviewDialogState) {
    if (state.isShowing) {
        ShortXDialog(
            onDismissRequest = {
                state.dismiss()
            },
            title = {},
            buttons = {
                TextButton(onClick = {
                    state.dismiss()
                    state.edit()
                }) {
                    Text(text = LocalI18N.current[I18N_R.UI_COMMON_EDIT])
                }
            }
        ) {
            Column {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    bitmap = BitmapUtils.base64ToBitmap(state.base64).asImageBitmap(),
                    contentDescription = ""
                )
            }
        }
    }
}

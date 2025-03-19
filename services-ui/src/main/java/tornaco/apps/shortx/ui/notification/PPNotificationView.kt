package tornaco.apps.shortx.ui.notification

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.ui.dialog.ShadowDialogView

@SuppressLint("ViewConstructor")
class PPNotificationView(
    context: Context,
    i18N: I18N,
    private val onDismissRequest: () -> Unit,
    private val onClick: () -> Unit,
    private val onLongClick: () -> Unit,
) : ShadowDialogView(context, i18N, DialogUiStyleSettings.getDefaultInstance()) {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun DialogContent() {
        Column(
            modifier = Modifier
                .combinedClickable(onClick = {
                    onClick()

                }, onLongClick = {
                    onLongClick()
                })
                .padding(16.dp)
        ) {
            Text("Hello there.")
            Text("How is it going?")
        }
    }
}
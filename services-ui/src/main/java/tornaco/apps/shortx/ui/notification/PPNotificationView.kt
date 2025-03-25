package tornaco.apps.shortx.ui.notification

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.action.ShowPPN
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.dialog.ShadowDialogView

@SuppressLint("ViewConstructor")
class PPNotificationView(
    context: Context,
    i18N: I18N,
    private val showPPN: ShowPPN,
    private val onDismissRequest: (ShowPPN) -> Unit,
    private val onClick: (ShowPPN) -> Unit,
    private val onLongClick: (ShowPPN) -> Unit,
) : ShadowDialogView(context, i18N, DialogUiStyleSettings.getDefaultInstance()) {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun DialogContent() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(onClick = {
                    onClick(showPPN)

                }, onLongClick = {
                    onLongClick(showPPN)
                })
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(showPPN.icon.isNotEmpty()) {
                ColoredIcon(
                    modifier = Modifier.padding(end = 16.dp),
                    color = ButtonDefaults.filledTonalButtonColors().containerColor,
                    size = 48.dp
                ) {
                    RemixIcon(
                        remixName = showPPN.icon, modifier = Modifier.size(24.dp),
                        tint = ButtonDefaults.filledTonalButtonColors().contentColor
                    )
                }
            }
            Column {
                Text(showPPN.title)
                Text(showPPN.message, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
package tornaco.apps.shortx.ui.addrule

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.util.ClipboardUtils


class ContextDataInfoDialogState {
    var isShowing by mutableStateOf(false)

    var contextData: List<ContextData> = emptyList()
    var objName: String = ""

    fun show(objName: String, contextData: List<ContextData>) {
        this.contextData = contextData
        this.objName = objName
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberContextDataInfoDialogState(
): ContextDataInfoDialogState {
    return remember {
        ContextDataInfoDialogState()
    }
}

@Composable
fun ContextDataInfoDialog(state: ContextDataInfoDialogState) {
    if (state.isShowing) {
        val i18N = LocalI18N.current
        ShortXDialog(
            title = {
                DialogTitle(text = i18N["ui.context.data.title"])
            },
            onDismissRequest = { state.dismiss() }) {

            DialogMessage(
                text = i18N["ui.context.data.message", mapOf(
                    "obj" to state.objName,
                    "accessor" to "{NAME}"
                )]
            )

            val context = LocalContext.current
            state.contextData.forEach {
                ContextDataItem(
                    title = it.key,
                    text1 = it.description,
                    text2 = i18N["ui.context.data.example", mapOf("eg" to it.accessExample)],
                    onClick = {
                        ClipboardUtils.copyToClipboard(context, "context", it.key)
                        Toast.makeText(
                            context,
                            i18N["ui.common.copied.to.clipboard"],
                            Toast.LENGTH_LONG
                        ).show()
                    })
            }
        }
    }
}


@Composable
private fun ContextDataItem(
    title: String? = null,
    text1: String? = null,
    text2: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(end = 24.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier) {
                title?.let {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 14.sp,
                        )
                    )
                }
                TinySpacer()
                text1?.let {
                    Text(
                        text = text1,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        maxLines = 12
                    )
                }
                TinySpacer()
                text2?.let {
                    Text(
                        text = text2,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        maxLines = 12
                    )
                }
            }
        }
    }
}

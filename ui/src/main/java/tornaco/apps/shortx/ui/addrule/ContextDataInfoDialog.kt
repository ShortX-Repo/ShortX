package tornaco.apps.shortx.ui.addrule

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import tornaco.apps.shortx.core.context.ContextDataMapping.asAccessExample
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.toStringPair
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogMessage
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MarkdownText
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ClipboardUtils


class ContextDataInfoDialogState : CommonDialogState() {
    var contextData: List<ContextData> by mutableStateOf(emptyList())
    var objName: String = ""

    private var setCustomKey: (StringPair) -> Unit = {}
    var canSetCustomKey: Boolean = false

    fun show(
        objName: String,
        contextData: List<ContextData>,
        canSetCustomKey: Boolean = false,
        setCustomKey: (StringPair) -> Unit = {}
    ) {
        this.contextData = contextData
        this.objName = objName
        this.canSetCustomKey = canSetCustomKey
        this.setCustomKey = setCustomKey

        show()
    }

    fun updateCustomKey(key: StringPair) {
        contextData = contextData.toMutableList().apply {
            val current = this.first { it.key == key.first }
            val currentIndex = this.indexOf(current)
            set(currentIndex, current.copy(customKey = key.second))
        }
        setCustomKey(key)
    }
}

@Composable
fun rememberContextDataInfoDialogState(
): ContextDataInfoDialogState {
    return remember {
        ContextDataInfoDialogState()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContextDataInfoDialog(state: ContextDataInfoDialogState) {
    if (state.isShowing) {
        val i18N = LocalI18N.current

        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    ShortXAppBarScaffold(
                        title = LocalI18N.current["ui.context.data.title"],
                        onBackPressed = { state.dismiss() },
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 120.dp),
                            modifier = Modifier.heightIn(max = 1000.dp)
                        ) {
                            item {
                                Box(modifier = Modifier.padding(16.dp)) {
                                    DialogMessage(
                                        text = i18N["ui.context.data.message", mapOf(
                                            "obj" to state.objName,
                                            "accessor" to "{NAME}"
                                        )]
                                    )
                                }
                            }
                            items(state.contextData) { contextData ->
                                val context = LocalContext.current
                                ContextDataItem(
                                    title = contextData.selectedKey,
                                    text1 = contextData.description,
                                    text2 = i18N["ui.context.data.example", mapOf("eg" to contextData.selectedKey.asAccessExample())],
                                    text1IsMD = contextData.descriptionIsMarkdown,
                                    onClick = {
                                        ClipboardUtils.copyToClipboard(
                                            context,
                                            "context",
                                            contextData.selectedKey.asAccessExample()
                                        )
                                        Toast.makeText(
                                            context,
                                            i18N["ui.common.copied.to.clipboard"],
                                            Toast.LENGTH_LONG
                                        ).show()
                                    },
                                    canEditKey = state.canSetCustomKey,
                                    isCustomKey = !contextData.customKey.isNullOrEmpty(),
                                    setCustomKey = {
                                        state.updateCustomKey((contextData.key to it).toStringPair())
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun ContextDataItem(
    title: String? = null,
    text1: String? = null,
    text2: String? = null,
    text1IsMD: Boolean = false,
    onClick: () -> Unit,
    canEditKey: Boolean,
    isCustomKey: Boolean,
    setCustomKey: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .background(ColorDefaults.backgroundSurfaceColor())
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
                    Row {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp,
                            )
                        )
                        MediumSpacer()
                        if (isCustomKey) OutlineBadge(text = LocalI18N.current["ui.context.data.custom.hint"])
                    }
                }
                TinySpacer()
                text1?.let {
                    if (text1IsMD) {
                        val context = LocalContext.current
                        val textColor = LocalContentColor.current.takeOrElse {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                        MarkdownText(
                            markdown = text1,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = textColor
                            ),
                            onLinkClicked = {
                                kotlin.runCatching {
                                    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                                        data = it.toUri()
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    })
                                }
                            },
                            onClick = {
                                onClick()
                            }
                        )
                    } else {
                        Text(
                            text = text1,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                            maxLines = 12
                        )
                    }
                }
                MediumSpacer()
                text2?.let {
                    Text(
                        text = text2,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.W500
                        ),
                        maxLines = 12
                    )
                }
            }
        }

        val textInputDialogState =
            rememberTextInputState(
                title = LocalI18N.current["ui.context.data.custom.key"],
                message = null,
                onSelected = {
                    setCustomKey(it)
                })
        TextInputDialog(state = textInputDialogState)
        if (canEditKey) {
            IconButton(onClick = {
                textInputDialogState.show()
            }) {
                RemixIcon(remixName = Remix.Document.file_edit_line)
            }
        }
    }
}

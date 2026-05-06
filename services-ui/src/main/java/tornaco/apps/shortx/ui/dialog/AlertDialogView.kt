@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings

@SuppressLint("ViewConstructor")
class AlertDialogView(
    context: Context,
    i18N: I18N,
    style: DialogUiStyleSettings,
    private val onDismissRequest: (DialogButton) -> Unit,
    private val title: String,
    private val message: String,
    private val positiveText: String?,
    private val negativeText: String?,
    private val neutralText: String?,
) : DialogView(context, i18N, style) {
    @Composable
    override fun DialogContent() {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
                    .heightIn(max = 800.dp)
                    .padding(horizontal = 8.dp),
            ) {
                stickyHeader {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            title,
                            style = MaterialTheme.typography.headlineSmall
                                .copy(fontWeight = FontWeight.W500)
                                .applyStyle(style)
                        )
                    }
                }

                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(message, style = MaterialTheme.typography.bodyMedium.applyStyle(style))
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (neutralText != null) {
                    TextButton(onClick = {
                        onDismissRequest(DialogButton.Neutral)
                    }) {
                        Text(
                            text = neutralText,
                            style = MaterialTheme.typography.labelLarge.applyStyle(style)
                        )
                    }
                } else {
                    Box(Modifier.size(24.dp))
                }

                Row(
                    modifier = Modifier
                        .weight(1f, fill = false),
                    horizontalArrangement = Arrangement.End
                ) {
                    negativeText?.let {
                        TextButton(onClick = {
                            onDismissRequest(DialogButton.Negative)
                        }) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelLarge.applyStyle(style)
                            )
                        }
                    }
                    positiveText?.let {
                        TextButton(onClick = {
                            onDismissRequest(DialogButton.Positive)
                        }) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelLarge.applyStyle(style)
                            )
                        }
                    }
                }
            }
        }
    }
}
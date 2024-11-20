@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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

@SuppressLint("ViewConstructor")
class AlertDialogView(
    context: Context,
    i18N: I18N,
    private val onDismissRequest: (DialogButton) -> Unit,
    private val title: String,
    private val message: String,
    private val positiveText: String?,
    private val negativeText: String?,
    private val neutralText: String?,
) : DialogView(context, i18N) {
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
                    .heightIn(max = 800.dp),
            ) {
                stickyHeader {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.W500)
                        )
                    }
                }

                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(message, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                neutralText?.let {
                    TextButton(onClick = {
                        onDismissRequest(DialogButton.Neutral)
                    }) {
                        Text(it)
                    }
                }

                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    horizontalArrangement = Arrangement.End
                ) {
                    negativeText?.let {
                        TextButton(onClick = {
                            onDismissRequest(DialogButton.Negative)
                        }) {
                            Text(it)
                        }
                    }
                    positiveText?.let {
                        TextButton(onClick = {
                            onDismissRequest(DialogButton.Positive)
                        }) {
                            Text(it)
                        }
                    }
                }
            }
        }
    }
}
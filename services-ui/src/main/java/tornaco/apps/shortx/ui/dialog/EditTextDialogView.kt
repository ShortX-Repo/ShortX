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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.ui.base.ShortXTextButton
import tornaco.apps.shortx.ui.base.ShortXTextField

@SuppressLint("ViewConstructor")
class EditTextDialogView(
    context: Context,
    i18N: I18N,
    style: DialogUiStyleSettings,
    private val onDismissRequest: (DialogButton) -> Unit,
    private val title: String,
    private val value: String,
    private val placeholder: String? = null,
    private val onValueChange: (String) -> Unit,
) : DialogView(context, i18N, style) {
    @Composable
    override fun DialogContent() {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            var valueState by remember { mutableStateOf(value) }
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
                    .heightIn(max = 800.dp),
            ) {
                stickyHeader {
                    Text(
                        title, style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.W500
                        ).applyStyle(style),
                        modifier = Modifier.padding(24.dp)
                    )
                }
                item {
                    ShortXTextField(
                        value = valueState,
                        placeholder = placeholder?.let {
                            {
                                Text(placeholder)
                            }
                        },
                        onValueChange = {
                            valueState = it
                            onValueChange(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        showSymbolButton = false
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                ShortXTextButton(onClick = {
                    onDismissRequest(DialogButton.Negative)
                }) {
                    Text(
                        text = stringResource(android.R.string.cancel),
                        style = MaterialTheme.typography.labelLarge.applyStyle(style)
                    )
                }
                ShortXTextButton(onClick = {
                    onDismissRequest(DialogButton.Positive)
                }) {
                    Text(
                        text = stringResource(android.R.string.ok),
                        style = MaterialTheme.typography.labelLarge.applyStyle(style)
                    )
                }
            }
        }
    }
}
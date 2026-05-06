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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.ui.base.ListItem

data class ChoiceItem(
    val id: Int,
    val label: String,
    val value: String,
    val isSelectedByDefault: Boolean,
    val summary: String? = null,
)

@SuppressLint("ViewConstructor")
class ChoiceDialogView(
    context: Context,
    i18N: I18N,
    style: DialogUiStyleSettings,
    private val onDismissRequest: () -> Unit,
    private val title: String,
    private val multipleSelection: Boolean,
    private val choices: List<ChoiceItem>,
    // Selected id.
    private val onSelectionChange: (List<Int>) -> Unit,
) : DialogView(context, i18N, style) {
    @Composable
    override fun DialogContent() {
        val selectedValues = remember {
            mutableStateListOf(
                *choices.filter { it.isSelectedByDefault }.map { it.id }.take(
                    if (multipleSelection) Int.MAX_VALUE else 1
                ).toTypedArray()
            )
        }
        LaunchedEffect(Unit) {
            onSelectionChange(selectedValues)
        }
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    title, style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.W500
                    ).applyStyle(style)
                )
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
                    .heightIn(max = 800.dp),
            ) {
                itemsIndexed(
                    items = choices,
                    key = { index, item -> "$index-${item.id}" }) { _, choiceItem ->
                    val isSelected = selectedValues.contains(choiceItem.id)
                    val handleCheckClick = {
                        if (!multipleSelection) {
                            selectedValues.clear()
                            selectedValues.add(choiceItem.id)
                            onSelectionChange(selectedValues)
                        } else {
                            if (isSelected) {
                                selectedValues.remove(choiceItem.id)
                            } else {
                                selectedValues.add(choiceItem.id)
                            }
                            onSelectionChange(selectedValues)
                        }
                    }
                    ListItem(
                        titleLines = listOfNotNull(
                            choiceItem.label,
                            choiceItem.summary,
                            choiceItem.value
                        ),
                        action1 = {
                            if (multipleSelection) {
                                Checkbox(isSelected, onCheckedChange = {
                                    handleCheckClick()
                                })
                            } else {
                                RadioButton(isSelected, onClick = {
                                    handleCheckClick()
                                })
                            }
                        },
                        onClick = {
                            handleCheckClick()
                        }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp), horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    onDismissRequest()
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
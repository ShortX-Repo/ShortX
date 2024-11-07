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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.rememberSearchBarState

@SuppressLint("ViewConstructor")
class ListDialogView(
    context: Context,
    i18N: I18N,
    private val onDismissRequest: () -> Unit,
    private val title: String,
    private val multipleSelection: Boolean,
    private val defaultSelectedItems: List<String>,
    private val listItems: List<String>,
    private val onSelectionChange: (List<String>) -> Unit,
) : DialogView(context, i18N) {
    @Composable
    override fun DialogContent() {
        val data = remember { mutableStateListOf(*defaultSelectedItems.toTypedArray()) }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val searchBarState = rememberSearchBarState()
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
                        Column {
                            Text(
                                title, style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.W500
                                )
                            )
                            SmallSpacer()
                            SearchBar(
                                searchBarState = searchBarState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                            )
                        }
                    }
                }

                items(listItems.filter {
                    searchBarState.keyword.isBlank() || it.contains(
                        searchBarState.keyword,
                        true
                    )
                }) {
                    val isSelected = data.contains(it)
                    val handleCheckClick = {
                        if (!multipleSelection) {
                            data.clear()
                            data.add(it)
                            onSelectionChange(data)
                        } else {
                            if (isSelected) {
                                data.remove(it)
                            } else {
                                data.add(it)
                            }
                            onSelectionChange(data)
                        }
                    }
                    ListItem(
                        titleLines = listOf(it),
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = {
                    onDismissRequest()
                }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        }
    }
}

@Composable
@Preview
private fun DialogContentPreview() {
    ListDialogView(
        LocalContext.current,
        object : I18N {
            override fun get(key: String, args: Map<String, String>, fallback: String?): String {
                return key
            }
        },
        {},
        "Test",
        false,
        emptyList(),
        listOf("A"),
        {}).ComposeContent()
}
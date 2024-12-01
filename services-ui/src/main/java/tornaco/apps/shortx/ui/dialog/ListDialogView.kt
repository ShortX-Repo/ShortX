@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.core.util.ApkUtils
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.rememberSearchBarState

data class ListItem(
    val id: Int,
    val value: String,
    val lines: List<String>,
    val icon: String? = null,
)

@SuppressLint("ViewConstructor")
class ListDialogView(
    context: Context,
    i18N: I18N,
    style: DialogUiStyleSettings,
    private val onDismissRequest: () -> Unit,
    private val title: String,
    private val multipleSelection: Boolean,
    private val needConfirmAction: Boolean,
    private val defaultSelectedItems: List<Int>,
    private val listItems: List<ListItem>,
    private val onSelectionChange: (List<Int>) -> Unit,
) : DialogView(context, i18N, style) {
    @Composable
    override fun DialogContent() {
        val selectedIdList = remember { mutableStateListOf(*defaultSelectedItems.toTypedArray()) }
        val context = LocalContext.current

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val searchBarState = rememberSearchBarState()
            val listState = rememberLazyListState()
            val scope = rememberCoroutineScope()
            LazyColumn(
                state = listState,
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
                            TextButton(onClick = {
                                scope.launch { listState.scrollToItem(0) }
                            }) {
                                Text(
                                    title, style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.W500
                                    ).applyStyle(style)
                                )
                            }
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

                items(listItems.filter { listItem ->
                    searchBarState.keyword.isBlank() || listItem.lines.any {
                        it.contains(
                            searchBarState.keyword,
                            ignoreCase = true
                        )
                    }
                }) { listItem ->
                    val isSelected = selectedIdList.contains(listItem.id)
                    val handleCheckClick = {
                        if (multipleSelection) {
                            if (isSelected) {
                                selectedIdList.remove(listItem.id)
                            } else {
                                selectedIdList.add(listItem.id)
                            }
                            onSelectionChange(selectedIdList)
                        } else {
                            selectedIdList.clear()
                            selectedIdList.add(listItem.id)
                            onSelectionChange(selectedIdList)
                            if (!needConfirmAction) {
                                onDismissRequest()
                            }
                        }
                    }
                    ListItem(
                        titleLines = listItem.lines.takeIf { it.isNotEmpty() }
                            ?: listOf(listItem.value),
                        action1 = {
                            if (multipleSelection) {
                                Checkbox(isSelected, onCheckedChange = {
                                    handleCheckClick()
                                })
                            } else if (needConfirmAction) {
                                RadioButton(isSelected, onClick = {
                                    handleCheckClick()
                                })
                            } else {
                                // Noop.
                            }
                        },
                        icon = listItem.icon?.let { iconName ->
                            {
                                // app://com.tencent.mm
                                if (iconName.startsWith("app://")) {
                                    val pkgName = iconName.removePrefix("app://")
                                    val iconBitmap: Bitmap? = remember(iconName) {
                                        runCatching {
                                            val drawable =
                                                ApkUtils.loadIconByPkgName(context, pkgName)
                                            BitmapUtils.getBitmap(context, drawable)
                                        }
                                    }.getOrElse {
                                        Logger.nameless.e(it, "Get app icon")
                                        null
                                    }
                                    iconBitmap?.let {
                                        Image(
                                            it.asImageBitmap(),
                                            "App",
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                } else {
                                    RemixIcon(iconName)
                                }
                            }
                        },
                        onClick = {
                            handleCheckClick()
                        }
                    )
                }
            }
            if (needConfirmAction) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
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
}
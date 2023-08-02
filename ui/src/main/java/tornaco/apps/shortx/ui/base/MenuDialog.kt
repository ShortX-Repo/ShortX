package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.ui.util.BrowserUtils

@Composable
fun <T> MenuDialog(
    state: MenuDialogState<T>,
) {
    if (state.isShowing) {
        ShortXDialog(
            onDismissRequest = {
                state.dismiss()
            },

            title = {
                DialogTitle(text = state.title)
            }) {

            state.message?.let {
                DialogMessage(text = it)
                StandardSpacer()
            }
            state.menuItems.forEach { dialogItem ->
                ItemContent(
                    title = dialogItem.title,
                    summary = dialogItem.summary,
                    icon = {
                        dialogItem.iconName?.let {
                            RemixIcon(
                                remixName = it,
                                tint = dialogItem.textColor ?: LocalContentColor.current
                            )
                        }
                    },
                    textColor = dialogItem.textColor,
                    isSelected = state._currentSelection.contains(dialogItem.id),
                    customAction = dialogItem.customAction,
                    onClick = {
                        state.dismiss()
                        state.onItemSelected(state._data, dialogItem.id)
                    })
            }

        }
    }
}


@Composable
private fun ItemContent(
    icon: (@Composable () -> Unit)? = null,
    title: String? = null,
    summary: String? = null,
    textColor: Color? = null,
    isSelected: Boolean = false,
    customAction: (Pair<String, () -> Unit>)? = null,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .background(color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Unspecified)
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
            icon?.invoke()
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier) {
                title?.let {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 18.sp,
                        ),
                        color = textColor ?: Color.Unspecified
                    )
                }
                summary?.let {
                    Column {
                        TinySpacer()
                        MarkdownText(
                            markdown = summary,
                            fontSize = 12.sp,
                            color = textColor ?: Color.Unspecified,
                            onLinkClicked = {
                                BrowserUtils.launch(context, it)
                            },
                            onClick = {
                                onClick()
                            }
                        )
                    }
                }
            }
        }

        if (customAction != null) {
            IconButton(
                onClick = { customAction.second() }) {
                RemixIcon(
                    remixName = customAction.first,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

data class MenuDialogItem(
    val id: String,
    val title: String,
    val summary: String? = null,
    val iconName: String? = null,
    val textColor: Color? = null,
    val customAction: (Pair<String, () -> Unit>)? = null
)

open class MenuDialogState<DATA>(
    val title: String,
    val message: String? = null,
    val menuItems: List<MenuDialogItem>,
    val onItemSelected: (DATA?, String) -> Unit
) :
    CommonDialogState() {
    var _data: DATA? = null
    var _currentSelection: List<String> = emptyList()

    fun show(data: DATA? = null, currentSelection: List<String> = emptyList()) {
        _data = data
        _currentSelection = currentSelection

        show()
    }
}

@Composable
fun <T> rememberMenuDialogState(
    key1: Any? = null,
    title: String,
    message: String? = null,
    menuItems: List<MenuDialogItem>,
    onItemSelected: (T?, String) -> Unit
): MenuDialogState<T> {
    return remember(key1) {
        MenuDialogState(title, message, menuItems, onItemSelected)
    }
}



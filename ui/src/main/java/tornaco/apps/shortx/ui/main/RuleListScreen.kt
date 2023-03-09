package tornaco.apps.shortx.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.ui.base.*

@Composable
fun RuleScreen(
    state: MainState,
    onDelete: (RuleUM) -> Unit,
    onEdit: (RuleUM) -> Unit,
    onRuleClick: (RuleUM) -> Unit,
    setRuleEnabled: (String, Boolean) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp)
    ) {
        items(
            items = state.rules,
            key = { listItem: RuleUM -> listItem.id + listItem.updateTime }) { rule ->

            var scaleAnimStarted by remember {
                mutableStateOf(false)
            }
            val scalePercent by animateFloatAsState(
                targetValue = if (scaleAnimStarted) 0.99f else 1f,
                animationSpec = spring(),
                finishedListener = {
                    scaleAnimStarted = false
                }
            )

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .scale(scalePercent),
                backgroundColor = ColorDefaults.backgroundSurfaceColor(),
                shape = ShortXCardRoundedCornerShape,
                elevation = 0.dp
            ) {
                RuleItem(
                    rule = rule,
                    onClick = {
                        onRuleClick(rule)
                        scaleAnimStarted = true
                    },
                    setRuleEnabled = setRuleEnabled,
                    onDelete = onDelete,
                    onEdit = onEdit,
                )
            }
        }
    }
}

@Composable
private fun RuleItem(
    rule: RuleUM,
    onClick: () -> Unit,
    setRuleEnabled: (String, Boolean) -> Unit,
    onDelete: (RuleUM) -> Unit,
    onEdit: (RuleUM) -> Unit,
) {
    ListItem(
        icon = {
            ColoredIcon {
                Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = "Icon")
            }
        },
        title = rule.title,
        text1 = rule.description,
        text2 = LocalI18N.current["ui.update.time", mapOf("date" to rule.updateTime)],
        overflow = {
            ListItemOverflowMenu(rule, onDelete, onEdit)
        },
        checked = rule.isEnabled,
        onClick = onClick,
        onCheckedChange = { setRuleEnabled(rule.id, it) }
    )
}

@Composable
private fun ListItemOverflowMenu(
    rule: RuleUM,
    onDelete: (RuleUM) -> Unit,
    onEdit: (RuleUM) -> Unit
) {
    val deleteDialogState = rememberDeleteConfirmDialogState()
    DeleteConfirmDialog(
        state = deleteDialogState,
        data = rule,
        messageHint = { rule.title },
        onDelete = onDelete
    )

    Box {
        val menuState = rememberDropdownSelectorState()
        IconButton(
            modifier = Modifier,
            onClick = {
                menuState.open()
            }) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More")
        }

        DropdownSelector(
            widthFraction = 0.3f,
            state = menuState,
            items = listOf(
                DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.edit"]),
                    data = "edit",
                    icon = {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
                    }),
                DropdownItem(
                    labelLines = listOf(LocalI18N.current["ui.common.delete"]),
                    data = "delete",
                    icon = {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete")
                    })
            ), onSelect = {
                when (it.data) {
                    "delete" -> {
                        deleteDialogState.show()
                    }
                    "edit" -> {
                        onEdit(rule)
                    }
                    else -> {}
                }
            })
    }
}
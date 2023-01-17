package tornaco.apps.shortx.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Commit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.ui.base.*

@Composable
fun DirectActionListScreen(
    state: MainState,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
    onDirectActionClick: (DirectActionUM) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp)
    ) {
        items(
            items = state.das,
            key = { listItem: DirectActionUM -> listItem.id }
        ) { da ->
            Card(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                backgroundColor = ColorDefaults.backgroundSurfaceColor(),
                shape = ShortXCardRoundedCornerShape,
                elevation = 0.dp
            ) {
                DirectActionItem(
                    da = da,
                    onClick = {
                        onDirectActionClick(da)
                    },
                    onDelete = onDelete,
                    onEdit = onEdit,
                )
            }
        }
    }
}

@Composable
private fun DirectActionItem(
    da: DirectActionUM,
    onClick: () -> Unit,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
) {
    DAListItem(
        title = da.title,
        text1 = da.description,
        overflow = {
            ListItemOverflowMenu(da, onDelete, onEdit)
        },
        onClick = onClick,
    )
}


@Composable
private fun DAListItem(
    title: String? = null,
    text1: String? = null,
    text2: String? = null,
    overflow: @Composable ColumnScope.() -> Unit = {},
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.Commit, contentDescription = "Icon")
            Column(horizontalAlignment = Alignment.End) {
                overflow()
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            title?.let {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600
                    )
                )
            }
            TinySpacer()
            text1?.let {
                Text(
                    text = text1,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    maxLines = 3
                )
            }
            TinySpacer()
            TinySpacer()
            TinySpacer()
            text2?.let {
                Text(
                    text = text2,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    maxLines = 3
                )
            }
        }
    }
}

@Composable
private fun ListItemOverflowMenu(
    da: DirectActionUM,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit
) {
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
            widthFraction = 0.33f,
            state = menuState,
            items = listOf(
                DropdownItem(
                    labelLines = listOf(LocalI18N.current["ui.common.delete"]),
                    data = "delete",
                    icon = {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                    }),
                DropdownItem(
                    labelLines = listOf(LocalI18N.current["ui.common.edit"]),
                    data = "edit",
                    icon = {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Edit")
                    })
            ), onSelect = {
                when (it.data) {
                    "delete" -> {
                        onDelete(da)
                    }
                    "edit" -> {
                        onEdit(da)
                    }
                    else -> {}
                }
            })
    }
}
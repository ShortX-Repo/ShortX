@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.ui.base.*

const val EXECUTE_ANIM_DURATION = 600

@Composable
fun DirectActionListScreen(
    state: MainState,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
    onDirectActionClick: (DirectActionUM) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp)
    ) {
        items(items = state.das, key = { it.id }) { da ->
            var colorAnimStarted by remember {
                mutableStateOf(false)
            }
            var scaleAnimStarted by remember {
                mutableStateOf(false)
            }
            val widthPercent by animateFloatAsState(
                targetValue = if (!colorAnimStarted) 0f else 1f,
                animationSpec = tween(EXECUTE_ANIM_DURATION),
                finishedListener = {
                    colorAnimStarted = false
                }
            )
            val scalePercent by animateFloatAsState(
                targetValue = if (scaleAnimStarted) 0.96f else 1f,
                animationSpec = spring(),
                finishedListener = {
                    scaleAnimStarted = false
                }
            )
            var itemSize by remember {
                mutableStateOf(Pair(0.dp, 0.dp))
            }
            val density = LocalDensity.current

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .scale(scalePercent),
                backgroundColor = ColorDefaults.backgroundSurfaceColor(),
                shape = ShortXCardRoundedCornerShape,
                elevation = 0.dp
            ) {
                Box {
                    DirectActionItem(
                        modifier = Modifier.onSizeChanged {
                            itemSize = with(density) {
                                it.width.toDp() to it.height.toDp()
                            }
                        },
                        da = da,
                        onClick = {
                            onDirectActionClick(da)
                            colorAnimStarted = true
                            scaleAnimStarted = true
                        },
                        onDelete = onDelete,
                        onEdit = onEdit,
                    )

                    if (colorAnimStarted) Spacer(
                        modifier = Modifier
                            .width((itemSize.first * widthPercent))
                            .height(itemSize.second)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f))
                    )
                }
            }
        }
    }
}

@Composable
private fun DirectActionItem(
    modifier: Modifier,
    da: DirectActionUM,
    onClick: () -> Unit,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
) {
    DAListItem(
        modifier = modifier,
        title = da.title,
        text1 = da.description,
        icon = da.icon,
        overflow = {
            ListItemOverflowMenu(da, onDelete, onEdit)
        },
        onClick = onClick,
    )
}


@Composable
private fun DAListItem(
    modifier: Modifier,
    title: String? = null,
    text1: String? = null,
    text2: String? = null,
    icon: ImageVector,
    overflow: @Composable ColumnScope.() -> Unit = {},
    onClick: () -> Unit,
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .clip(ShortXCardRoundedCornerShape)
        .clickable {
            onClick()
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColoredIcon(size = 32.dp, padding = 0.dp) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = icon,
                    contentDescription = "Icon"
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                overflow()
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            title?.let {
                Text(
                    text = title, style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp, fontWeight = FontWeight.W600
                    )
                )
            }
            TinySpacer()
            text1?.let {
                Text(
                    text = text1,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    maxLines = 8
                )
            }
            TinySpacer()
            TinySpacer()
            TinySpacer()
            text2?.let {
                Text(
                    text = text2,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    maxLines = 8
                )
            }
        }
    }
}

@Composable
private fun ListItemOverflowMenu(
    da: DirectActionUM, onDelete: (DirectActionUM) -> Unit, onEdit: (DirectActionUM) -> Unit
) {
    val deleteDialogState = rememberDeleteConfirmDialogState()
    DeleteConfirmDialog(
        state = deleteDialogState,
        data = da,
        messageHint = { da.title },
        onDelete = onDelete
    )

    Box {
        val menuState = rememberDropdownSelectorState()
        IconButton(modifier = Modifier, onClick = {
            menuState.open()
        }) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More")
        }

        DropdownSelector(widthFraction = 0.33f, state = menuState, items = listOf(
            DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.edit"]),
                data = "edit",
                icon = {
                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
                }),
            DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.delete"]),
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
                    onEdit(da)
                }
                else -> {}
            }
        })
    }
}
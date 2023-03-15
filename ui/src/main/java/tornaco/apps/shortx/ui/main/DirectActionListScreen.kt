@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.outlined.StopCircle
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
    stopAllJobs: (DirectActionUM) -> Unit
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
                        stopAllJobs = stopAllJobs
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
    stopAllJobs: (DirectActionUM) -> Unit
) {
    DAListItem(
        modifier = modifier,
        title = da.title,
        text1 = da.description,
        icon = da.icon,
        runningInsCount = da.runningInsCount,
        overflow = {
            ListItemOverflowMenu(da, onDelete, onEdit)
        },
        onClick = onClick,
        stopAllJobs = {
            stopAllJobs(da)
        }
    )
}


@Composable
private fun DAListItem(
    modifier: Modifier,
    title: String? = null,
    text1: String? = null,
    text2: String? = null,
    runningInsCount: Int = 0,
    icon: ImageVector,
    overflow: @Composable ColumnScope.() -> Unit = {},
    onClick: () -> Unit,
    stopAllJobs: () -> Unit
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

        AnimatedVisibility(visible = runningInsCount > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        strokeWidth = 1.dp
                    )
                    SmallSpacer()
                    Text(
                        text = LocalI18N.current["ui.title.jobs.count", mapOf("count" to "$runningInsCount")],
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        maxLines = 1
                    )
                }

                IconButton(onClick = {
                    stopAllJobs()
                }) {
                    Icon(
                        imageVector = Icons.Outlined.StopCircle,
                        contentDescription = "Stop",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
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
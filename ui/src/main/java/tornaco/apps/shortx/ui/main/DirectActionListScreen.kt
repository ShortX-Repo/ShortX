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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import github.tornaco.shortx.android.icon.Remix
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.base.LoadState.Loading.data
import tornaco.apps.shortx.ui.shortcut.ActionShortcutActivity
import tornaco.apps.shortx.ui.util.ClipboardUtils

const val EXECUTE_ANIM_DURATION = 600

@Composable
fun DirectActionListScreen() {
    val vm = hiltViewModel<MainViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadDA()
    }

    val appNavController = LocalNavHostController.current
    val context = LocalContext.current

    MainScaffold(
        state = state,
        appBarTitleKey = "ui.feat.label.da",
        goToSettings = {
            appNavController.navigate(AppRoute.Settings.name)
        },
        onClipboardClick = {
            vm.parseShareContentDA(ClipboardUtils.readClipboard(context))
                ?.let {
                    vm.importDA(it)
                }
        },
        onAddClick = {
            appNavController.navigate(AppRoute.AddDA.name)
        },
        acceptPrivacyStatement = {
            vm.acceptPrivacyStatement()
        }
    ) {
        val shareDialogState = rememberShareDialogState("share.shortx")
        ShareDialog(state = shareDialogState)

        DirectActionListContent(
            state = state,
            onDelete = { da ->
                vm.deleteDA(da.id)
            },
            onEdit = {
                appNavController.navigate("${AppRoute.AddDA.name}?daId=${it.id}")
            },
            onShare = { um ->
                vm.daToShareString(um)?.let {
                    shareDialogState.show(it)
                }
            },
            onDirectActionClick = { da ->
                vm.executeDA(da)
            },
            stopAllJobs = {
                vm.stopJobs(it)
            }
        )
    }
}

@Composable
private fun DirectActionListContent(
    state: MainState,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
    onShare: (DirectActionUM) -> Unit,
    onDirectActionClick: (DirectActionUM) -> Unit,
    stopAllJobs: (DirectActionUM) -> Unit
) {
    if (state.das.isSuccess() && state.das.data.isNullOrEmpty()) {
        EmptyList(modifier = Modifier.fillMaxSize())
    } else if (state.das.isSuccess()) {
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp)
        ) {
            items(items = state.das.data.orEmpty(), key = { it.id }) { da ->
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
                            onShare = onShare,
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
}

@Composable
private fun DirectActionItem(
    modifier: Modifier,
    da: DirectActionUM,
    onClick: () -> Unit,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
    onShare: (DirectActionUM) -> Unit,
    stopAllJobs: (DirectActionUM) -> Unit
) {
    DAListItem(
        modifier = modifier,
        title = da.title,
        text1 = da.description,
        icon = da.icon,
        runningInsCount = da.runningInsCount,
        overflow = {
            ListItemOverflowMenu(da, onDelete, onEdit, onShare)
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
    icon: String,
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
                RemixIcon(
                    modifier = Modifier.size(16.dp),
                    remixName = icon
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
                        fontSize = 16.sp
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
                    RemixIcon(
                        remixName = Remix.Media.stop_circle_line,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun ListItemOverflowMenu(
    da: DirectActionUM,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
    onShare: (DirectActionUM) -> Unit,
) {
    val context = LocalContext.current

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
            DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.create.shortcut"]),
                data = "shortcut",
                icon = {
                    RemixIcon(Remix.System.external_link_line)
                }),
            DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.edit"]),
                data = "edit",
                icon = {
                    RemixIcon(Remix.Design.edit_line)
                }),
            DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.share"]),
                data = "share",
                icon = {
                    RemixIcon(Remix.System.share_line)
                }),
            DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.delete"]),
                data = "delete",
                icon = {
                    RemixIcon(Remix.System.delete_bin_4_line)
                })
        ), onSelect = {
            when (it.data) {
                "shortcut" -> {
                    da.icon
                    ActionShortcutActivity.addShortcut(context, da)
                }
                "delete" -> {
                    deleteDialogState.show()
                }
                "edit" -> {
                    onEdit(da)
                }
                "share" -> {
                    onShare(da)
                }
                else -> {}
            }
        })
    }
}
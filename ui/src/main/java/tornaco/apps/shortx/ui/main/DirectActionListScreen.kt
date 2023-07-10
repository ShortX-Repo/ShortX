@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.ui.addda.AddDirectActionActivity
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.DeleteConfirmDialog
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.EmptyList
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalSnackbarHostState
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.adaptiveCardSizeMin
import tornaco.apps.shortx.ui.base.adaptiveLauncherIconSizeMin
import tornaco.apps.shortx.ui.base.rememberDeleteConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.shortcut.ActionShortcutActivity
import tornaco.apps.shortx.ui.theme.NoteTextColor
import tornaco.apps.shortx.ui.theme.VipColorGold
import tornaco.apps.shortx.ui.util.ClipboardUtils

const val EXECUTE_ANIM_DURATION = 600

@Composable
fun DirectActionListScreen() {
    val vm = hiltViewModel<MainViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadDA(delay = true)
        vm.refreshStates()
    }

    val context = LocalContext.current

    MainScaffold(
        state = state,
        appBarTitleKey = "ui.feat.label.da",
        appBarActions = {
            IconButton(onClick = {
                vm.toggleShowDetailedDAItem()
            }) {
                if (state.isShowDetailedDAItem) {
                    RemixIcon(remixName = Remix.Document.bill_line)
                } else {
                    RemixIcon(remixName = Remix.Document.article_line)
                }
            }
        },
        onClipboardClick = {
            ClipboardUtils.readClipboard(context)?.let { clip ->
                vm.parseShareContentDA(clip)
                    ?.let {
                        vm.importDA(it)
                    }
            }
        },
        onAddClick = {
            AddDirectActionActivity.start(context)
        },
        acceptPrivacyStatement = {
            vm.acceptPrivacyStatement()
        }
    ) {
        val shareDialogState = rememberShareDialogState("share.shortx")
        ShareDialog(state = shareDialogState)

        DirectActionListContent(
            state = state,
            showOverflowMenu = true,
            onDelete = { da ->
                vm.deleteDA(da.id)
            },
            onDup = {
                vm.dupDA(it.id)
            },
            onEdit = {
                AddDirectActionActivity.start(context, it.id)
            },
            onShare = { um ->
                vm.daToShareString(um)?.let {
                    shareDialogState.show(um.title, it)
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
fun DirectActionListContent(
    state: MainState,
    showOverflowMenu: Boolean = false,
    onDelete: (DirectActionUM) -> Unit = {},
    onDup: (DirectActionUM) -> Unit = {},
    onEdit: (DirectActionUM) -> Unit = {},
    onShare: (DirectActionUM) -> Unit = {},
    onDirectActionClick: (DirectActionUM) -> Unit = {},
    stopAllJobs: (DirectActionUM) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp), contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }

    if (!state.isLoading && state.das.isEmpty()) {
        EmptyList(modifier = Modifier.fillMaxSize())
    } else {
        if (state.isShowDetailedDAItem) {
            DAListContent(
                state.das,
                showOverflowMenu,
                onDirectActionClick,
                onDup,
                onDelete,
                onEdit,
                onShare,
                stopAllJobs
            )
        } else {
            DAListContentCompat(
                state,
                showOverflowMenu,
                onDirectActionClick,
                onDup,
                onDelete,
                onEdit,
                onShare,
                stopAllJobs
            )
        }
    }
}

@Composable
private fun DAListContent(
    das: List<DirectActionUM>,
    showOverflowMenu: Boolean,
    onDirectActionClick: (DirectActionUM) -> Unit,
    onDup: (DirectActionUM) -> Unit = {},
    onDelete: (DirectActionUM) -> Unit = {},
    onEdit: (DirectActionUM) -> Unit = {},
    onShare: (DirectActionUM) -> Unit = {},
    stopAllJobs: (DirectActionUM) -> Unit = {}
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        columns = StaggeredGridCells.Adaptive(minSize = adaptiveCardSizeMin),
        contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp)
    ) {
        items(items = das, key = { it.id }) { da ->
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
                }, label = "widthPercent"
            )
            val scalePercent by animateFloatAsState(
                targetValue = if (scaleAnimStarted) 0.96f else 1f,
                animationSpec = spring(),
                finishedListener = {
                    scaleAnimStarted = false
                }, label = "scalePercent"
            )
            var itemSize by remember {
                mutableStateOf(Pair(0.dp, 0.dp))
            }
            val density = LocalDensity.current

            ShortXCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .scale(scalePercent)
                    .animateContentSize(),
            ) {
                Box {
                    DirectActionItem(
                        modifier = Modifier.onSizeChanged {
                            itemSize = with(density) {
                                it.width.toDp() to it.height.toDp()
                            }
                        },
                        da = da,
                        isLongClickEnabled = showOverflowMenu,
                        onClick = {
                            onDirectActionClick(da)
                            colorAnimStarted = true
                            scaleAnimStarted = true
                        },
                        onDup = onDup,
                        onDelete = onDelete,
                        onEdit = onEdit,
                        onShare = onShare,
                        stopAllJobs = stopAllJobs
                    )

                    if (colorAnimStarted) Spacer(
                        modifier = Modifier
                            .width((itemSize.first * widthPercent))
                            .height(itemSize.second)
                            .background(Color(da.iconColor).copy(alpha = ActionColorBgAlpha))
                    )
                }
            }
        }
    }
}


@Composable
private fun DAListContentCompat(
    state: MainState,
    showOverflowMenu: Boolean,
    onDirectActionClick: (DirectActionUM) -> Unit,
    onDup: (DirectActionUM) -> Unit,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
    onShare: (DirectActionUM) -> Unit,
    stopAllJobs: (DirectActionUM) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(adaptiveLauncherIconSizeMin),
        contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp, start = 16.dp, end = 16.dp)
    ) {
        items(items = state.das, key = { it.id }) { da ->
            DirectActionItemCompat(
                modifier = Modifier,
                da = da,
                isLongClickEnabled = showOverflowMenu,
                onClick = {
                    onDirectActionClick(da)
                },
                onDup = onDup,
                onDelete = onDelete,
                onEdit = onEdit,
                onShare = onShare,
                stopAllJobs = stopAllJobs
            )
        }
    }
}

@Composable
private fun DirectActionItem(
    modifier: Modifier,
    da: DirectActionUM,
    isLongClickEnabled: Boolean,
    onClick: () -> Unit,
    onDup: (DirectActionUM) -> Unit,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
    onShare: (DirectActionUM) -> Unit,
    stopAllJobs: (DirectActionUM) -> Unit
) {
    Box {
        val popMenuState = listItemOverflowMenu(
            da = da,
            onDup = onDup,
            onDelete = onDelete,
            onEdit = onEdit,
            onShare = onShare
        )
        DAListItem(
            modifier = modifier,
            title = da.title,
            description = da.description,
            updateTime = da.updateTime,
            icon = da.icon,
            iconColor = Color(da.iconColor),
            runningInsCount = da.runningInsCount,
            edit = { onEdit(da) },
            onClick = onClick,
            onLongClick = { if (isLongClickEnabled) popMenuState.open() },
            stopAllJobs = {
                stopAllJobs(da)
            }
        )
    }
}

@Composable
private fun DirectActionItemCompat(
    modifier: Modifier,
    da: DirectActionUM,
    isLongClickEnabled: Boolean,
    onClick: () -> Unit,
    onDup: (DirectActionUM) -> Unit,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
    onShare: (DirectActionUM) -> Unit,
    stopAllJobs: (DirectActionUM) -> Unit
) {
    Box {
        val popMenuState = listItemOverflowMenu(
            da = da,
            onDup = onDup,
            onDelete = onDelete,
            onEdit = onEdit,
            onShare = onShare
        )
        DAListItemCompat(
            modifier = modifier,
            title = da.title,
            icon = da.icon,
            iconColor = Color(da.iconColor),
            onClick = onClick,
            onLongClick = { if (isLongClickEnabled) popMenuState.open() },
        )
    }
}

@Composable
private fun DAListItemCompat(
    modifier: Modifier,
    title: String,
    icon: String,
    iconColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .combinedClickable(onLongClick = onLongClick) {
                onClick()
            }
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ColoredIcon(
            size = 48.dp,
            padding = 0.dp,
            color = iconColor
        ) {
            RemixIcon(
                modifier = Modifier.size(24.dp),
                remixName = icon,
                tint = Color.White
            )
        }

        TinySpacer()

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 12.sp
            ),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}


@Composable
private fun DAListItem(
    modifier: Modifier,
    title: String,
    description: String,
    updateTime: String,
    runningInsCount: Int = 0,
    icon: String,
    iconColor: Color,
    edit: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    stopAllJobs: () -> Unit
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .clip(ShortXCardRoundedCornerShape)
        .combinedClickable(onLongClick = onLongClick) {
            onClick()
        }
        .padding(horizontal = 12.dp)
        .padding(top = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColoredIcon(
                size = 42.dp,
                padding = 0.dp,
                color = iconColor.copy(alpha = ActionColorBgAlpha)
            ) {
                RemixIcon(
                    modifier = Modifier.size(16.dp),
                    remixName = icon,
                    tint = iconColor
                )
            }

            IconButton(onClick = {
                edit()
            }) {
                RemixIcon(remixName = Remix.Document.file_edit_line, tint = NoteTextColor)
            }
        }

        MediumSpacer()

        Column(modifier = Modifier) {
            Text(
                text = title, style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp
                )
            )
            SmallSpacer()
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                maxLines = 8
            )
            MediumSpacer()
            TinySpacer()
        }

        AnimatedVisibility(visible = runningInsCount > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
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
private fun listItemOverflowMenu(
    da: DirectActionUM,
    onDup: (DirectActionUM) -> Unit,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
    onShare: (DirectActionUM) -> Unit,
): DropdownSelectorState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = LocalSnackbarHostState.current
    val i18N = LocalI18N.current

    val deleteDialogState = rememberDeleteConfirmDialogState()
    DeleteConfirmDialog(
        state = deleteDialogState,
        data = da,
        messageHint = { da.title },
        onDelete = onDelete
    )

    val menuState = rememberDropdownSelectorState()

    DropdownSelector(widthFraction = 0.33f, state = menuState, items = listOf(
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.create.shortcut"]),
            data = "shortcut",
            icon = {
                RemixIcon(Remix.System.external_link_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.dup"]),
            data = "dup",
            icon = {
                RemixIcon(Remix.Document.file_copy_2_fill)
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
                ActionShortcutActivity.addShortcut(context, da)
                scope.launch {
                    val res = snackbar.showSnackbar(
                        i18N["ui.common.create.shortcut.snackbar.message"],
                        i18N["ui.common.settings"],
                        true,
                    )
                    if (res == SnackbarResult.ActionPerformed) {
                        kotlin.runCatching {
                            PkgUtils.launchPkgSettings(
                                context,
                                BuildProp.APP_PACKAGE_NAME
                            )
                        }
                    }
                }
            }

            "dup" -> {
                onDup(da)
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

    return menuState
}

@Composable
@Preview
private fun DAListItemPreview() {
    CompositionLocalProvider(LocalI18N provides object : I18N {
        override fun get(key: String, args: Map<String, String>): String {
            return "2 Jobs"
        }
    }) {
        DAListItem(
            modifier = Modifier,
            title = "Start QQ",
            description = "This is description",
            updateTime = "2022-12-22",
            runningInsCount = 2,
            icon = Remix.Media.broadcast_line,
            iconColor = VipColorGold,
            edit = {},
            onClick = {},
            onLongClick = {},
            stopAllJobs = {}
        )
    }
}
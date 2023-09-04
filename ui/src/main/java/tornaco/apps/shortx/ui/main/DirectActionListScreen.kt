@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.compat.ManifestCompat
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addda.AddDirectActionActivity
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.base.EmptyList
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.adaptiveLauncherIconSizeMin
import tornaco.apps.shortx.ui.base.dynamicStaggeredGridCells
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.main.model.MainState
import tornaco.apps.shortx.ui.share.SharedData
import tornaco.apps.shortx.ui.share.ViewerActivity
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.util.ClipboardUtils
import tornaco.apps.shortx.ui.util.Permissions.shouldCheckStoragePermissionBeforeWriteDownloads

const val EXECUTE_ANIM_DURATION = 600

@Composable
fun DirectActionListScreen() {
    val vm = hiltViewModel<MainViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadDA(delay = true)
    }

    val context = LocalContext.current

    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { fileUri ->
        if (fileUri != null) {
            vm.importDAFromUri(fileUri)
        }
    }

    val i18N = LocalI18N.current
    LaunchedEffect(vm) {
        vm.importComplete.collectLatest {
            when (it) {
                is ImportResult.Success -> {
                    Toast.makeText(context, i18N["ui.common.import.success"], Toast.LENGTH_SHORT)
                        .show()
                }

                is ImportResult.Fail -> {
                    Toast.makeText(
                        context,
                        i18N["ui.common.import.fail", mapOf("why" to it.reason.description)],
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

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
            vm.importDAFromClipboard()
        },
        onFileClick = {
            pickFileLauncher.launch("text/plain")
        },
        onAddClick = {
            AddDirectActionActivity.start(context)
        },
        onUrlClick = {
            ViewerActivity.start(context, SharedData("daUrl", it))
        },
        onNewSetClick = {
            vm.createNewDASet(it)
        },
        acceptPrivacyStatement = {
            vm.acceptPrivacyStatement()
        }
    ) {
        val permissionRequest = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {})

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
            onClick = {
                AddDirectActionActivity.start(context, it.id)
            },
            onDeepLink = {
                val uri = "shortx://run?id=${it.id}"
                ClipboardUtils.copyToClipboard(context, "uri", uri)
                Toast.makeText(
                    context,
                    i18N["ui.common.copied.to.clipboard"],
                    Toast.LENGTH_LONG
                ).show()
            },
            onShare = { um ->
                if (context.shouldCheckStoragePermissionBeforeWriteDownloads()) {
                    permissionRequest.launch(
                        arrayOf(
                            ManifestCompat.permission.READ_EXTERNAL_STORAGE,
                            ManifestCompat.permission.WRITE_EXTERNAL_STORAGE,
                        )
                    )
                } else {
                    vm.daToShareString(um)?.let {
                        shareDialogState.show(um.title, it)
                    }
                }


            },
            run = { da ->
                vm.executeDA(da)
            },
            stopAllJobs = {
                vm.stopJobs(it)
            },
            onMoveToSet = {da, setId->
                vm.addDAToDASet(da, setId)
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
    onClick: (DirectActionUM) -> Unit = {},
    onDeepLink: (DirectActionUM) -> Unit = {},
    onShare: (DirectActionUM) -> Unit = {},
    run: (DirectActionUM) -> Unit = {},
    onMoveToSet: (DirectActionUM, String) -> Unit = { _, _ -> },
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
        val subscribeState by LVLStateHolder.subscribeState.collectAsState()
        if (state.isShowDetailedDAItem) {
            DAListContent(
                das = state.das,
                isSubscribed = subscribeState.isSubscribed,
                showOverflowMenu = showOverflowMenu,
                run = run,
                onDup = onDup,
                onDelete = onDelete,
                onEdit = onClick,
                onDeepLink = onDeepLink,
                onShare = onShare,
                onMoveToSet = onMoveToSet,
                stopAllJobs = stopAllJobs
            )
        } else {
            DAListContentCompat(
                state = state,
                showOverflowMenu = showOverflowMenu,
                onDirectActionClick = run,
                onDup = onDup,
                onDelete = onDelete,
                onEdit = onClick,
                onDeepLink = onDeepLink,
                onShare = onShare,
                onMoveToSet = onMoveToSet,
                stopAllJobs = stopAllJobs
            )
        }

    }
}

@Composable
private fun DAListContent(
    das: List<DirectActionUM>,
    isSubscribed: Boolean,
    showOverflowMenu: Boolean,
    run: (DirectActionUM) -> Unit,
    onDup: (DirectActionUM) -> Unit = {},
    onDelete: (DirectActionUM) -> Unit = {},
    onEdit: (DirectActionUM) -> Unit = {},
    onDeepLink: (DirectActionUM) -> Unit = {},
    onShare: (DirectActionUM) -> Unit = {},
    onMoveToSet: (DirectActionUM, String) -> Unit = { _, _ -> },
    stopAllJobs: (DirectActionUM) -> Unit = {}
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        columns = dynamicStaggeredGridCells(),
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
                    if (da.isDirectActionSet) {
                        DirectActionSetItem(
                            modifier = Modifier.onSizeChanged {
                                itemSize = with(density) {
                                    it.width.toDp() to it.height.toDp()
                                }
                            },
                            da = da,
                            isSubscribed = isSubscribed,
                            onDelete = onDelete,
                            onEdit = onEdit,
                            onShare = onShare,
                        )
                    } else {
                        DirectActionItem(
                            modifier = Modifier.onSizeChanged {
                                itemSize = with(density) {
                                    it.width.toDp() to it.height.toDp()
                                }
                            },
                            da = da,
                            isSubscribed = isSubscribed,
                            isLongClickEnabled = showOverflowMenu,
                            run = {
                                run(da)
                                colorAnimStarted = true
                                scaleAnimStarted = true
                            },
                            onDup = onDup,
                            onDelete = onDelete,
                            onEdit = onEdit,
                            onDeepLink = onDeepLink,
                            onShare = onShare,
                            onMoveToSet = onMoveToSet,
                            stopAllJobs = stopAllJobs
                        )
                    }

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
    onDeepLink: (DirectActionUM) -> Unit,
    onShare: (DirectActionUM) -> Unit,
    onMoveToSet: (DirectActionUM, String) -> Unit,
    stopAllJobs: (DirectActionUM) -> Unit
) {
    val subscribeState by LVLStateHolder.subscribeState.collectAsState()
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(adaptiveLauncherIconSizeMin),
        contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp, start = 16.dp, end = 16.dp)
    ) {
        items(items = state.das, key = { it.id }) { da ->
            DirectActionItemCompat(
                modifier = Modifier,
                da = da,
                isSubscribed = subscribeState.isSubscribed,
                isLongClickEnabled = showOverflowMenu,
                onClick = {
                    onDirectActionClick(da)
                },
                onDup = onDup,
                onDelete = onDelete,
                onEdit = onEdit,
                onDeepLink = onDeepLink,
                onShare = onShare,
                onMoveToSet = onMoveToSet,
                stopAllJobs = stopAllJobs
            )
        }
    }
}

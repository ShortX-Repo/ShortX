@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)

package tornaco.apps.shortx.ui.main.da

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addda.AddDirectActionActivity
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.EmptyList
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.LoadState.Loading.data
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ProgressDialog
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXFilterChip
import tornaco.apps.shortx.ui.base.TextDisplayActivity
import tornaco.apps.shortx.ui.base.TipIds
import tornaco.apps.shortx.ui.base.UserTip
import tornaco.apps.shortx.ui.base.adaptiveLauncherIconSizeMin
import tornaco.apps.shortx.ui.base.dynamicStaggeredGridCells
import tornaco.apps.shortx.ui.base.rememberProgressDialogState
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.main.CommonSetFilter
import tornaco.apps.shortx.ui.main.MainScaffold
import tornaco.apps.shortx.ui.main.MainViewModel
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.main.userSetIdOrNull
import tornaco.apps.shortx.ui.share.SharedData
import tornaco.apps.shortx.ui.share.ViewerActivity
import tornaco.apps.shortx.ui.shortcut.ApkInstallState
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.util.ClipboardUtils
import java.io.File

@Composable
fun DirectActionListScreen() {
    val mainViewModel = hiltViewModel<MainViewModel>()
    val mainState by mainViewModel.state.collectAsState()

    val daListViewModel = hiltViewModel<DAListViewModel>()
    val daListState by daListViewModel.state.collectAsState()

    val context = LocalContext.current
    val i18n = LocalI18N.current

    val progressDialogState =
        rememberProgressDialogState(title = i18n["ui.common.install"], message = "...")
    ProgressDialog(state = progressDialogState)

    LaunchedEffect(daListViewModel) {
        daListViewModel.shortcutApkInstalled.collectLatest {
            composeLogger.d("shortcutApkInstalled: $it")
            when (it) {
                is ApkInstallState.InstallFailure -> {
                    progressDialogState.dismiss()
                    TextDisplayActivity.start(
                        context,
                        i18n["ui.common.install.failed"],
                        it.stackTrace
                    )
                }

                ApkInstallState.InstallSuccess -> {
                    progressDialogState.dismiss()
                    Toast.makeText(context, i18n["ui.common.install.success"], Toast.LENGTH_LONG)
                        .show()
                }

                ApkInstallState.Installing -> {
                    progressDialogState.show()
                }
            }
        }
    }

    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { fileUri ->
        if (fileUri != null) {
            ViewerActivity.start(context, fileUri)
        }
    }

    val searchBarState = rememberSearchBarState(
        keywordChanged = {
            daListViewModel.updateSearchQuery(it)
        }
    )

    MainScaffold(
        state = mainState,
        appBarTitle = {
            Text(
                text = LocalI18N.current["ui.feat.label.da"]
            )
        },
        enableSearch = true,
        searchBarState = searchBarState,
        appBarActions = {
            IconButton(onClick = {
                daListViewModel.toggleShowDetailedDAItem()
            }) {
                RemixIcon(remixName = Remix.Design.collage_line)
            }
        },
        onClipboardClick = {
            ClipboardUtils.readClipboard(context)?.let { clip ->
                val file = File(context.cacheDir, "clipboard.content")
                file.writeText(clip)
                ViewerActivity.start(context, file.toUri())
            }
        },
        onFileClick = {
            pickFileLauncher.launch("text/plain")
        },
        onAddClick = {
            AddDirectActionActivity.create(
                context = context,
                daSetId = daListState.selectedDASetFilter.userSetIdOrNull
            )
        },
        onUrlClick = {
            ViewerActivity.start(context, SharedData(SharedData.TYPE_DA_URL, it))
        },
        onNewSetClick = {
            daListViewModel.createNewDASet(it)
        },
        acceptPrivacyStatement = {
            mainViewModel.acceptPrivacyStatement()
        },
        appBarContent = {
            val daList by daListViewModel.daList.collectAsState()
            DASetFilterFlowRow(
                daSetFilters = if (daList.isSuccess()) {
                    daList.data?.daSetFilters ?: emptyList()
                } else {
                    emptyList()
                },
                selectedDASetFilter = daListState.selectedDASetFilter,
                selectDASetFilter = { daListViewModel.selectDASetFilter(it) },
            )
        }
    ) {
        val bottomSheetState = rememberShortXBottomSheetState()
        DADetailsBottomSheet(dialogState = bottomSheetState)

        val daHandler = getLocalDAItemHandler(vm = daListViewModel)
        val setHandler =
            getLocalDASetItemHandler(bottomSheetState = bottomSheetState, vm = daListViewModel)

        val lazyStaggeredGridState = rememberLazyStaggeredGridState()

        CompositionLocalProvider(
            LocalDAItemHandler provides daHandler,
            LocalDASetItemHandler provides setHandler
        ) {
            val activeJobIds by daListViewModel.activeJobIds.collectAsState()
            val daList by daListViewModel.daList.collectAsState()
            when (daList) {
                is LoadState.Error -> {
                    LoadError(modifier = Modifier.fillMaxSize()) {
                    }
                }

                LoadState.Loading -> {
                    Loading(modifier = Modifier.fillMaxSize())
                }

                is LoadState.Success -> {
                    DirectActionListContent(
                        lazyStaggeredGridState = lazyStaggeredGridState,
                        isShowDetailedDAItem = daListState.isShowDetailedDAItem,
                        colorfulCard = daListState.colorfulCard,
                        das = (daList as LoadState.Success<DAList>).data.das,
                        activeJobs = activeJobIds,
                        showOverflowMenu = true
                    )
                }
            }
        }
    }
}

@Composable
fun DASetFilterFlowRow(
    daSetFilters: List<CommonSetFilter> = emptyList(),
    selectedDASetFilter: CommonSetFilter = CommonSetFilter.PinnedAll,
    selectDASetFilter: (CommonSetFilter) -> Unit = {},
) {
    if (daSetFilters.any { !it.isStock }) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            daSetFilters.forEach {
                ShortXFilterChip(
                    modifier = Modifier.padding(start = 16.dp),
                    selected = selectedDASetFilter == it,
                    label = {
                        Text(text = it.title)
                    },
                    onClick = {
                        selectDASetFilter(it)
                    })
            }
        }
    }
}

@Composable
fun DirectActionListContent(
    lazyStaggeredGridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    isShowDetailedDAItem: Boolean,
    colorfulCard: Boolean,
    das: List<DirectActionUM>,
    activeJobs: ActiveJobIds,
    showOverflowMenu: Boolean = false,
) {

    Column {
        if (das.isEmpty()) {
            EmptyList(modifier = Modifier.fillMaxSize())
        } else {
            UserTip(
                modifier = Modifier.padding(horizontal = 16.dp),
                tipId = TipIds.commonRuleItem,
                message = LocalI18N.current["ui.common.rule.item.tip"]
            )
            val subscribeState by LVLStateHolder.collectAsState()
            if (isShowDetailedDAItem) {
                DAListContent(
                    lazyStaggeredGridState = lazyStaggeredGridState,
                    das = das,
                    activeJobs = activeJobs,
                    isSubscribed = subscribeState.isSubscribed,
                    colorfulCard = colorfulCard,
                    showOverflowMenu = showOverflowMenu,
                )
            } else {
                DAListContentCompat(
                    das = das,
                    isSubscribed = subscribeState.isSubscribed,
                    showOverflowMenu = showOverflowMenu,
                )
            }

        }
    }
}

@Composable
private fun DAListContent(
    lazyStaggeredGridState: LazyStaggeredGridState,
    das: List<DirectActionUM>,
    activeJobs: ActiveJobIds,
    isSubscribed: Boolean,
    colorfulCard: Boolean,
    showOverflowMenu: Boolean,
) {
    LazyVerticalStaggeredGrid(
        state = lazyStaggeredGridState,
        modifier = Modifier.fillMaxSize(),
        columns = dynamicStaggeredGridCells(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 72.dp)
    ) {
        items(items = das, key = { it.id }) { da ->
            ShortXCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                backgroundColor = if (!colorfulCard || da.isDirectActionSet) {
                    ColorDefaults.backgroundSurfaceColor()
                } else {
                    Color(da.iconColor).copy(alpha = ActionColorBgAlpha)
                },
            ) {
                Box {
                    if (da.isDirectActionSet) {
                        DirectActionSetItem(
                            modifier = Modifier,
                            da = da,
                            isSubscribed = isSubscribed
                        )
                    } else {
                        composeLogger.d("RUNNING-INS activeJobs: $activeJobs")
                        val runningInsCount = remember(activeJobs) {
                            activeJobs.ids.count { it == da.id }
                        }
                        composeLogger.d("RUNNING-INS runningInsCount: $activeJobs")
                        DirectActionItem(
                            modifier = Modifier,
                            da = da,
                            isSubscribed = isSubscribed,
                            isLongClickEnabled = showOverflowMenu,
                            colorfulCard = colorfulCard,
                            runningInsCount = runningInsCount
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun DAListContentCompat(
    das: List<DirectActionUM>,
    isSubscribed: Boolean,
    showOverflowMenu: Boolean
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(adaptiveLauncherIconSizeMin),
        contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp)
    ) {
        items(items = das, key = { it.id }) { da ->
            if (da.isDirectActionSet) {
                DASetListItemCompat(
                    modifier = Modifier,
                    da = da,
                    isSubscribed = isSubscribed
                )
            } else {
                DAListItemCompat(
                    modifier = Modifier,
                    da = da,
                    isSubscribed = isSubscribed,
                    isLongClickEnabled = showOverflowMenu,
                )
            }
        }
    }
}

@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.main.da

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addda.AddDirectActionActivity
import tornaco.apps.shortx.ui.base.EmptyList
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXFilterChip
import tornaco.apps.shortx.ui.base.adaptiveLauncherIconSizeMin
import tornaco.apps.shortx.ui.base.dynamicStaggeredGridCells
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.main.CommonSetFilter
import tornaco.apps.shortx.ui.main.MainScaffold
import tornaco.apps.shortx.ui.main.MainViewModel
import tornaco.apps.shortx.ui.main.id
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.share.SharedData
import tornaco.apps.shortx.ui.share.ViewerActivity
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.util.ClipboardUtils
import java.io.File

@Composable
fun DirectActionListScreen() {
    val mainViewModel = hiltViewModel<MainViewModel>()
    val mainState by mainViewModel.state.collectAsState()

    val daListViewModel = hiltViewModel<DAListViewModel>()
    val daListState by daListViewModel.state.collectAsState()

    LaunchedEffect(daListViewModel) {
        daListViewModel.loadDAList(delay = true)
    }

    val context = LocalContext.current

    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { fileUri ->
        if (fileUri != null) {
            ViewerActivity.start(context, fileUri)
        }
    }
    MainScaffold(
        state = mainState,
        appBarTitleKey = "ui.feat.label.da",
        appBarActions = {
            IconButton(onClick = {
                daListViewModel.toggleShowDetailedDAItem()
            }) {
                if (daListState.isShowDetailedDAItem) {
                    RemixIcon(remixName = Remix.Document.bill_line)
                } else {
                    RemixIcon(remixName = Remix.Document.article_line)
                }
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
                daSetId = daListState.selectedDASetFilter.id
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
            DirectActionListContent(
                lazyStaggeredGridState = lazyStaggeredGridState,
                isLoading = daListState.isLoading,
                isShowDetailedDAItem = daListState.isShowDetailedDAItem,
                das = daListState.das,
                daSetFilters = daListState.daSetFilters,
                selectedDASetFilter = daListState.selectedDASetFilter,
                selectDASetFilter = { daListViewModel.selectDASetFilter(it) },
                showOverflowMenu = true
            )
        }
    }
}

@Composable
fun DirectActionListContent(
    lazyStaggeredGridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    isLoading: Boolean,
    isShowDetailedDAItem: Boolean,
    das: List<DirectActionUM>,
    showOverflowMenu: Boolean = false,
    daSetFilters: List<CommonSetFilter> = emptyList(),
    selectedDASetFilter: CommonSetFilter = CommonSetFilter.PinnedAll,
    selectDASetFilter: (CommonSetFilter) -> Unit = {},
) {
    Column {
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

        if (!isLoading && das.isEmpty()) {
            EmptyList(modifier = Modifier.fillMaxSize())
        } else {
            val subscribeState by LVLStateHolder.collectAsState()
            if (isShowDetailedDAItem) {
                DAListContent(
                    lazyStaggeredGridState = lazyStaggeredGridState,
                    das = das,
                    isSubscribed = subscribeState.isSubscribed,
                    showOverflowMenu = showOverflowMenu,
                )
            } else {
                DAListContentCompat(
                    das = das,
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
    isSubscribed: Boolean,
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .animateContentSize(),
            ) {
                Box {
                    if (da.isDirectActionSet) {
                        DirectActionSetItem(
                            modifier = Modifier,
                            da = da,
                            isSubscribed = isSubscribed,
                        )
                    } else {
                        DirectActionItem(
                            modifier = Modifier,
                            da = da,
                            isSubscribed = isSubscribed,
                            isLongClickEnabled = showOverflowMenu
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
    showOverflowMenu: Boolean
) {
    val subscribeState by LVLStateHolder.collectAsState()
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(adaptiveLauncherIconSizeMin),
        contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp, start = 16.dp, end = 16.dp)
    ) {
        items(items = das, key = { it.id }) { da ->
            DirectActionItemCompat(
                modifier = Modifier,
                da = da,
                isSubscribed = subscribeState.isSubscribed,
                isLongClickEnabled = showOverflowMenu,
            )
        }
    }
}

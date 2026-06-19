@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalSnackbarHostState
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.main.CommonSetFilter
import tornaco.apps.shortx.ui.main.da.ActiveJobIds
import tornaco.apps.shortx.ui.main.da.DAItemHandler
import tornaco.apps.shortx.ui.main.da.DAList
import tornaco.apps.shortx.ui.main.da.DAListViewModel
import tornaco.apps.shortx.ui.main.da.DASetFilterFlowRow
import tornaco.apps.shortx.ui.main.da.DASetItemHandler
import tornaco.apps.shortx.ui.main.da.DirectActionListContent
import tornaco.apps.shortx.ui.main.da.LocalDAItemHandler
import tornaco.apps.shortx.ui.main.da.LocalDASetItemHandler
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

class DADialogState(
    val onSelected: (DirectActionUM) -> Unit
) : CommonDialogState()

@Composable
fun rememberDADialogState(
    onSelected: (DirectActionUM) -> Unit
): DADialogState {
    return remember {
        DADialogState(onSelected)
    }
}

@Composable
fun DASelectorDialog(state: DADialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            DASelector(state)
        }
    }
}

@Composable
private fun DASelector(dialogState: DADialogState) {
    val daListViewModel = hiltViewModel<DAListViewModel>().apply { resetState() }
    val daListState by daListViewModel.state.collectAsState()

    LaunchedEffect(daListViewModel) {
        daListViewModel.selectDASetFilter(CommonSetFilter.PinnedAllFlat)
    }
    val searchBarState = rememberSearchBarState(
        keywordChanged = {
            daListViewModel.updateSearchQuery(it)
        }
    )

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current[I18N_R.UI_TITLE_SELECT_ACTION]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                        },
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    val daItemHandler = object : DAItemHandler {
                        override fun click(da: DirectActionUM) {
                            dialogState.onSelected(da)
                            dialogState.dismiss()
                        }
                    }
                    val setItemHandler = object : DASetItemHandler {}
                    CompositionLocalProvider(
                        LocalSnackbarHostState provides SnackbarHostState(),
                        LocalDAItemHandler provides daItemHandler,
                        LocalDASetItemHandler provides setItemHandler
                    ) {
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
                                DASetFilterFlowRow(
                                    daSetFilters = (daList as LoadState.Success<DAList>).data.daSetFilters,
                                    selectedDASetFilter = (daList as LoadState.Success<DAList>).data.selectedDASetFilter,
                                    selectDASetFilter = { daListViewModel.selectDASetFilter(it) },
                                )
                                SearchBar(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    searchBarState = searchBarState,
                                    autoFocus = true,
                                )
                                DirectActionListContent(
                                    isShowDetailedDAItem = true,
                                    das = (daList as LoadState.Success<DAList>).data.das,
                                    showOverflowMenu = false,
                                    activeJobs = ActiveJobIds()
                                )
                            }
                        }
                    }
                }

            }
        }
    }

}

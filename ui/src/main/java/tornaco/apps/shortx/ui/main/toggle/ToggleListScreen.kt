package tornaco.apps.shortx.ui.main.toggle

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.ui.addtoggle.AddToggleActivity
import tornaco.apps.shortx.ui.base.EmptyList
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.TipIds
import tornaco.apps.shortx.ui.base.UserTip
import tornaco.apps.shortx.ui.base.dynamicStaggeredGridCells
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.main.MainScaffoldFabOptions
import tornaco.apps.shortx.ui.main.MainScaffold
import tornaco.apps.shortx.ui.main.MainViewModel
import tornaco.apps.shortx.ui.main.model.ToggleUM
import tornaco.apps.shortx.ui.main.uniqueUIKey
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder


@Composable
fun ToggleListScreen() {
    val mainViewModel = hiltViewModel<MainViewModel>()
    val mainState by mainViewModel.state.collectAsState()

    val toggleListViewModel = hiltViewModel<ToggleListViewModel>()
    val toggleListState by toggleListViewModel.state.collectAsState()

    LaunchedEffect(toggleListViewModel) {
        toggleListViewModel.loadToggleList(delay = true)
    }

    val context = LocalContext.current
    val searchBarState = rememberSearchBarState(
        keywordChanged = toggleListViewModel::updateSearchQuery
    )

    MainScaffold(
        state = mainState,
        appBarTitle = {
            Text(
                text = LocalI18N.current["ui.feat.label.toggles"]
            )
        },
        appBarActions = {
        },
        enableSearch = true,
        searchBarState = searchBarState,
        onClipboardClick = {

        },
        onFileClick = {
        },
        onAddClick = {
            AddToggleActivity.create(context)
        },
        onUrlClick = {
        },
        onNewSetClick = {
        },
        acceptPrivacyStatement = {
            mainViewModel.acceptPrivacyStatement()
        },
        fabOptions = listOf(MainScaffoldFabOptions.New)
    ) {
        val bottomSheetState = rememberShortXBottomSheetState()

        val setHandler =
            getLocalToggleSetItemHandler(
                bottomSheetState = bottomSheetState,
                vm = toggleListViewModel
            )
        val itemHandler = getLocalToggleItemHandler(vm = toggleListViewModel)
        CompositionLocalProvider(
            LocalToggleItemHandler provides itemHandler,
            LocalToggleSetItemHandler provides setHandler,
        ) {
            if (!toggleListState.isLoading && toggleListState.toggles.isEmpty()) {
                EmptyList(modifier = Modifier.fillMaxSize())
            } else {
                if (!toggleListState.isLoading) {
                    UserTip(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        tipId = TipIds.commonRuleItem,
                        message = LocalI18N.current["ui.common.rule.item.tip"]
                    )
                }

                val subscribeState by LVLStateHolder.collectAsState()
                LazyVerticalStaggeredGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = dynamicStaggeredGridCells(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 160.dp)
                ) {
                    items(
                        items = toggleListState.toggles,
                        key = { listItem: ToggleUM -> listItem.uniqueUIKey }) { toggle ->
                        ShortXCard(
                            modifier = Modifier
                                .animateItem()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            ToggleItem(toggle = toggle, isSubscribed = subscribeState.isSubscribed)
                        }
                    }
                }
            }
        }
    }

}

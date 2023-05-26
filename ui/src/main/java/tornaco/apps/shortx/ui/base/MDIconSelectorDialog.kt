@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.theme.ShortXTheme

class MDIconSelectorState(val onSelected: (String) -> Unit) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    fun show() {
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberMDIconSelectorState(onSelected: (String) -> Unit): MDIconSelectorState {
    return remember {
        MDIconSelectorState(onSelected)
    }
}


@Composable
fun MDIconSelectorDialog(state: MDIconSelectorState) {
    if (state.isShow) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            MDIconSelector(state)
        }
    }
}

@Composable
private fun MDIconSelector(state: MDIconSelectorState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            var selectedTabIndex by remember {
                mutableStateOf(0)
            }

            Scaffold(
                modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text("Icons") },
                        navigationIcon = {
                            IconButton(onClick = {
                                state.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                        }
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    val iconCategories: List<String> = Remix.all.keys.toList()
                    ShortXScrollableTabRow(selectedTabIndex = selectedTabIndex) {
                        iconCategories.forEachIndexed { index, categoryName ->
                            Tab(selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = categoryName,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                })
                        }
                    }

                    val searchBarState = rememberSearchBarState(iconCategories.size)
                    SearchBar(
                        searchBarState, modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    )

                    val icons =
                        remember(selectedTabIndex, searchBarState.keyword) {
                            Remix.all[iconCategories[selectedTabIndex]]!!.filterKeys {
                                searchBarState.keyword.isBlank() || it.contains(searchBarState.keyword)
                            }.map { it.key to it.value }
                        }

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize(),
                        columns = GridCells.Fixed(3),
                        content = {


                            itemsIndexed(icons) { _, nameAndResId ->
                                Column(
                                    modifier = Modifier
                                        .clickable {
                                            state.onSelected(nameAndResId.first)
                                            state.dismiss()
                                        }
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    RemixIcon(
                                        modifier = Modifier.size(32.dp),
                                        remixName = nameAndResId.first
                                    )
                                    TinySpacer()
                                    Text(
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                                        text = nameAndResId.first,
                                        overflow = TextOverflow.Visible,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        })
                }
            }
        }
    }
}
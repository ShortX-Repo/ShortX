@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)

package tornaco.apps.shortx.ui.pkgset

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PkgSetListScreen(back: () -> Unit, goToDetail: (String) -> Unit) {
    val vm = hiltViewModel<PkgSetListViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState()
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val i18N = LocalI18N.current
            val addPkgSetDialogState =
                rememberTextInputState(
                    title = i18N["ui.pkg.set.add.title"],
                    message = i18N["ui.pkg.set.add.input.label"],
                    inputValidator = {
                        vm.validateLabel(it, i18N)
                    },
                    onSelected = {
                        vm.createPkgSet(it)
                    })
            TextInputDialog(addPkgSetDialogState)

            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    ShortXMediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = i18N["ui.pkg.set.title"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {},
                        scrollBehavior = scrollBehavior
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        addPkgSetDialogState.show()
                    }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "add")
                    }
                }) { paddingValues ->
                if (state.isLoading) {
                    Loading(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                } else {
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = StaggeredGridCells.Fixed(2),
                        contentPadding = paddingValues
                    ) {
                        items(state.pkgSets) {
                            androidx.compose.material.Card(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                backgroundColor = ColorDefaults.backgroundSurfaceColor(),
                                shape = ShortXCardRoundedCornerShape,
                                elevation = 0.dp
                            ) {
                                Box {
                                    val popMenuState = listItemOverflowMenu(
                                        pkgSet = it,
                                        onDelete = {
                                            vm.delete(it.label)
                                        },
                                    )

                                    PkgSetListItem(modifier = Modifier,
                                        title = it.label,
                                        text1 = i18N["ui.pkg.set.items.count", mapOf("count" to "${it.appPkgsCount}")],
                                        onClick = {
                                            goToDetail(it.label)
                                        },
                                        onLongClick = {
                                            if (!it.isPrebuilt) {
                                                popMenuState.open()
                                            }
                                        })
                                }
                            }
                        }
                    }
                }


            }
        }
    }
}


@Composable
private fun PkgSetListItem(
    modifier: Modifier,
    title: String,
    text1: String,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .combinedClickable(onLongClick = onLongClick, onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColoredIcon(size = 32.dp, padding = 0.dp) {
                RemixIcon(
                    modifier = Modifier.size(16.dp),
                    remixName = Remix.Document.folder_4_line
                )
            }
            MediumSpacer()
            Text(
                text = title, style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                )
            )
        }

        MediumSpacer()

        Text(
            text = text1,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            maxLines = 3
        )
    }
}

@Composable
private fun listItemOverflowMenu(
    pkgSet: PkgSet, onDelete: (PkgSet) -> Unit
): DropdownSelectorState {
    val menuState = rememberDropdownSelectorState()

    DropdownSelector(widthFraction = 0.33f, state = menuState, items = listOf(
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.delete"]),
            data = "delete",
            icon = {
                RemixIcon(remixName = Remix.System.delete_bin_fill)
            }),
    ), onSelect = {
        when (it.data) {
            "delete" -> {
                onDelete(pkgSet)
            }

            else -> {}
        }
    })
    return menuState
}
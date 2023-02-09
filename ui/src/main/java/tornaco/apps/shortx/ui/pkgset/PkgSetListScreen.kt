@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package tornaco.apps.shortx.ui.pkgset

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
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
            val addPkgSetDialogState =
                rememberTextInputState(
                    title = LocalI18N.current["ui.pkg.set.add.title"],
                    message = LocalI18N.current["ui.pkg.set.add.input.label"],
                    onSelected = {
                        vm.createPkgSet(it)
                    })
            TextInputDialog(addPkgSetDialogState)

            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = LocalI18N.current["ui.pkg.set.title"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
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
                }) {
                LazyVerticalStaggeredGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = it
                ) {
                    items(state.pkgSets) {
                        androidx.compose.material.Card(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            backgroundColor = ColorDefaults.backgroundSurfaceColor(),
                            shape = ShortXCardRoundedCornerShape,
                            elevation = 0.dp
                        ) {
                            PkgSetListItem(modifier = Modifier,
                                title = it.label,
                                text1 = LocalI18N.current["ui.pkg.set.items.count", mapOf("count" to "${it.appPkgsCount}")],
                                overflow = {
                                    ListItemOverflowMenu(
                                        pkgSet = it,
                                        onDelete = {},
                                        onEdit = {}
                                    )
                                }, onClick = {
                                    goToDetail(it.id)
                                })
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
    title: String? = null,
    text1: String? = null,
    text2: String? = null,
    overflow: @Composable ColumnScope.() -> Unit = {},
    onClick: () -> Unit,
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
                    imageVector = Icons.Filled.Apps,
                    contentDescription = "Apps"
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
                    maxLines = 3
                )
            }
            TinySpacer()
            TinySpacer()
            TinySpacer()
            text2?.let {
                Text(
                    text = text2,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    maxLines = 3
                )
            }
        }
    }
}

@Composable
private fun ListItemOverflowMenu(
    pkgSet: PkgSet, onDelete: (PkgSet) -> Unit, onEdit: (PkgSet) -> Unit
) {
    Box {
        val menuState = rememberDropdownSelectorState()
        IconButton(modifier = Modifier, onClick = {
            menuState.open()
        }) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More")
        }

        DropdownSelector(widthFraction = 0.33f, state = menuState, items = listOf(
            DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.delete"]),
                data = "delete",
                icon = {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete")
                }),
            DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.edit"]),
                data = "edit",
                icon = {
                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
                })
        ), onSelect = {
            when (it.data) {
                "delete" -> {
                    onDelete(pkgSet)
                }
                "edit" -> {
                    onEdit(pkgSet)
                }
                else -> {}
            }
        })
    }
}
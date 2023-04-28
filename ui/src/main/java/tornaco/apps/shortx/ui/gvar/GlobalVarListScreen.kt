@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package tornaco.apps.shortx.ui.gvar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.label
import tornaco.apps.shortx.core.rule.toType
import tornaco.apps.shortx.core.rule.valueString
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GlobalVarListScreen(back: () -> Unit, goToDetail: (String) -> Unit) {
    val vm = hiltViewModel<GlobalVarListViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState()
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val addGlobalVarDialogState =
                rememberNewGlobalVarState {
                    vm.createGlobalVar(it.toGlobalVar())
                }
            NewGlobalVarDialog(addGlobalVarDialogState)

            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = LocalI18N.current["ui.global.var.title"]) },
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
                        addGlobalVarDialogState.show()
                    }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "add")
                    }
                }) { paddingValues ->
                LazyVerticalStaggeredGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = paddingValues
                ) {
                    items(state.globalVars) {
                        androidx.compose.material.Card(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            backgroundColor = ColorDefaults.backgroundSurfaceColor(),
                            shape = ShortXCardRoundedCornerShape,
                            elevation = 0.dp
                        ) {
                            GlobalVarListItem(modifier = Modifier,
                                name = it.name,
                                value = it.valueString(),
                                type = it.type.toType()?.label() ?: "?",
                                overflow = {
                                    ListItemOverflowMenu(
                                        pkgSet = it,
                                        onDelete = {
                                            vm.delete(it.name)
                                        },
                                        onEdit = {}
                                    )
                                },
                                onClick = {
                                    goToDetail(it.name)
                                })
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun GlobalVarListItem(
    modifier: Modifier,
    name: String? = null,
    value: String? = null,
    type: String,
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
            MD3Badge(text = type)

            Column(horizontalAlignment = Alignment.End) {
                overflow()
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            name?.let {
                Text(
                    text = name, style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                    )
                )
            }
            MediumSpacer()
            Text(value ?: "", fontSize = 12.sp, maxLines = 3)
            StandardSpacer()
        }
    }
}

@Composable
private fun ListItemOverflowMenu(
    pkgSet: GlobalVar, onDelete: (GlobalVar) -> Unit, onEdit: (GlobalVar) -> Unit
) {
    Box {
        val menuState = rememberDropdownSelectorState()
        IconButton(modifier = Modifier, onClick = {
            menuState.open()
        }) {
            RemixIcon(modifier = Modifier.size(16.dp), remixName = Remix.System.more_2_line)
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
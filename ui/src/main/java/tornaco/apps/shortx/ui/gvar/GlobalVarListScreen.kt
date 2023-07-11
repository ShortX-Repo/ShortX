@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)

package tornaco.apps.shortx.ui.gvar

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.label
import tornaco.apps.shortx.core.rule.toType
import tornaco.apps.shortx.core.rule.valueString
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.adaptiveCardSizeMin
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class GlobalVarListActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, GlobalVarListActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        GlobalVarListScreen(back = { finish() },
            goToDetail = {
                GlobalVarDetailActivity.start(this@GlobalVarListActivity, it)
            })
    }
}


@Composable
fun GlobalVarListScreenDialog(state: CommonDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            GlobalVarListScreen(back = { state.dismiss() }, goToDetail = {})
        }
    }
}

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
                    ShortXMediumTopAppBar(
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    val searchBarState = rememberSearchBarState(state.globalVars.size)
                    SearchBar(searchBarState = searchBarState, modifier = Modifier.padding(16.dp))
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = StaggeredGridCells.Adaptive(adaptiveCardSizeMin),
                    ) {
                        items(state.globalVars.filter {
                            searchBarState.keyword.isBlank() || it.name.contains(
                                searchBarState.keyword,
                                ignoreCase = true
                            )
                        }) {
                            androidx.compose.material.Card(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                backgroundColor = ColorDefaults.backgroundSurfaceColor(),
                                shape = ShortXCardRoundedCornerShape,
                                elevation = 0.dp
                            ) {
                                Box {
                                    val menuState = listItemOverflowMenu(
                                        globalVar = it,
                                        onDelete = {
                                            vm.delete(it.name)
                                        },
                                        onEdit = { goToDetail(it.name) }
                                    )

                                    GlobalVarListItem(modifier = Modifier,
                                        name = it.name,
                                        value = it.valueString(),
                                        type = it.type.toType()?.label() ?: "?",
                                        onClick = {
                                            goToDetail(it.name)
                                        },
                                        onLongClick = {
                                            menuState.open()
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
private fun GlobalVarListItem(
    modifier: Modifier,
    name: String,
    value: String? = null,
    type: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(12.dp)
    ) {
        OutlineBadge(text = type)
        Text(text = name, style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp))
        Text(value ?: "", fontSize = 12.sp, maxLines = 3)
    }
}

@Composable
private fun listItemOverflowMenu(
    globalVar: GlobalVar, onDelete: (GlobalVar) -> Unit, onEdit: (GlobalVar) -> Unit
): DropdownSelectorState {
    val menuState = rememberDropdownSelectorState()

    val globalVarAccessHelpDialogState =
        rememberGlobalVarAccessHelpDialogState(globalVar = globalVar)
    GlobalVarAccessHelpDialog(state = globalVarAccessHelpDialogState)

    DropdownSelector(widthFraction = 0.33f, state = menuState, items = listOf(
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.delete"]),
            data = "delete",
            icon = {
                RemixIcon(remixName = Remix.System.delete_bin_fill)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.edit"]),
            data = "edit",
            icon = {
                RemixIcon(remixName = Remix.Design.edit_fill)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.global.var.access.help.title"]),
            data = "access",
            icon = {
                RemixIcon(remixName = Remix.Development.braces_fill)
            })
    ), onSelect = {
        when (it.data) {
            "delete" -> {
                onDelete(globalVar)
            }

            "edit" -> {
                onEdit(globalVar)
            }

            "access" -> {
                globalVarAccessHelpDialogState.show()
            }

            else -> {}
        }
    })

    return menuState
}
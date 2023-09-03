@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)

package tornaco.apps.shortx.ui.gvar

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.toJson
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.label
import tornaco.apps.shortx.core.rule.toType
import tornaco.apps.shortx.core.rule.valueString
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.FabState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MultiFabItem
import tornaco.apps.shortx.ui.base.MultiFloatingActionButton
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.dynamicStaggeredGridCells
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberFabState
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.share.addShareContentHeader
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

            val pickFileLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.GetContent()
            ) { fileUri ->
                if (fileUri != null) {
                    vm.importFromUri(fileUri)
                }
            }

            val fabState = rememberFabState(
                onFabItemClicked = {
                    when (GVFabOptions.valueOf(it.id)) {
                        GVFabOptions.ImportClipboard -> {
                            vm.importFromClipboard()
                        }

                        GVFabOptions.ImportFile -> {
                            pickFileLauncher.launch("text/plain")
                        }

                        GVFabOptions.New -> {
                            addGlobalVarDialogState.show()
                        }
                    }
                })

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
                    AddOptionsFab(fabState = fabState)
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
                        contentPadding = PaddingValues(bottom = 64.dp),
                        columns = dynamicStaggeredGridCells(),
                    ) {
                        items(state.globalVars.filter {
                            searchBarState.keyword.isBlank() || it.name.contains(
                                searchBarState.keyword,
                                ignoreCase = true
                            )
                        }) { globalVar ->
                            val globalVarAccessHelpDialogState =
                                rememberGlobalVarAccessHelpDialogState(globalVar = globalVar)
                            GlobalVarAccessHelpDialog(state = globalVarAccessHelpDialogState)

                            androidx.compose.material.Card(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                backgroundColor = ColorDefaults.backgroundSurfaceColor(),
                                shape = ShortXCardRoundedCornerShape,
                                elevation = 0.dp
                            ) {
                                Box {
                                    val menuState = listItemOverflowMenu(
                                        globalVar = globalVar,
                                        onDelete = {
                                            vm.delete(it.name)
                                        },
                                        onEdit = { goToDetail(it.name) }
                                    )

                                    GlobalVarListItem(
                                        modifier = Modifier,
                                        name = globalVar.name,
                                        value = globalVar.valueString(),
                                        type = globalVar.type.toType()?.label() ?: "?",
                                        isSecret = globalVar.isSecret,
                                        onInfoClick = {
                                            globalVarAccessHelpDialogState.show()
                                        },
                                        onClick = {
                                            goToDetail(globalVar.name)
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
    isSecret: Boolean,
    onInfoClick: () -> Unit,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlineBadge(text = type)
            IconButton(onClick = {
                onInfoClick()
            }) {
                RemixIcon(remixName = Remix.System.information_line)
            }
        }

        Text(text = name, style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp))
        Text(
            if (!isSecret) {
                (value ?: "")
            } else {
                "****"
            }, fontSize = 12.sp, maxLines = 3
        )
        if (isSecret) {
            MediumSpacer()
            TipRow(text = LocalI18N.current["ui.global.var.secrets.title"],
                icon = {
                    ColoredIcon(size = 16.dp) {
                        RemixIcon(
                            modifier = Modifier.size(8.dp),
                            remixName = Remix.Document.file_search_line
                        )
                    }
                })
        }
    }
}

@Composable
private fun listItemOverflowMenu(
    globalVar: GlobalVar, onDelete: (GlobalVar) -> Unit, onEdit: (GlobalVar) -> Unit
): DropdownSelectorState {
    val shareDialogState = rememberShareDialogState("share.shortx.global.var")
    ShareDialog(state = shareDialogState)

    val menuState = rememberDropdownSelectorState()
    DropdownSelector(widthFraction = 0.33f, state = menuState, items = listOf(
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.delete"]),
            data = "delete",
            icon = {
                RemixIcon(remixName = Remix.System.delete_bin_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.edit"]),
            data = "edit",
            icon = {
                RemixIcon(remixName = Remix.Design.edit_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.share"]),
            data = "share",
            icon = {
                RemixIcon(Remix.System.share_line)
            })
    ), onSelect = {
        when (it.data) {
            "delete" -> {
                onDelete(globalVar)
            }

            "edit" -> {
                onEdit(globalVar)
            }

            "share" -> {
                val shareContent =
                    globalVar.toJson().addShareContentHeader(mapOf("type" to "GlobalVar"))
                shareDialogState.show("GlobalVar-${globalVar.name}", shareContent)
            }

            else -> {}
        }
    })

    return menuState
}


@Composable
private fun AddOptionsFab(
    fabState: FabState
) {
    val expandFbItemList: MutableList<MultiFabItem> = mutableListOf(
        MultiFabItem(
            id = GVFabOptions.ImportFile.name,
            icon = Remix.Document.file_line,
            label = LocalI18N.current["ui.common.import.from.file"]
        ),
        MultiFabItem(
            id = GVFabOptions.ImportClipboard.name,
            icon = Remix.Document.clipboard_line,
            label = LocalI18N.current["ui.common.import.from.clipboard"]
        ),
        MultiFabItem(
            id = GVFabOptions.New.name,
            icon = Remix.System.add_line,
            label = LocalI18N.current["ui.common.new"]
        ),
    )

    MultiFloatingActionButton(
        srcIcon = Icons.Outlined.Add,
        showLabels = true,
        items = expandFbItemList,
        state = fabState
    )
}


enum class GVFabOptions {
    ImportFile,
    ImportClipboard,
    New,
}
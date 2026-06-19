@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)

package tornaco.apps.shortx.ui.gvar

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.GlobalVarCreatedBy_User
import tornaco.apps.shortx.core.proto.toJson
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.label
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.toType
import tornaco.apps.shortx.core.rule.valueString
import tornaco.apps.shortx.ui.addda.AddDirectActionActivity
import tornaco.apps.shortx.ui.addrule.AddRuleActivity
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.FabState
import tornaco.apps.shortx.ui.base.LinkText
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MultiFabItem
import tornaco.apps.shortx.ui.base.MultiFloatingActionButton
import tornaco.apps.shortx.ui.base.NoteRow
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SearchBar
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.dynamicStaggeredGridCells
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberFabState
import tornaco.apps.shortx.ui.base.rememberSearchBarState
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.share.addShareContentHeader
import tornaco.apps.shortx.core.i18n.I18N_R

@AndroidEntryPoint
class GlobalVarListActivity : BaseActivity() {
    companion object {
        const val KEY_SHOW_GROUP_VIEW = "feat.gv.list.activity.group.view"

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, GlobalVarListActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        GlobalVarListScreen(
            back = { finish() },
            goToDetail = {
                GlobalVarDetailActivity.start(this@GlobalVarListActivity, it)
            })
    }
}

@Composable
fun GlobalVarListScreen(back: () -> Unit, goToDetail: (String) -> Unit) {
    val vm = hiltViewModel<GlobalVarListViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState()
    }

    val addGlobalVarDialogState =
        rememberNewGlobalVarState {
            vm.createGlobalVar(
                it.toGlobalVar(
                    GlobalVarCreatedBy_User.newBuilder().build().pack_()
                )
            )
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

    val context = LocalContext.current
    val pref = remember { Pref(context) }
    val miscToggles by pref.miscUiToggles.collectAsState(initial = emptySet())
    val isGroupView = miscToggles.contains(GlobalVarListActivity.KEY_SHOW_GROUP_VIEW)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ShortXMediumTopAppBar(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                title = { Text(text = LocalI18N.current[I18N_R.UI_GLOBAL_VAR_TITLE]) },
                navigationIcon = {
                    IconButton(onClick = {
                        back()
                    }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                },
                actions = {
                    val scope = rememberCoroutineScope()
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (isGroupView) {
                                    pref.miscUiTogglesOff(GlobalVarListActivity.KEY_SHOW_GROUP_VIEW)
                                } else {
                                    pref.miscUiTogglesOn(GlobalVarListActivity.KEY_SHOW_GROUP_VIEW)
                                }
                            }
                        }) {
                        RemixIcon(Remix.Edit.group)
                    }
                },
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
            AnimatedVisibility(!isGroupView) {
                SearchBar(searchBarState = searchBarState, modifier = Modifier.padding(16.dp))
            }

            if (isGroupView) {
                GlobalGroups(
                    groups = state.groups,
                    goToDetail = goToDetail,
                    delete = { vm.delete(it) }
                )
            } else {
                GlobalGrid(
                    globalVars = state.globalVars.filter {
                        searchBarState.keyword.isBlank() || it.globalVar.name.contains(
                            searchBarState.keyword,
                            ignoreCase = true
                        )
                    },
                    goToDetail = goToDetail,
                    delete = { vm.delete(it) }
                )
            }
        }
    }
}

@Composable
private fun GlobalGroups(
    groups: List<GlobalVarGroup>,
    goToDetail: (String) -> Unit,
    delete: (String) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 64.dp),
        columns = dynamicStaggeredGridCells(),
    ) {
        items(groups) { group ->
            val dialogState = rememberConfirmDialogState()
            GlobalGroupDetailDialog(dialogState, group, goToDetail, delete)
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = cardColors(containerColor = ColorDefaults.backgroundSurfaceColor()),
                shape = ShortXCardRoundedCornerShape,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            dialogState.show()
                        }
                        .padding(16.dp)) {
                    Text(
                        text = group.createdBy.getLabel(LocalI18N.current),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp)
                    )
                    SmallSpacer()
                    Text(
                        text = "${group.vars.size} items",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun GlobalGroupDetailDialog(
    state: CommonDialogState,
    group: GlobalVarGroup,
    goToDetail: (String) -> Unit,
    delete: (String) -> Unit,
) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXAppBarScaffold(
                title = group.createdBy.getLabel(LocalI18N.current),
                onBackPressed = {
                    state.dismiss()
                },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                verticalScroll = false
            ) {
                GlobalGrid(
                    group.vars,
                    goToDetail,
                    delete
                )
            }
        }
    }
}


@Composable
private fun GlobalGrid(
    globalVars: List<GlobalVarUiItem>,
    goToDetail: (String) -> Unit,
    delete: (String) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 64.dp),
        columns = dynamicStaggeredGridCells(),
    ) {
        items(globalVars) { globalVar ->
            val globalVarAccessHelpDialogState =
                rememberGlobalVarAccessHelpDialogState(globalVar = globalVar.globalVar)
            GlobalVarAccessHelpDialog(state = globalVarAccessHelpDialogState)

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = cardColors(containerColor = ColorDefaults.backgroundSurfaceColor()),
                shape = ShortXCardRoundedCornerShape,
            ) {
                Box {
                    val menuState = listItemOverflowMenu(
                        globalVar = globalVar.globalVar,
                        onDelete = {
                            delete(it.name)
                        },
                        onEdit = { goToDetail(it.name) }
                    )

                    GlobalVarListItem(
                        modifier = Modifier,
                        name = globalVar.globalVar.name,
                        value = globalVar.globalVar.valueString(),
                        note = globalVar.globalVar.note.takeIf { it.isNotEmpty() },
                        type = globalVar.globalVar.type.toType()?.label() ?: "?",
                        createdBy = globalVar.createdBy,
                        isSecret = globalVar.globalVar.isSecret,
                        onInfoClick = {
                            globalVarAccessHelpDialogState.show()
                        },
                        onClick = {
                            goToDetail(globalVar.globalVar.name)
                        },
                        onLongClick = {
                            menuState.open()
                        })
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
    note: String? = null,
    type: String,
    createdBy: CreatedBy,
    isSecret: Boolean,
    onInfoClick: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val i18N = LocalI18N.current
    val context = LocalContext.current

    val createdByLabel = remember(createdBy) {
        createdBy.getLabel(i18N)
    }
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

        when (createdBy) {
            is CreatedBy.DA -> {
                LinkText(text = createdByLabel) {
                    AddDirectActionActivity.edit(context, createdBy.da.id)
                }
            }

            is CreatedBy.Rule -> {
                LinkText(text = createdByLabel) {
                    AddRuleActivity.edit(context, createdBy.rule.id)
                }
            }

            CreatedBy.Unknown -> {

            }

            CreatedBy.User -> {
                TipBody(text = createdByLabel)
            }
        }

        note?.let { NoteRow(text = it) }

        if (isSecret) {
            MediumSpacer()
            TipRow(
                text = LocalI18N.current[I18N_R.UI_GLOBAL_VAR_SECRETS_TITLE],
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
    DropdownSelector(
        widthFraction = 0.33f, state = menuState, items = listOf(
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_DELETE]),
                data = "delete",
                icon = {
                    RemixIcon(remixName = Remix.System.delete_bin_line)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_EDIT]),
                data = "edit",
                icon = {
                    RemixIcon(remixName = Remix.Design.edit_line)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_SHARE]),
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
            label = LocalI18N.current[I18N_R.UI_COMMON_IMPORT_FROM_FILE]
        ),
        MultiFabItem(
            id = GVFabOptions.ImportClipboard.name,
            icon = Remix.Document.clipboard_line,
            label = LocalI18N.current[I18N_R.UI_COMMON_IMPORT_FROM_CLIPBOARD]
        ),
        MultiFabItem(
            id = GVFabOptions.New.name,
            icon = Remix.System.add_line,
            label = LocalI18N.current[I18N_R.UI_COMMON_NEW]
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
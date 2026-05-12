@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)

package tornaco.apps.shortx.ui.pkgset

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.NoteRow
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.dynamicStaggeredGridCells
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class PkgSetListActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, PkgSetListActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        PkgSetListScreen(back = { finish() }, goToDetail = {
            PkgSetDetailActivity.start(this, it)
        })
    }
}

@Composable
fun PkgSetListScreenDialog(state: CommonDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            PkgSetListScreen(back = { state.dismiss() }, goToDetail = {})
        }
    }
}

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
                rememberNewPkgSetState(
                    inputValidator = {
                        vm.validateLabel(it, i18N)
                    },
                    onSelected = {
                        vm.createPkgSet(it.label, it.description)
                    })
            NewPkgSetDialog(addPkgSetDialogState)

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
                Box(modifier = Modifier.padding(paddingValues)) {
                    if (state.isLoading) {
                        Loading(
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyVerticalStaggeredGrid(
                            modifier = Modifier.fillMaxSize(),
                            columns = dynamicStaggeredGridCells(),
                            contentPadding = PaddingValues(bottom = 128.dp)
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
                                            title = LocalI18N.current["ui.common.app.${it.label.lowercase()}", emptyMap(), it.label],
                                            text1 = i18N["ui.pkg.set.items.count", mapOf("count" to "${it.appPkgsCount}")],
                                            note = it.description.takeIf { desc -> desc.isNotBlank() },
                                            badge = if (it.isPrebuilt) {
                                                i18N["ui.pkg.set.prebuilt"]
                                            } else null,
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
}


@Composable
private fun PkgSetListItem(
    modifier: Modifier,
    title: String,
    text1: String,
    note: String?,
    badge: String?,
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

        note?.let {
            MediumSpacer()
            NoteRow(text = it)
        }

        if (badge != null) {
            MediumSpacer()
            OutlineBadge(text = badge)
        }
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
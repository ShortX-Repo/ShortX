@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.gesture

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.Card
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.adaptiveCardSizeMin
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class GestureRecordListActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, GestureRecordListActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        GestureRecordListScreen(
            back = {
                finish()
            }
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GestureRecordListScreen(back: () -> Unit) {
    val vm = hiltViewModel<GestureRecordListViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState()
    }

    val context = LocalContext.current
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    ShortXMediumTopAppBar(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        title = { Text(text = LocalI18N.current["ui.settings.gesture.record"]) },
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
                        GestureRecorderActivity.start(context)
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
                        columns = StaggeredGridCells.Adaptive(adaptiveCardSizeMin),
                        contentPadding = paddingValues
                    ) {
                        items(state.gestures) {
                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                backgroundColor = ColorDefaults.backgroundSurfaceColor(),
                                shape = ShortXCardRoundedCornerShape,
                                elevation = 0.dp
                            ) {
                                Box {
                                    val popMenuState = listItemOverflowMenu(
                                        record = it,
                                        onDelete = {
                                            vm.delete(it.id)
                                        },
                                    )

                                    ListItem(
                                        title = it.label,
                                        icon = {
                                            ColoredIcon {
                                                RemixIcon(remixName = Remix.Editor.sketching)
                                            }
                                        },

                                        onClick = {
                                            popMenuState.open()
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
private fun listItemOverflowMenu(
    record: GestureRecord, onDelete: (GestureRecord) -> Unit
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
                onDelete(record)
            }

            else -> {}
        }
    })
    return menuState
}
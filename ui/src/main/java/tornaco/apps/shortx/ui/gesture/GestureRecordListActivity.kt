@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.gesture

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.common.ScreenOrientation
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.DisposableEffectWithLifeCycle
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXMediumTopAppBar
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.time.Duration
import java.util.Date

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


@Composable
fun GestureRecordListScreen(back: () -> Unit) {
    val vm = hiltViewModel<GestureRecordListViewModel>()
    val state by vm.state.collectAsState()
    DisposableEffectWithLifeCycle(
        onResume = {
            vm.loadState()
        }
    )

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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = paddingValues
                    ) {
                        items(state.gestures) {
                            Box {
                                val popMenuState = listItemOverflowMenu(
                                    record = it,
                                    onDelete = {
                                        vm.delete(it.id)
                                    },
                                    onRename = { gestureRecord, name ->
                                        vm.rename(gestureRecord, name)
                                    },
                                    onView = {
                                        GestureRecordeViewerActivity.start(context, it.id)
                                    }
                                )

                                val orientationString =
                                    if (it.screenOrientation == ScreenOrientation.ScreenOrientation_P) {
                                        LocalI18N.current["ui.common.screen.p"]
                                    } else {
                                        LocalI18N.current["ui.common.screen.p"]
                                    }
                                val durationString =
                                    DateUtils.formatDuration(Duration.ofMillis(it.duration))

                                ListItem(
                                    title = it.label,
                                    text1 = orientationString + "\t" + durationString,
                                    text2 = FuzzyDateTimeFormatter.getTimeAgo(
                                        context,
                                        Date(it.recordingTimeMillis)
                                    ),
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


@Composable
private fun listItemOverflowMenu(
    record: GestureRecord,
    onDelete: (GestureRecord) -> Unit,
    onRename: (GestureRecord, String) -> Unit,
    onView: (GestureRecord) -> Unit,
): DropdownSelectorState {
    val menuState = rememberDropdownSelectorState()

    val textInputState = rememberTextInputState(
        title = LocalI18N.current["ui.common.rename"],
        message = null,
        showSymbolButton = false,
        onSelected = {
            onRename(record, it)
        })
    TextInputDialog(textInputState)

    DropdownSelector(widthFraction = 0.33f, state = menuState, items = listOf(
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.view"]),
            data = "view",
            icon = {
                RemixIcon(remixName = Remix.Media.play_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.rename"]),
            data = "rename",
            icon = {
                RemixIcon(remixName = Remix.Editor.font_size)
            }),
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

            "rename" -> {
                textInputState.show(initialValue = record.label)
            }

            "view" -> {
                onView(record)
            }

            else -> {}
        }
    })
    return menuState
}
@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)

package tornaco.apps.shortx.ui.record

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.proto.common.AudioRecordFile
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.DateUtilsKotlin
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.theme.PrefIcon
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.io.File
import java.text.DecimalFormat
import java.util.Date
import kotlin.math.log10
import kotlin.math.pow

@AndroidEntryPoint
class AudioRecordActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, AudioRecordActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        AudioRecordScreen { finish() }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AudioRecordScreen(back: () -> Unit) {
    val vm = hiltViewModel<AudioRecordViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState()
    }
    val snackbarHostState = remember { SnackbarHostState() }
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val i18N = LocalI18N.current
            Scaffold(modifier = Modifier,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = i18N["ui.settings.audio.record"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {

                        }
                    )
                }) { paddingValues ->
                val pullRefreshState = rememberPullRefreshState(state.isLoading, { vm.loadState() })

                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                LaunchedEffect(vm) {
                    vm.exportPerformed.collectLatest {
                        when (it) {
                            is ExportResult.Success -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        i18N["ui.settings.audio.record.export.success", mapOf("dir" to it.path)]
                                    )
                                }
                            }

                            is ExportResult.Failed -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        it.error
                                    )
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .pullRefresh(pullRefreshState),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                TipRow(
                                    text = LocalI18N.current["ui.settings.audio.record.export.summary"]
                                )
                            }
                        }

                        items(state.records) { recordFile ->
                            val dateString = remember(recordFile.date) {
                                FuzzyDateTimeFormatter.getTimeAgo(context, Date(recordFile.date))
                            }
                            val fileSizeString = readableFileSize(recordFile.fileSize)
                            Box {
                                val popMenuState =
                                    listItemOverflowMenu(
                                        record = recordFile,
                                        onDelete = {
                                            vm.delete(it)
                                        },
                                        onRename = { record, name ->
                                            vm.rename(record, name)
                                        },
                                        onExport = {
                                            vm.export(recordFile)
                                        },
                                        onView = {
                                            playRecordingWithExternalApp(context, recordFile)
                                        }
                                    )

                                ListItem(
                                    titleLines = listOf(
                                        recordFile.name,
                                        DateUtilsKotlin.formatDuration(
                                            recordFile.duration
                                        ) + "\t\t" + fileSizeString + "\t\t" + dateString
                                    ),
                                    icon = {
                                        ColoredIcon {
                                            RemixIcon(remixName = Remix.Document.file_music_fill)
                                        }
                                    },
                                    action1 = {
                                        IconButton(onClick = {
                                            playRecordingWithExternalApp(context, recordFile)
                                        }) {
                                            RemixIcon(
                                                remixName = Remix.Media.play_line,
                                                tint = PrefIcon
                                            )
                                        }
                                    },
                                    onClick = {
                                        popMenuState.open()
                                    })
                            }
                        }
                    }

                    PullRefreshIndicator(
                        state.isLoading,
                        pullRefreshState,
                        Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@Composable
private fun listItemOverflowMenu(
    record: AudioRecordFile,
    onDelete: (AudioRecordFile) -> Unit,
    onRename: (AudioRecordFile, String) -> Unit,
    onExport: (AudioRecordFile) -> Unit,
    onView: (AudioRecordFile) -> Unit,
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
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.get"]),
            data = "export",
            icon = {
                RemixIcon(remixName = Remix.System.download_2_fill)
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
                textInputState.show(initialValue = record.name)
            }

            "export" -> {
                onExport(record)
            }

            "view" -> {
                onView(record)
            }

            else -> {}
        }
    })
    return menuState
}

fun readableFileSize(size: Long): String {
    if (size <= 0) return "0"
    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(
        size / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}

private fun playRecordingWithExternalApp(context: Context, audioFile: AudioRecordFile) {
    val uri: Uri = FileProvider.getUriForFile(
        context,
        "tornaco.apps.shortx",
        File(audioFile.path)
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "audio/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No suitable app found to play the recording", Toast.LENGTH_SHORT)
            .show()
    }
}
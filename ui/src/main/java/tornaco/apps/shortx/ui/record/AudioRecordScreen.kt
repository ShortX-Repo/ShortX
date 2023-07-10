@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)

package tornaco.apps.shortx.ui.record

import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.theme.PrefIcon
import tornaco.apps.shortx.ui.theme.ShortXTheme
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

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val i18N = LocalI18N.current
            Scaffold(modifier = Modifier,
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

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .pullRefresh(pullRefreshState),
                ) {
                    val showExportSuccessToast: (String) -> Unit = {
                        Toast.makeText(
                            context,
                            i18N["ui.settings.audio.record.export.success", mapOf("dir" to it)],
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val showExportFailToast: (String) -> Unit = {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    }

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
                                ListItem(
                                    modifier = Modifier.padding(end = 16.dp),
                                    titleLines = listOf(
                                        recordFile.name,
                                        dateString,
                                        fileSizeString
                                    ),
                                    icon = {
                                        ColoredIcon {
                                            RemixIcon(remixName = Remix.Document.file_2_line)
                                        }
                                    },
                                    action1 = {
                                        IconButton(onClick = {
                                            vm.delete(recordFile)
                                        }) {
                                            RemixIcon(
                                                remixName = Remix.System.delete_bin_5_fill,
                                                tint = PrefIcon
                                            )
                                        }
                                        IconButton(onClick = {
                                            runCatching { vm.export(recordFile) }.onSuccess {
                                                showExportSuccessToast(it)
                                            }.onFailure {
                                                showExportFailToast(it.message ?: it.toString())
                                            }
                                        }) {
                                            RemixIcon(
                                                remixName = Remix.System.download_2_fill,
                                                tint = PrefIcon
                                            )
                                        }
                                    },
                                    onClick = {
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

fun readableFileSize(size: Long): String {
    if (size <= 0) return "0"
    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(
        size / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}
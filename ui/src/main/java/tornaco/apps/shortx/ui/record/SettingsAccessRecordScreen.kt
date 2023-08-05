@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)

package tornaco.apps.shortx.ui.record

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.DisposableEffectWithLifeCycle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ClipboardUtils

@AndroidEntryPoint
class SettingsAccessRecordActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, SettingsAccessRecordActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        SettingsAccessRecordScreen { finish() }
    }
}

@Composable
fun SettingsAccessRecordScreen(back: () -> Unit) {
    val vm = hiltViewModel<SettingsAccessRecordViewModel>()
    val state by vm.collectAsState()

    val i18N = LocalI18N.current
    val context = LocalContext.current

    vm.collectSideEffect(sideEffect = {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    })

    DisposableEffectWithLifeCycle(onResume = {
        vm.loadRecords()
    })

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = i18N["ui.settings.settings.catcher.record.summary"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                vm.clearRecords()
                            }) {
                                RemixIcon(remixName = Remix.System.delete_bin_5_line)
                            }
                        }
                    )
                }) { paddingValues ->
                val pullRefreshState =
                    rememberPullRefreshState(state.isLoading, { vm.loadRecords() })
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .pullRefresh(pullRefreshState),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(state.data) { record ->
                            Column(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .clip(ShortXCardRoundedCornerShape)
                                    .clickable {
                                        ClipboardUtils.copyToClipboard(context, "url", record.url)
                                        Toast
                                            .makeText(
                                                context,
                                                i18N["ui.common.copied.to.clipboard"],
                                                Toast.LENGTH_LONG
                                            )
                                            .show()
                                    }) {
                                Text(
                                    text = record.url,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = W500
                                    )
                                )
                                TipBody(
                                    text = "${record.timeString}   ${
                                        record.isRead.select(
                                            "Read",
                                            "Write"
                                        )
                                    } --> " + record.value
                                )
                                Divider()
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
@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)

package tornaco.apps.shortx.ui.record

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.condition.ConditionIcon
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForCondition
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ErrorMessageDialog
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SwitchBar
import tornaco.apps.shortx.ui.base.rememberErrorMessageDialogState
import tornaco.apps.shortx.ui.theme.ErrorRed
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.theme.SuccessGreen

@AndroidEntryPoint
class ConditionEvaRecordActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, ConditionEvaRecordActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        ConditionEvaRecordScreen { finish() }
    }
}

@Composable
fun ConditionEvaRecordScreenDialog(state: CommonDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ConditionEvaRecordScreen(back = { state.dismiss() })
        }
    }
}

@Composable
fun ConditionEvaRecordScreen(back: () -> Unit) {
    val vm = hiltViewModel<ConditionEvaRecordViewModel>()
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
                        title = { Text(text = i18N["ui.settings.condition.record"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                vm.clear()
                            }) {
                                RemixIcon(remixName = Remix.System.delete_bin_5_fill)
                            }
                        }
                    )
                }) { paddingValues ->
                val pullRefreshState = rememberPullRefreshState(state.isLoading, { vm.loadState() })

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
                            SwitchBar(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 24.dp),
                                isChecked = state.isEnabled,
                                onCheckChange = { vm.toggleRecorder() })
                        }

                        items(state.records) {
                            Box {
                                val actionLabel = labelForCondition(
                                    i18N,
                                    it.actionIns
                                ).toMutableList().apply { add(it.formattedTimeStr) }
                                val messageDialogState = rememberErrorMessageDialogState(
                                    title = actionLabel[0],
                                    message = it.record.message
                                )
                                ErrorMessageDialog(state = messageDialogState)

                                ListItem(
                                    modifier = Modifier.padding(end = 16.dp),
                                    titleLines = actionLabel,
                                    icon = {
                                        ConditionIcon(it.actionIns)
                                    },
                                    action1 = {
                                        if (it.record.isSuccess) {
                                            if (it.record.isMatch) {
                                                RemixIcon(
                                                    remixName = Remix.System.checkbox_circle_fill,
                                                    tint = SuccessGreen
                                                )
                                            } else {
                                                RemixIcon(
                                                    remixName = Remix.System.indeterminate_circle_fill,
                                                    tint = SuccessGreen
                                                )
                                            }
                                        } else {
                                            RemixIcon(
                                                remixName = Remix.System.error_warning_fill,
                                                tint = ErrorRed
                                            )
                                        }
                                    },
                                    onClick = {
                                        if (!it.record.isSuccess) messageDialogState.show()
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
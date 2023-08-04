@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)

package tornaco.apps.shortx.ui.record

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionIcon
import tornaco.apps.shortx.ui.addrule.action.mapper.labelLinesForAction
import tornaco.apps.shortx.ui.addrule.condition.ConditionIcon
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForCondition
import tornaco.apps.shortx.ui.addrule.fact.FactIcon
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelForFact
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DisposableEffectWithLifeCycle
import tornaco.apps.shortx.ui.base.ErrorMessageDialog
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LinkText
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberErrorMessageDialogState
import tornaco.apps.shortx.ui.theme.ActionRecordColor
import tornaco.apps.shortx.ui.theme.ConditionRecordColor
import tornaco.apps.shortx.ui.theme.ErrorRed
import tornaco.apps.shortx.ui.theme.FactRecordColor
import tornaco.apps.shortx.ui.theme.NoteTextColor
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.theme.SuccessGreen

@AndroidEntryPoint
class ActionEvaRecordActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, ActionEvaRecordActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        ActionEvaRecordScreen { finish() }
    }
}

@Composable
fun ActionEvaRecordScreenDialog(state: CommonDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ActionEvaRecordScreen(back = { state.dismiss() })
        }
    }
}

@Composable
fun ActionEvaRecordScreen(back: () -> Unit) {
    val vm = hiltViewModel<EvaRecordViewModel>()
    val state by vm.collectAsState()

    DisposableEffectWithLifeCycle(onResume = {
        vm.loadRecords()
    })

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val i18N = LocalI18N.current
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = i18N["ui.settings.action.record"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                back()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                scope.launch {
                                    sheetState.expand()
                                }
                            }) {
                                RemixIcon(remixName = Remix.System.filter_2_fill)
                            }
                        }
                    )
                }) { paddingValues ->
                val pullRefreshState =
                    rememberPullRefreshState(state.loadState.isLoading(), { vm.loadRecords() })

                BottomSheetMenu(sheetState, vm)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .pullRefresh(pullRefreshState),
                ) {
                    when (val loadState = state.loadState) {
                        is LoadState.Error -> {
                            LoadError(Modifier.fillMaxSize())
                        }

                        LoadState.Loading -> {
                            Loading(Modifier.fillMaxSize())
                        }

                        is LoadState.Success -> {
                            RecordList(records = loadState.data)
                        }
                    }

                    PullRefreshIndicator(
                        state.loadState.isLoading(),
                        pullRefreshState,
                        Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordList(records: List<EvaRecord>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(records) {
            when (it) {
                is EvaRecord.ActionEvaluateRecordUM -> {
                    ActionRecordItem(it = it)
                }

                is EvaRecord.ConditionEvaluateRecordUM -> {
                    ConditionRecordItem(it = it)
                }

                is EvaRecord.FactPublishRecordUM -> {
                    FactRecordItem(it = it)
                }
            }
        }
    }
}

@Composable
fun BottomSheetMenu(sheetState: SheetState, vm: EvaRecordViewModel) {
    val scope = rememberCoroutineScope()
    val state by vm.collectAsState()
    if (sheetState.isVisible) {

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                }
            },
        ) {
            BackHandler {
                scope.launch { sheetState.hide() }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                CenterAlignedTopAppBar(title = { Text(LocalI18N.current["ui.common.filter"]) })

                FieldTitle(text = LocalI18N.current["ui.fact.title"])
                SmallSpacer()
                FilterChip(
                    selected = state.factSetting.show,
                    label = {
                        Text(text = LocalI18N.current["ui.common.filter.show"])
                    },

                    onClick = {
                        vm.showFact(!state.factSetting.show)
                    })

                LargeSpacer()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FieldTitle(text = LocalI18N.current["ui.conditions.title"])
                    LinkText(text = LocalI18N.current["ui.common.select.all"], icon = {}) {
                        val allOn =
                            state.conditionSetting.showMatch && state.conditionSetting.showNotMatch
                                    && state.conditionSetting.showError
                        vm.showConditionMatch(!allOn)
                        vm.showConditionNotMatch(!allOn)
                        vm.showConditionError(!allOn)
                    }
                }
                SmallSpacer()
                Row {
                    FilterChip(
                        selected = state.conditionSetting.showMatch,
                        label = {
                            Text(text = LocalI18N.current["ui.common.filter.matched"])
                        },
                        leadingIcon = {
                            RemixIcon(
                                remixName = Remix.System.checkbox_circle_fill,
                            )
                        },
                        onClick = {
                            vm.showConditionMatch(!state.conditionSetting.showMatch)
                        })
                    StandardSpacer()
                    FilterChip(
                        selected = state.conditionSetting.showNotMatch,
                        label = {
                            Text(text = LocalI18N.current["ui.common.filter.unmatched"])
                        },
                        leadingIcon = {
                            RemixIcon(
                                remixName = Remix.System.indeterminate_circle_fill,
                            )
                        },
                        onClick = {
                            vm.showConditionNotMatch(!state.conditionSetting.showNotMatch)
                        })
                    StandardSpacer()
                    FilterChip(
                        selected = state.conditionSetting.showError,
                        label = {
                            Text(text = LocalI18N.current["ui.common.filter.error"])
                        },
                        leadingIcon = {
                            RemixIcon(
                                remixName = Remix.System.error_warning_fill,
                            )
                        },
                        onClick = {
                            vm.showConditionError(!state.conditionSetting.showError)
                        })
                }

                LargeSpacer()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FieldTitle(text = LocalI18N.current["ui.actions.title"])
                    LinkText(text = LocalI18N.current["ui.common.select.all"], icon = {}) {
                        val allOn =
                            state.actionSetting.showSuccess
                                    && state.actionSetting.showError
                        vm.showActionSuccess(!allOn)
                        vm.showActionError(!allOn)
                    }
                }
                SmallSpacer()
                Row {
                    FilterChip(
                        selected = state.actionSetting.showSuccess,
                        label = {
                            Text(text = LocalI18N.current["ui.common.filter.success"])
                        },
                        leadingIcon = {
                            RemixIcon(
                                remixName = Remix.System.checkbox_circle_fill,
                            )
                        },
                        onClick = {
                            vm.showActionSuccess(!state.actionSetting.showSuccess)
                        })
                    StandardSpacer()
                    FilterChip(
                        selected = state.actionSetting.showError,
                        label = {
                            Text(text = LocalI18N.current["ui.common.filter.error"])
                        },
                        leadingIcon = {
                            RemixIcon(
                                remixName = Remix.System.error_warning_fill,
                            )
                        },
                        onClick = {
                            vm.showActionError(!state.actionSetting.showError)
                        })
                }

                LargeSpacer()
                LargeSpacer()
                LargeSpacer()
            }
        }
    }
}

@Composable
private fun ActionRecordItem(it: EvaRecord.ActionEvaluateRecordUM) {
    val i18N = LocalI18N.current
    Box {
        val actionLabel = labelLinesForAction(
            i18N,
            it.actionIns
        ).toMutableList().apply {
            if (it.record.srcTitle.isNotBlank()) {
                add("${it.record.srcEvent.displayLabel(i18N)}   ${it.record.srcTitle}")
            }
            add(it.formattedTimeStr)
        }
        val messageDialogState = rememberErrorMessageDialogState(
            title = actionLabel[0],
            message = it.record.message
        )
        ErrorMessageDialog(state = messageDialogState)

        ListItem(
            modifier = Modifier
                .background(ActionRecordColor.copy(RECORD_BG_ALPHA))
                .padding(end = 16.dp),
            titleLines = actionLabel,
            icon = {
                ActionIcon(it.actionIns)
            },
            action1 = {
                if (it.record.isSuccess) {
                    RemixIcon(
                        remixName = Remix.System.checkbox_circle_fill,
                        tint = SuccessGreen
                    )
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

@Composable
private fun ConditionRecordItem(it: EvaRecord.ConditionEvaluateRecordUM) {
    Box {
        val actionLabel = labelForCondition(
            LocalI18N.current,
            it.actionIns
        ).toMutableList().apply {
            if (it.record.message.isNotBlank()) {
                add(it.record.message)
            }
            add(it.formattedTimeStr)
        }
        val messageDialogState = rememberErrorMessageDialogState(
            title = actionLabel[0],
            message = it.record.message
        )
        ErrorMessageDialog(state = messageDialogState)

        ListItem(
            modifier = Modifier
                .background(ConditionRecordColor.copy(RECORD_BG_ALPHA))
                .padding(end = 16.dp),
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
                            tint = NoteTextColor
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

@Composable
private fun FactRecordItem(it: EvaRecord.FactPublishRecordUM) {
    Box {
        val actionLabel = mutableListOf<String>().apply {
            addAll(
                labelForFact(
                    LocalI18N.current,
                    it.factIns
                )
            )
            add(it.formattedTimeStr)
        }

        ListItem(
            modifier = Modifier
                .background(FactRecordColor.copy(RECORD_BG_ALPHA))
                .padding(end = 16.dp),
            titleLines = actionLabel,
            icon = {
                FactIcon(it.factIns)
            },
            onClick = {
            })
    }
}

private const val RECORD_BG_ALPHA = 0.048f
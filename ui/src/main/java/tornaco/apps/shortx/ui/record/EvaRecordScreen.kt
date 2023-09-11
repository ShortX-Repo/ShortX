@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)

package tornaco.apps.shortx.ui.record

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import tornaco.apps.shortx.core.context.isFromRule
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addda.AddDirectActionActivity
import tornaco.apps.shortx.ui.addrule.AddRuleActivity
import tornaco.apps.shortx.ui.addrule.action.ActionIcon
import tornaco.apps.shortx.ui.addrule.action.mapper.labelLinesForAction
import tornaco.apps.shortx.ui.addrule.condition.ConditionIcon
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForCondition
import tornaco.apps.shortx.ui.addrule.fact.FactIcon
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelForFact
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.DisposableEffectWithLifeCycle
import tornaco.apps.shortx.ui.base.ErrorMessageDialog
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LinkText
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchBar
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.rememberErrorMessageDialogState
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.theme.ActionRecordColor
import tornaco.apps.shortx.ui.theme.ConditionRecordColor
import tornaco.apps.shortx.ui.theme.ErrorRed
import tornaco.apps.shortx.ui.theme.FactRecordColor
import tornaco.apps.shortx.ui.theme.NoteTextColor
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.theme.SuccessGreen

@AndroidEntryPoint
class EvaRecordActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, EvaRecordActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        EvaRecordScreen { finish() }
    }
}

@Composable
fun EvaRecordScreen(back: () -> Unit) {
    val vm = hiltViewModel<EvaRecordViewModel>()
    val state by vm.collectAsState()

    val i18N = LocalI18N.current
    val context = LocalContext.current

    vm.collectSideEffect(sideEffect = {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    })

    DisposableEffectWithLifeCycle(onResume = {
        vm.loadSwitch()
        vm.loadRecords()
    })

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = i18N["ui.settings.eva.record"]) },
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
                            IconButton(onClick = {
                                openBottomSheet = true
                            }) {
                                RemixIcon(remixName = Remix.System.filter_2_line)
                            }
                        }
                    )
                }) { paddingValues ->
                val pullRefreshState =
                    rememberPullRefreshState(state.isLoading, { vm.loadRecords() })

                if (openBottomSheet) {
                    BottomSheetMenu(sheetState, vm) {
                        openBottomSheet = false
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
                            SwitchBar(
                                modifier = Modifier.padding(16.dp),
                                isChecked = state.isRecordEnabled,
                                onCheckChange = {
                                    vm.toggleRecordEnable(it)
                                })
                        }
                        items(state.data) { record ->
                            when (record) {
                                is EvaRecord.ActionEvaluateRecordUM -> {
                                    ActionRecordItem(
                                        it = record,
                                        viewSrc = {
                                            if (record.record.context.srcEvent.isFromRule) {
                                                AddRuleActivity.edit(context, it)
                                            } else {
                                                AddDirectActionActivity.edit(context, it)
                                            }
                                        },
                                        onShowThisSrc = {
                                            vm.setOnlyShowSrcId(it)
                                        }
                                    )
                                }

                                is EvaRecord.ConditionEvaluateRecordUM -> {
                                    ConditionRecordItem(
                                        it = record,
                                        viewSrc = {
                                            if (record.record.context.srcEvent.isFromRule) {
                                                AddRuleActivity.edit(context, it)
                                            } else {
                                                AddDirectActionActivity.edit(context, it)
                                            }
                                        },
                                        onShowThisSrc = {
                                            vm.setOnlyShowSrcId(it)
                                        }
                                    )
                                }

                                is EvaRecord.FactPublishRecordUM -> {
                                    FactRecordItem(it = record)
                                }
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
private fun BottomSheetMenu(
    sheetState: SheetState,
    vm: EvaRecordViewModel,
    onDismissRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state by vm.collectAsState()
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismissRequest() },
    ) {
        BackHandler {
            scope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismissRequest()
                }
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            CenterAlignedTopAppBar(title = { Text(LocalI18N.current["ui.common.filter"]) })

            val subscribeState by LVLStateHolder.collectAsState()
            if (subscribeState.isSubscribed) {
                if (state.onlyShowSrc != null) {
                    StandardSpacer()
                    ListItem(
                        modifier = Modifier
                            .clip(ShortXCardRoundedCornerShape)
                            .background(color = MaterialTheme.colorScheme.primaryContainer),
                        title = LocalI18N.current["ui.common.only.show"] + " ${state.onlyShowSrc?.srcTitle}",
                        action1 = {
                            IconButton(onClick = {
                                vm.setOnlyShowSrcId(null)
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        })
                }

                LargeSpacer()
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
            } else {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OutlineBadge(text = LocalI18N.current["ui.subscription.subscribed.only"])
                }
            }
        }
    }
}

@Composable
private fun ActionRecordItem(
    it: EvaRecord.ActionEvaluateRecordUM,
    viewSrc: (String) -> Unit,
    onShowThisSrc: (EvaluateContext) -> Unit,
) {
    val i18N = LocalI18N.current
    Box {
        val actionLabel = labelLinesForAction(
            i18N,
            it.actionIns
        ).toMutableList().apply {
            add(it.formattedTimeStr)
        }
        val messageDialogState = rememberErrorMessageDialogState(
            title = actionLabel[0],
            message = it.record.message
        )
        ErrorMessageDialog(state = messageDialogState)

        CommonRecordListItem(
            modifier = Modifier
                .background(ActionRecordColor.copy(RECORD_BG_ALPHA))
                .padding(end = 16.dp),
            title = actionLabel[0],
            messages = if (actionLabel.size > 1) actionLabel.subList(
                1,
                actionLabel.size
            ) else emptyList(),
            event = "${it.record.context.srcEvent.displayLabel(i18N)}   ${it.record.context.srcTitle}",
            canViewSrc = it.canViewSrc,
            onLinkClick = {
                viewSrc(it.record.context.srcId)
            },
            onlyShowThisSrc = {
                onShowThisSrc(it.record.context)
            },
            icon = {
                ActionIcon(it.actionIns)
            },
            overflow = {
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
private fun ConditionRecordItem(
    it: EvaRecord.ConditionEvaluateRecordUM,
    viewSrc: (String) -> Unit,
    onShowThisSrc: (EvaluateContext) -> Unit,
) {
    val i18N = LocalI18N.current
    Box {
        val conditionLabel = labelForCondition(
            i18N,
            it.actionIns
        ).toMutableList().apply {
            if (it.record.message.isNotBlank()) {
                add(it.record.message)
            }
            add(it.formattedTimeStr)
        }
        val messageDialogState = rememberErrorMessageDialogState(
            title = conditionLabel[0],
            message = it.record.message
        )
        ErrorMessageDialog(state = messageDialogState)

        CommonRecordListItem(
            modifier = Modifier
                .background(ConditionRecordColor.copy(RECORD_BG_ALPHA))
                .padding(end = 16.dp),
            title = conditionLabel[0],
            messages = if (conditionLabel.size > 1) conditionLabel.subList(
                1,
                conditionLabel.size
            ) else emptyList(),
            event = "${it.record.context.srcEvent.displayLabel(i18N)}   ${it.record.context.srcTitle}",
            canViewSrc = it.canViewSrc,
            onLinkClick = {
                viewSrc(it.record.context.srcId)
            },
            onlyShowThisSrc = {
                onShowThisSrc(it.record.context)
            },
            icon = {
                ConditionIcon(it.actionIns)
            },
            overflow = {
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
        val factLabels = mutableListOf<String>().apply {
            addAll(
                labelForFact(
                    LocalI18N.current,
                    it.factIns
                )
            )
            add(it.formattedTimeStr)
        }

        CommonRecordListItem(
            modifier = Modifier
                .background(FactRecordColor.copy(RECORD_BG_ALPHA))
                .padding(end = 16.dp),
            title = factLabels[0],
            messages = if (factLabels.size > 1) factLabels.subList(
                1,
                factLabels.size
            ) else emptyList(),
            icon = {
                FactIcon(it.factIns)
            },
            event = null,
            canViewSrc = false,
            onClick = {
            })
    }
}


@Composable
private fun CommonRecordListItem(
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit),
    title: String,
    messages: List<String> = emptyList(),
    event: String? = null,
    canViewSrc: Boolean,
    overflow: @Composable ColumnScope.() -> Unit = {},
    onClick: () -> Unit,
    onLinkClick: () -> Unit = {},
    onlyShowThisSrc: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(1f, fill = false)
                .padding(end = 24.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon.invoke()
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                    )
                )

                if (event != null) {
                    if (canViewSrc) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinkText(text = event, onClick = onLinkClick)
                            StandardSpacer()
                            ColoredIcon(
                                color = Color.Transparent,
                                size = 24.dp,
                                onClick = {
                                    onlyShowThisSrc()
                                }) {
                                RemixIcon(
                                    modifier = Modifier.size(18.dp),
                                    remixName = Remix.System.filter_2_line
                                )
                            }
                        }
                    } else {
                        TipBody(text = event)
                    }
                }

                TipBody(text = messages.joinToString(System.lineSeparator()), maxLines = 12)
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            overflow()
        }
    }
}

private const val RECORD_BG_ALPHA = 0.1f
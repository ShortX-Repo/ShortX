@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.compat.ManifestCompat
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.AddRuleActivity
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.EmptyList
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.dynamicStaggeredGridCells
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.share.SharedData
import tornaco.apps.shortx.ui.share.ViewerActivity
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.util.Permissions.shouldCheckStoragePermissionBeforeWriteDownloads


@Composable
fun RuleScreen() {
    val vm = hiltViewModel<MainViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadRules(delay = true)
    }

    val context = LocalContext.current

    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { fileUri ->
        if (fileUri != null) {
            vm.importRuleFromUri(fileUri)
        }
    }

    val i18N = LocalI18N.current
    LaunchedEffect(vm) {
        vm.importComplete.collectLatest {
            when (it) {
                is ImportResult.Success -> {
                    Toast.makeText(context, i18N["ui.common.import.success"], Toast.LENGTH_SHORT)
                        .show()
                }

                is ImportResult.Fail -> {
                    Toast.makeText(
                        context,
                        i18N["ui.common.import.fail", mapOf("why" to it.reason.description)],
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    MainScaffold(
        state = state,
        appBarTitleKey = "ui.feat.label.rule",
        appBarActions = {
        },
        onClipboardClick = {
            vm.importRuleFromClipboard()
        },
        onFileClick = {
            pickFileLauncher.launch("text/plain")
        },
        onAddClick = {
            AddRuleActivity.start(context)
        },
        onUrlClick = {
            ViewerActivity.start(context, SharedData("ruleUrl", it))
        },
        onNewSetClick = {},
        acceptPrivacyStatement = {
            vm.acceptPrivacyStatement()
        }
    ) {
        val shareDialogState = rememberShareDialogState("share.shortx")
        ShareDialog(state = shareDialogState)

        val permissionRequest = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {})

        RuleContent(
            state = state,
            onDup = {
                vm.dupRule(it.id)
            },
            onDelete = { rule ->
                vm.deleteRule(rule.id)
            },
            onEdit = { rule ->
                AddRuleActivity.start(context, rule.id)
            },
            onShare = { rule ->
                if (context.shouldCheckStoragePermissionBeforeWriteDownloads()) {
                    permissionRequest.launch(
                        arrayOf(
                            ManifestCompat.permission.READ_EXTERNAL_STORAGE,
                            ManifestCompat.permission.WRITE_EXTERNAL_STORAGE,
                        )
                    )
                } else {
                    vm.ruleToShareString(rule)?.let {
                        shareDialogState.show(rule.title, it)
                    }
                }
            },
            onRuleClick = { rule ->
                AddRuleActivity.start(context, rule.id)
            },
            setRuleEnabled = { id, enable ->
                vm.setRuleEnabled(id, enable)
            },
            onExecute = {
                vm.directExecuteRuleActions(it)
            },
            stopAllJobs = {
                vm.stopJobs(it)
            }
        )
    }

}

@Composable
private fun RuleContent(
    state: MainState,
    onDup: (RuleUM) -> Unit,
    onDelete: (RuleUM) -> Unit,
    onEdit: (RuleUM) -> Unit,
    onShare: (RuleUM) -> Unit,
    onRuleClick: (RuleUM) -> Unit,
    onExecute: (RuleUM) -> Unit,
    stopAllJobs: (RuleUM) -> Unit,
    setRuleEnabled: (String, Boolean) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp), contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }

    if (!state.isLoading && state.rules.isEmpty()) {
        EmptyList(modifier = Modifier.fillMaxSize())
    } else {
        val subscribeState by LVLStateHolder.subscribeState.collectAsState()
        RuleListContent(
            isSubscribed = subscribeState.isSubscribed,
            rules = state.rules,
            onRuleClick = onRuleClick,
            setRuleEnabled = setRuleEnabled,
            onDup = onDup,
            onDelete = onDelete,
            onEdit = onEdit,
            onShare = onShare,
            onExecute = onExecute,
            stopAllJobs = stopAllJobs
        )
    }

}

@Composable
private fun RuleListContent(
    isSubscribed: Boolean,
    rules: List<RuleUM>,
    onRuleClick: (RuleUM) -> Unit,
    setRuleEnabled: (String, Boolean) -> Unit = { id, enable -> },
    onDup: (RuleUM) -> Unit = {},
    onDelete: (RuleUM) -> Unit = {},
    onEdit: (RuleUM) -> Unit = {},
    onShare: (RuleUM) -> Unit = {},
    onExecute: (RuleUM) -> Unit = {},
    stopAllJobs: (RuleUM) -> Unit
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        columns = dynamicStaggeredGridCells(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp)
    ) {
        items(
            items = rules,
            key = { listItem: RuleUM -> listItem.id + listItem.updateTime }) { rule ->

            var scaleAnimStarted by remember {
                mutableStateOf(false)
            }
            val scalePercent by animateFloatAsState(
                targetValue = if (scaleAnimStarted) 0.99f else 1f,
                animationSpec = spring(),
                finishedListener = {
                    scaleAnimStarted = false
                }, label = "scalePercent"
            )

            ShortXCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .scale(scalePercent),
            ) {
                RuleItem2(
                    rule = rule,
                    isSubscribed = isSubscribed,
                    onClick = {
                        onRuleClick(rule)
                        scaleAnimStarted = true
                    },
                    setRuleEnabled = setRuleEnabled,
                    onDup = onDup,
                    onDelete = onDelete,
                    onEdit = onEdit,
                    onShare = onShare,
                    onExecute = onExecute,
                    stopAllJobs = stopAllJobs
                )
            }

        }
    }
}

@Composable
private fun RuleItem2(
    rule: RuleUM,
    isSubscribed: Boolean,
    onClick: () -> Unit,
    setRuleEnabled: (String, Boolean) -> Unit,
    onDup: (RuleUM) -> Unit,
    onDelete: (RuleUM) -> Unit,
    onEdit: (RuleUM) -> Unit,
    onShare: (RuleUM) -> Unit,
    onExecute: (RuleUM) -> Unit,
    stopAllJobs: (RuleUM) -> Unit
) {
    Box {
        val popMenuState = listItemOverflowMenu(
            rule = rule,
            isSubscribed = isSubscribed,
            onDup = onDup,
            onDelete = onDelete,
            onEdit = onEdit,
            onShare = onShare,
            onExecute = onExecute,
        )
        Column(modifier = Modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .combinedClickable(onLongClick = {
                popMenuState.open()
            }) {
                onClick()
            }) {

            RuleIcon(rule)

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 6.dp)
            ) {
                Text(
                    text = rule.title, style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp
                    )
                )
                TinySpacer()
                Text(
                    text = rule.description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                MediumSpacer()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RemixIcon(
                            modifier = Modifier.size(12.dp),
                            remixName = Remix.System.refresh_line
                        )
                        TinySpacer()
                        Text(
                            text = rule.updateTime,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                        )
                    }
                    ShortXSwitch(
                        modifier = Modifier,
                        checked = rule.isEnabled,
                        onCheckedChange = {
                            setRuleEnabled(rule.id, it)
                        },
                    )
                }
                MediumSpacer()
            }

            AnimatedVisibility(visible = rule.runningInsCount > 0) {
                RunningIndicator(
                    modifier = Modifier.padding(start = 12.dp),
                    runningInsCount = rule.runningInsCount,
                    stopAllJobs = {
                        stopAllJobs(rule)
                    }
                )
            }
            MediumSpacer()
        }
    }
}

@Composable
fun RuleIcon(rule: RuleUM, arrow: String = Remix.Arrows.arrow_right_line) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ColoredIcon(
            size = 32.dp,
            padding = 0.dp,
            color = Color(rule.factIconColor).copy(alpha = ActionColorBgAlpha)
        ) {
            RemixIcon(
                modifier = Modifier.size(16.dp),
                remixName = rule.factIcon,
                tint = Color(rule.factIconColor)
            )
        }

        RemixIcon(
            modifier = Modifier.size(18.dp),
            remixName = arrow,
            tint = MaterialTheme.colorScheme.surfaceVariant
        )

        ColoredIcon(
            size = 32.dp,
            padding = 0.dp,
            color = Color(rule.actionIconColor).copy(alpha = ActionColorBgAlpha)
        ) {
            RemixIcon(
                modifier = Modifier.size(16.dp),
                remixName = rule.actionIcon,
                tint = Color(rule.actionIconColor)
            )
        }
    }
}

@Composable
private fun listItemOverflowMenu(
    rule: RuleUM,
    isSubscribed: Boolean,
    onDup: (RuleUM) -> Unit,
    onDelete: (RuleUM) -> Unit,
    onEdit: (RuleUM) -> Unit,
    onShare: (RuleUM) -> Unit,
    onExecute: (RuleUM) -> Unit,
): DropdownSelectorState {
    val i18N = LocalI18N.current
    val badge = if (isSubscribed) {
        null
    } else i18N["ui.subscription.subscribed.only"]

    val subscribeDialogState = rememberCommonDialogState()
    FeatureAccessDialog(subscribeDialogState)

    val deleteDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = i18N["ui.common.delete"],
        state = deleteDialogState,
        data = rule,
        messageHint = { i18N["ui.delete.dialog.confirm", mapOf("wht" to rule.title)] },
        onConfirm = onDelete
    )

    val menuState = rememberDropdownSelectorState()

    DropdownSelector(
        widthFraction = 0.3f,
        state = menuState,
        items = listOf(
            DropdownItem(labelLines = listOf(i18N["ui.common.execute.actions"]),
                data = "execute",
                icon = {
                    RemixIcon(Remix.Media.play_fill)
                }),
            DropdownItem(labelLines = listOf(i18N["ui.common.dup"]),
                data = "dup",
                badge = badge,
                icon = {
                    RemixIcon(Remix.Document.file_copy_2_fill)
                }),
            DropdownItem(labelLines = listOf(i18N["ui.common.edit"]),
                data = "edit",
                icon = {
                    RemixIcon(Remix.Design.edit_line)
                }),
            DropdownItem(
                labelLines = listOf(i18N["ui.common.share"]),
                data = "share",
                badge = badge,
                icon = {
                    RemixIcon(Remix.System.share_line)
                }),
            DropdownItem(
                labelLines = listOf(i18N["ui.common.delete"]),
                data = "delete",
                icon = {
                    RemixIcon(Remix.System.delete_bin_4_line)
                })
        ), onSelect = {
            Logger("RuleMenu").d("onSelect: ${it.data}")
            when (it.data) {
                "dup" -> {
                    if (isSubscribed) {
                        onDup(rule)
                    } else {
                        subscribeDialogState.show()
                    }
                }

                "delete" -> {
                    deleteDialogState.show()
                }

                "edit" -> {
                    onEdit(rule)
                }

                "share" -> {
                    if (isSubscribed) {
                        onShare(rule)
                    } else {
                        subscribeDialogState.show()
                    }
                }

                "execute" -> {
                    onExecute(rule)
                }

                else -> {}
            }
        })
    return menuState
}
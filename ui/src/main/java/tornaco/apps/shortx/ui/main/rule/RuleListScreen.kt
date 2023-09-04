@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main.rule

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.compat.ManifestCompat
import tornaco.apps.shortx.ui.addrule.AddRuleActivity
import tornaco.apps.shortx.ui.base.EmptyList
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.ShareDialog
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.dynamicStaggeredGridCells
import tornaco.apps.shortx.ui.base.rememberShareDialogState
import tornaco.apps.shortx.ui.main.MainScaffold
import tornaco.apps.shortx.ui.main.MainViewModel
import tornaco.apps.shortx.ui.main.model.MainState
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.share.SharedData
import tornaco.apps.shortx.ui.share.ViewerActivity
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.util.ClipboardUtils
import tornaco.apps.shortx.ui.util.Permissions.shouldCheckStoragePermissionBeforeWriteDownloads
import java.io.File


@Composable
fun RuleScreen() {
    val mainViewModel = hiltViewModel<MainViewModel>()
    val mainState by mainViewModel.state.collectAsState()

    val ruleListViewModel = hiltViewModel<RuleListViewModel>()
    val ruleListState by ruleListViewModel.state.collectAsState()

    LaunchedEffect(mainViewModel) {
        ruleListViewModel.loadRuleList(delay = true)
    }

    val context = LocalContext.current

    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { fileUri ->
        if (fileUri != null) {
            ViewerActivity.start(context, fileUri)
        }
    }

    MainScaffold(
        state = mainState,
        appBarTitleKey = "ui.feat.label.rule",
        appBarActions = {
        },
        onClipboardClick = {
            ClipboardUtils.readClipboard(context)?.let { clip ->
                val file = File(context.cacheDir, "clipboard.content")
                file.writeText(clip)
                ViewerActivity.start(context, file.toUri())
            }
        },
        onFileClick = {
            pickFileLauncher.launch("text/plain")
        },
        onAddClick = {
            AddRuleActivity.start(context)
        },
        onUrlClick = {
            ViewerActivity.start(context, SharedData(SharedData.TYPE_RULE_URL, it))
        },
        onNewSetClick = {},
        acceptPrivacyStatement = {
            mainViewModel.acceptPrivacyStatement()
        }
    ) {
        val shareDialogState = rememberShareDialogState("share.shortx")
        ShareDialog(state = shareDialogState)

        val permissionRequest = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {})

        val props = LocalShortXProps.current

        RuleContent(
            state = mainState,
            ruleListState = ruleListState,
            onDup = {
                ruleListViewModel.dupRule(it.id, props.firstOrNull())
            },
            onDelete = { rule ->
                ruleListViewModel.deleteRule(rule.id)
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
                    ruleListViewModel.ruleToShareString(rule)?.let {
                        shareDialogState.show(rule.title, it)
                    }
                }
            },
            onRuleClick = { rule ->
                AddRuleActivity.start(context, rule.id)
            },
            setRuleEnabled = { id, enable ->
                ruleListViewModel.setRuleEnabled(id, enable)
            },
            onExecute = {
                ruleListViewModel.directExecuteRuleActions(it)
            },
            stopAllJobs = {
                ruleListViewModel.stopJobs(it)
            }
        )
    }

}

@Composable
private fun RuleContent(
    state: MainState,
    ruleListState: RuleListState,
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
        if (ruleListState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }

    if (!ruleListState.isLoading && ruleListState.rules.isEmpty()) {
        EmptyList(modifier = Modifier.fillMaxSize())
    } else {
        val subscribeState by LVLStateHolder.subscribeState.collectAsState()
        RuleListContent(
            isSubscribed = subscribeState.isSubscribed,
            rules = ruleListState.rules,
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
            ShortXCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                RuleItem2(
                    rule = rule,
                    isSubscribed = isSubscribed,
                    onClick = {
                        onRuleClick(rule)
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

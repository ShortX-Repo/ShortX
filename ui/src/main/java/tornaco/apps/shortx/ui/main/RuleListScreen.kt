@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.base.LoadState.Loading.data
import tornaco.apps.shortx.ui.util.ClipboardUtils


@Composable
fun RuleScreen() {
    val vm = hiltViewModel<MainViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadRules()
    }

    val appNavController = LocalNavHostController.current
    val context = LocalContext.current
    MainScaffold(
        state = state,
        appBarTitleKey = "ui.feat.label.rule",
        appBarActions = {
            androidx.compose.material3.IconButton(onClick = {
                vm.toggleRuleColumnCount()
            }) {
                Icon(
                    imageVector = Icons.Filled.ViewColumn,
                    contentDescription = "Column"
                )
            }
        },
        goToSettings = {
            appNavController.navigate(AppRoute.Settings.name)
        },
        onClipboardClick = {
            vm.parseShareContentRule(ClipboardUtils.readClipboard(context))
                ?.let {
                    vm.importRule(it)
                }
        },
        onAddClick = {
            appNavController.navigate(AppRoute.AddRule.name)
        },
        acceptPrivacyStatement = {
            vm.acceptPrivacyStatement()
        }
    ) {
        val shareDialogState = rememberShareDialogState("share.shortx")
        ShareDialog(state = shareDialogState)

        RuleContent(
            state = state,
            onDelete = { rule ->
                vm.deleteRule(rule.id)
            },
            onEdit = { rule ->
                appNavController.navigate("${AppRoute.AddRule.name}?ruleId=${rule.id}")
            },
            onShare = { um ->
                vm.ruleToShareString(um)?.let {
                    shareDialogState.show(it)
                }
            },
            onRuleClick = { rule ->
                vm.setRuleEnabled(rule.id, !rule.isEnabled)
            },
            setRuleEnabled = { id, enable ->
                vm.setRuleEnabled(id, enable)
            }
        )
    }

}

@Composable
private fun RuleContent(
    state: MainState,
    onDelete: (RuleUM) -> Unit,
    onEdit: (RuleUM) -> Unit,
    onShare: (RuleUM) -> Unit,
    onRuleClick: (RuleUM) -> Unit,
    setRuleEnabled: (String, Boolean) -> Unit,
) {
    when {
        state.rules.isSuccess() && state.rules.data.isNullOrEmpty() -> {
            EmptyList(modifier = Modifier.fillMaxSize())
        }
        state.rules.isSuccess() -> {
            LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxSize(),
                columns = StaggeredGridCells.Fixed(state.ruleColumnCount),
                contentPadding = PaddingValues(top = 16.dp, bottom = 72.dp)
            ) {
                items(
                    items = state.rules.data.orEmpty(),
                    key = { listItem: RuleUM -> listItem.id + listItem.updateTime }) { rule ->

                    var scaleAnimStarted by remember {
                        mutableStateOf(false)
                    }
                    val scalePercent by animateFloatAsState(
                        targetValue = if (scaleAnimStarted) 0.99f else 1f,
                        animationSpec = spring(),
                        finishedListener = {
                            scaleAnimStarted = false
                        }
                    )

                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .scale(scalePercent),
                        backgroundColor = ColorDefaults.backgroundSurfaceColor(),
                        shape = ShortXCardRoundedCornerShape,
                        elevation = 0.dp
                    ) {
                        if (state.ruleColumnCount == 1) {
                            RuleItem(
                                rule = rule,
                                onClick = {
                                    onRuleClick(rule)
                                    scaleAnimStarted = true
                                },
                                setRuleEnabled = setRuleEnabled,
                                onDelete = onDelete,
                                onEdit = onEdit,
                                onShare = onShare,
                            )
                        } else {
                            RuleItem2(
                                rule = rule,
                                onClick = {
                                    onRuleClick(rule)
                                    scaleAnimStarted = true
                                },
                                setRuleEnabled = setRuleEnabled,
                                onDelete = onDelete,
                                onEdit = onEdit,
                                onShare = onShare,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RuleItem(
    rule: RuleUM,
    onClick: () -> Unit,
    setRuleEnabled: (String, Boolean) -> Unit,
    onDelete: (RuleUM) -> Unit,
    onEdit: (RuleUM) -> Unit,
    onShare: (RuleUM) -> Unit,
) {
    ListItem(
        icon = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RuleIcon(rule = rule, arrow = Icons.Filled.ArrowDropDown)
            }
        },
        title = rule.title,
        text1 = rule.description,
        text2 = LocalI18N.current["ui.update.time", mapOf("date" to rule.updateTime)],
        overflow = {
            ListItemOverflowMenu(rule, onDelete, onEdit, onShare)
        },
        checked = rule.isEnabled,
        onClick = onClick,
        onCheckedChange = { setRuleEnabled(rule.id, it) }
    )
}


@Composable
private fun RuleItem2(
    rule: RuleUM,
    onClick: () -> Unit,
    setRuleEnabled: (String, Boolean) -> Unit,
    onDelete: (RuleUM) -> Unit,
    onEdit: (RuleUM) -> Unit,
    onShare: (RuleUM) -> Unit,
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(ShortXCardRoundedCornerShape)
        .clickable {
            onClick()
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RuleIcon(rule)
            }

            Column(horizontalAlignment = Alignment.End) {
                ListItemOverflowMenu(rule, onDelete, onEdit, onShare)
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = rule.title, style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp
                )
            )
            TinySpacer()
            Text(
                text = rule.description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                maxLines = 8
            )
            TinySpacer()
            TinySpacer()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(modifier = Modifier.size(12.dp), imageVector = Icons.Outlined.Update)
                    TinySpacer()
                    Text(
                        text = rule.updateTime,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                    )
                }
                Switch(
                    modifier = Modifier,
                    checked = rule.isEnabled,
                    onCheckedChange = {
                        setRuleEnabled(rule.id, it)
                    }
                )
            }
        }

    }
}

@Composable
private fun RuleIcon(rule: RuleUM, arrow: ImageVector = Icons.Filled.ArrowRight) {
    ColoredIcon(size = 32.dp, padding = 0.dp) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = rule.factIcon,
            contentDescription = "Icon"
        )
    }

    Icon(
        modifier = Modifier.size(18.dp),
        imageVector = arrow,
        tint = MaterialTheme.colorScheme.surfaceVariant
    )

    ColoredIcon(size = 32.dp, padding = 0.dp) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = rule.actionIcon,
            contentDescription = "Icon"
        )
    }
}

@Composable
private fun ListItemOverflowMenu(
    rule: RuleUM,
    onDelete: (RuleUM) -> Unit,
    onEdit: (RuleUM) -> Unit,
    onShare: (RuleUM) -> Unit,
) {
    val deleteDialogState = rememberDeleteConfirmDialogState()
    DeleteConfirmDialog(
        state = deleteDialogState,
        data = rule,
        messageHint = { rule.title },
        onDelete = onDelete
    )

    Box {
        val menuState = rememberDropdownSelectorState()
        IconButton(
            modifier = Modifier,
            onClick = {
                menuState.open()
            }) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More")
        }

        DropdownSelector(
            widthFraction = 0.3f,
            state = menuState,
            items = listOf(
                DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.edit"]),
                    data = "edit",
                    icon = {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
                    }),
                DropdownItem(
                    labelLines = listOf(LocalI18N.current["ui.common.share"]),
                    data = "share",
                    icon = {
                        Icon(imageVector = Icons.Outlined.Share, contentDescription = "Share")
                    }),
                DropdownItem(
                    labelLines = listOf(LocalI18N.current["ui.common.delete"]),
                    data = "delete",
                    icon = {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete")
                    })
            ), onSelect = {
                when (it.data) {
                    "delete" -> {
                        deleteDialogState.show()
                    }
                    "edit" -> {
                        onEdit(rule)
                    }
                    "share" -> {
                        onShare(rule)
                    }
                    else -> {}
                }
            })
    }
}
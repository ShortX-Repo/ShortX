@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main.rule

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlphaLarge
import tornaco.apps.shortx.ui.base.ClickAnimateContainer
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.LocalUiSettings
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberClickAnimateState
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.main.da.RunningIndicator
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.record.EvaRecordActivity
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog
import tornaco.apps.shortx.ui.theme.Dimens
import tornaco.apps.shortx.ui.theme.Dimens.menuDropdownFraction
import tornaco.apps.shortx.ui.util.themedTextColor
import tornaco.apps.shortx.core.i18n.I18N_R


@Composable
fun RuleItem(
    modifier: Modifier = Modifier,
    rule: RuleUM,
    isSubscribed: Boolean,
    showDescription: Boolean = true,
    runningInsCount: Int,
    isSelectionMode: Boolean = false,
    selected: Boolean = false,
    onSelectionChange: (Boolean) -> Unit = {}
) {
    val handler = LocalRuleItemHandler.current
    val iconColor = ruleIconColor(rule)
    // TODO Animate item
    Box {
        val popMenuState = listItemOverflowMenu(
            rule = rule,
            isSubscribed = isSubscribed,
        )
        val clickAnimateState =
            rememberClickAnimateState(animateColor = iconColor)
        ClickAnimateContainer(clickAnimateState) {
            RuleItem(
                modifier = modifier.then(it),
                popMenuState = popMenuState,
                rule = rule,
                showDescription = showDescription,
                runningInsCount = runningInsCount,
                isSelectionMode = isSelectionMode,
                selected = selected,
                onSelectionChange = onSelectionChange
            )
        }
    }
}

@Composable
private fun RuleItem(
    modifier: Modifier,
    popMenuState: DropdownSelectorState,
    rule: RuleUM,
    showDescription: Boolean,
    runningInsCount: Int,
    isSelectionMode: Boolean,
    selected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
) {
    val handler = LocalRuleItemHandler.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .combinedClickable(onLongClick = {
                if (isSelectionMode) {
                    onSelectionChange(!selected)
                } else {
                    popMenuState.open()
                }
            }) {
                if (isSelectionMode) {
                    onSelectionChange(!selected)
                } else {
                    handler.click(rule)
                }
            }
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .padding(bottom = 12.dp)
    ) {
        val iconColor = ruleIconColor(rule)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RuleIcon(
                rule = rule,
                runningInsCount = runningInsCount,
                stopAllJobs = {
                    handler.stopAllJobs(rule)
                },
                isSelectionMode = isSelectionMode,
                selected = selected
            ) {
                if (isSelectionMode) {
                    onSelectionChange(!selected)
                } else {
                    popMenuState.open()
                }
            }
            if (isSelectionMode) {
                RemixIcon(
                    modifier = Modifier.size(20.dp),
                    remixName = if (selected) Remix.System.checkbox_circle_fill else Remix.System.checkbox_blank_circle_line,
                    tint = if (selected) MaterialTheme.colorScheme.primary else Color.Unspecified
                )
            } else {
                ShortXSwitch(
                    modifier = Modifier,
                    checked = rule.isEnabled,
                    stock = false,
                    onCheckedChange = {
                        handler.setRuleEnabled(rule, it)
                    },
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(top = 6.dp)
        ) {
            val colorfulCard = LocalUiSettings.current.uiColorfulCard
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = rule.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = if (showDescription) 14.5.sp else 13.5.sp
                    ),
                    color = colorfulCard.select(
                        themedTextColor(color = iconColor),
                        Color.Unspecified
                    )
                )
                if (rule.isPinned) {
                    RemixIcon(
                        modifier = Modifier.size(16.dp),
                        remixName = Remix.Map.pushpin_fill,
                        tint = colorfulCard.select(
                            themedTextColor(color = iconColor),
                            MaterialTheme.colorScheme.primary
                        )
                    )
                }
                if (rule.isLocked) {
                    RemixIcon(
                        modifier = Modifier.size(16.dp),
                        remixName = Remix.System.lock_fill,
                        tint = colorfulCard.select(
                            themedTextColor(color = iconColor),
                            MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
            androidx.compose.animation.AnimatedVisibility(showDescription) {
                Text(
                    modifier = Modifier.padding(top = 0.dp),
                    text = rule.description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = colorfulCard.select(
                        themedTextColor(color = iconColor),
                        Color.Unspecified
                    )
                )
            }
        }
    }
}

@Composable
fun RuleIcon(
    rule: RuleUM,
    runningInsCount: Int,
    stopAllJobs: () -> Unit,
    isSelectionMode: Boolean = false,
    selected: Boolean = false,
    onClick: (() -> Unit)
) {
    val isRunning = runningInsCount > 0
    val iconColor = ruleIconColor(rule)
    if (isRunning && !isSelectionMode) {
        RunningIndicator(runningInsCount, stopAllJobs, iconColor)
    } else {
        val colorfulCard = LocalUiSettings.current.uiColorfulCard
        Box {
            ColoredIcon(
                onClick = onClick,
                size = Dimens.RuleIconSize,
                padding = 0.dp,
                color = colorfulCard.select(
                    t = {
                        iconColor
                    },
                    f = {
                        iconColor.copy(alpha = if (rule.isEnabled) ActionColorBgAlphaLarge else ActionColorBgAlpha)
                    })
            ) {
                RemixIcon(
                    modifier = Modifier.size(16.dp).alpha(if (isSelectionMode && selected) 0.2f else 1f),
                    remixName = rule.icon,
                    tint = colorfulCard.select(Color.White, iconColor)
                )
            }
            if (isSelectionMode && selected) {
                RemixIcon(
                    modifier = Modifier.align(Alignment.Center).size(18.dp),
                    remixName = Remix.System.check_line,
                    tint = colorfulCard.select(Color.White, iconColor)
                )
            }
        }
    }
}

@Composable
fun ruleIconColor(rule: RuleUM): Color = if (rule.useDynamicMonetIconColor) {
    MaterialTheme.colorScheme.primary
} else {
    Color(rule.iconColor)
}

@Composable
private fun listItemOverflowMenu(
    rule: RuleUM,
    isSubscribed: Boolean,
): DropdownSelectorState {
    val shortXProps = LocalShortXProps.current
    val handler = LocalRuleItemHandler.current
    val context = LocalContext.current

    val i18N = LocalI18N.current
    val badge = if (isSubscribed) {
        null
    } else i18N[I18N_R.UI_SUBSCRIPTION_SUBSCRIBED_ONLY]

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    val deleteDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = i18N[I18N_R.UI_COMMON_DELETE],
        state = deleteDialogState,
        data = rule,
        messageHint = { i18N[I18N_R.UI_DELETE_DIALOG_CONFIRM, mapOf("wht" to rule.title)] },
        onConfirm = { handler.delete(rule) }
    )

    val ruleSetSelectDialogState = rememberRuleSetSelectorState(onRuleSetSelected = {
        handler.moveToSet(rule, it.id)
    })
    RuleSetSelectorDialog(state = ruleSetSelectDialogState)

    val extractAsDAInputDialog =
        rememberTextInputState(title = I18N_R.UI_RULE_EXTRACT_AS_DA, message = null) {
            handler.extractAsDA(it, rule, shortXProps.firstOrNull())
            Toast.makeText(context, i18N[I18N_R.UI_COMMON_DONE], Toast.LENGTH_SHORT).show()
        }
    TextInputDialog(state = extractAsDAInputDialog)

    val menuState = rememberDropdownSelectorState()


    DropdownSelector(
        widthFraction = menuDropdownFraction,
        state = menuState,
        items = listOfNotNull(
            DropdownItem(
                labelLines = listOf(i18N[if (rule.isLocked) I18N_R.UI_COMMON_UNLOCK else I18N_R.UI_COMMON_LOCK]),
                data = RuleItemMenuOptions.ToggleLock,
                icon = {
                    RemixIcon(if (rule.isLocked) Remix.System.lock_unlock_line else Remix.System.lock_fill)
                }),
            DropdownItem(
                labelLines = listOf(i18N[if (rule.isPinned) I18N_R.UI_COMMON_UNPIN else I18N_R.UI_COMMON_PINNED]),
                data = RuleItemMenuOptions.TogglePin,
                icon = {
                    RemixIcon(if (rule.isPinned) Remix.Map.pushpin_line else Remix.Map.pushpin_fill)
                }),
            DropdownItem(
                labelLines = listOf(i18N[I18N_R.UI_COMMON_SELECT]),
                data = RuleItemMenuOptions.Select,
                icon = {
                    RemixIcon(Remix.System.checkbox_multiple_line)
                }),
            DropdownItem(
                labelLines = listOf(i18N[I18N_R.UI_COMMON_EXECUTE_ACTIONS]),
                data = RuleItemMenuOptions.Execute,
                icon = {
                    RemixIcon(Remix.Media.play_fill)
                }),
            if (!rule.isLocked) DropdownItem(
                labelLines = listOf(i18N[I18N_R.UI_COMMON_DUP]),
                data = RuleItemMenuOptions.Dup,
                badge = badge,
                icon = {
                    RemixIcon(Remix.Document.file_copy_2_fill)
                }) else null,
            if (!rule.isLocked) DropdownItem(
                labelLines = listOf(i18N[I18N_R.UI_COMMON_EDIT]),
                data = RuleItemMenuOptions.Edit,
                icon = {
                    RemixIcon(Remix.Design.edit_line)
                }) else null,
            DropdownItem(
                labelLines = listOf(i18N[I18N_R.UI_COMMON_SHARE]),
                data = RuleItemMenuOptions.Share,
                badge = badge,
                icon = {
                    RemixIcon(Remix.System.share_line)
                }),
            if (!rule.isLocked) DropdownItem(
                labelLines = listOf(i18N[I18N_R.UI_COMMON_MOVE_TO_SET]),
                data = RuleItemMenuOptions.MoveToSet,
                badge = badge,
                icon = {
                    RemixIcon(Remix.Document.folders_fill)
                }) else null,
            if (!rule.isLocked) DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_MOVE_OUT_SET]),
                data = RuleItemMenuOptions.MoveOutSet,
                icon = {
                    RemixIcon(Remix.Document.folder_reduce_fill)
                }) else null,
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_SETTINGS_EVA_RECORD]),
                data = RuleItemMenuOptions.ViewEvaRecord,
                icon = {
                    RemixIcon(Remix.System.history_fill)
                }),
            if (!rule.isLocked) DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_RULE_EXTRACT_AS_DA]),
                data = RuleItemMenuOptions.ExtractAsDA,
                badge = badge,
                icon = {
                    RemixIcon(Remix.Development.command_line)
                }) else null,
            if (!rule.isLocked) DropdownItem(
                labelLines = listOf(i18N[I18N_R.UI_COMMON_DELETE]),
                data = RuleItemMenuOptions.Delete,
                icon = {
                    RemixIcon(Remix.System.delete_bin_4_line)
                }) else null
        ), onSelect = {
            Logger("RuleMenu").d("onSelect: ${it.data}")
            when (it.data) {
                RuleItemMenuOptions.Dup -> {
                    if (isSubscribed) {
                        handler.dup(rule)
                    } else {
                        subscribeDialogState.show()
                    }
                }

                RuleItemMenuOptions.Delete -> {
                    deleteDialogState.show()
                }

                RuleItemMenuOptions.Edit -> {
                    handler.edit(rule)
                }

                RuleItemMenuOptions.Share -> {
                    if (isSubscribed) {
                        handler.share(rule)
                    } else {
                        subscribeDialogState.show()
                    }
                }

                RuleItemMenuOptions.TogglePin -> {
                    handler.togglePin(rule)
                }

                RuleItemMenuOptions.Execute -> {
                    handler.run(rule)
                }

                RuleItemMenuOptions.Select -> {
                    handler.select(rule)
                }

                RuleItemMenuOptions.MoveToSet -> {
                    ruleSetSelectDialogState.show()
                }

                RuleItemMenuOptions.MoveOutSet -> {
                    handler.moveOutSet(rule)
                }

                RuleItemMenuOptions.ViewEvaRecord -> {
                    EvaRecordActivity.start(context, rule.id)
                }

                RuleItemMenuOptions.ExtractAsDA -> {
                    if (isSubscribed) {
                        extractAsDAInputDialog.show(initialValue = rule.title)
                    } else {
                        subscribeDialogState.show()
                    }
                }

                RuleItemMenuOptions.ToggleLock -> {
                    handler.toggleLock(rule)
                }
            }
        })
    return menuState
}

enum class RuleItemMenuOptions {
    ToggleLock, TogglePin, Select, Dup, Delete, Edit, Share, Execute, MoveToSet, MoveOutSet, ViewEvaRecord, ExtractAsDA
}

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
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


@Composable
fun RuleItem(
    modifier: Modifier = Modifier,
    rule: RuleUM,
    isSubscribed: Boolean,
    colorfulCard: Boolean,
    showDescription: Boolean = true,
    runningInsCount: Int
) {
    val handler = LocalRuleItemHandler.current
    // TODO Animate item
    Box {
        val popMenuState = listItemOverflowMenu(
            rule = rule,
            isSubscribed = isSubscribed,
        )
        val clickAnimateState =
            rememberClickAnimateState(animateColor = Color(rule.iconColor))
        ClickAnimateContainer(clickAnimateState) {
            RuleItem(
                modifier = modifier.then(it),
                popMenuState = popMenuState,
                rule = rule,
                colorfulCard = colorfulCard,
                showDescription = showDescription,
                runningInsCount = runningInsCount
            )
        }
    }
}

@Composable
private fun RuleItem(
    modifier: Modifier,
    popMenuState: DropdownSelectorState,
    rule: RuleUM,
    colorfulCard: Boolean,
    showDescription: Boolean,
    runningInsCount: Int,
) {
    val handler = LocalRuleItemHandler.current
    Column(modifier = modifier
        .fillMaxWidth()
        .clip(ShortXCardRoundedCornerShape)
        .combinedClickable(onLongClick = {
            popMenuState.open()
        }) {
            handler.click(rule)
        }
        .padding(horizontal = 16.dp)
        .padding(top = 16.dp)
        .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RuleIcon(
                rule = rule,
                runningInsCount = runningInsCount,
                colorfulCard = colorfulCard,
                stopAllJobs = {
                    handler.stopAllJobs(rule)
                }) {
                popMenuState.open()
            }
            ShortXSwitch(
                modifier = Modifier,
                checked = rule.isEnabled,
                onCheckedChange = {
                    handler.setRuleEnabled(rule, it)
                },
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 6.dp)
        ) {
            Text(
                text = rule.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = if (showDescription) 14.5.sp else 13.5.sp
                ),
                color = colorfulCard.select(
                    themedTextColor(color = Color(rule.iconColor)),
                    Color.Unspecified
                )
            )
            androidx.compose.animation.AnimatedVisibility(showDescription) {
                Text(
                    modifier = Modifier.padding(top = 0.dp),
                    text = rule.description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = colorfulCard.select(
                        themedTextColor(color = Color(rule.iconColor)),
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
    colorfulCard: Boolean,
    stopAllJobs: () -> Unit,
    onClick: (() -> Unit)
) {
    val isRunning = runningInsCount > 0
    if (isRunning) {
        RunningIndicator(runningInsCount, stopAllJobs)
    } else {
        ColoredIcon(
            onClick = onClick,
            size = Dimens.RuleIconSize,
            padding = 0.dp,
            color = colorfulCard.select(
                t = {
                    Color(rule.iconColor)
                },
                f = {
                    Color(rule.iconColor).copy(alpha = if (rule.isEnabled) ActionColorBgAlphaLarge else ActionColorBgAlpha)
                })
        ) {
            RemixIcon(
                modifier = Modifier.size(16.dp),
                remixName = rule.icon,
                tint = colorfulCard.select(Color.White, Color(rule.iconColor))
            )
        }
    }
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
    } else i18N["ui.subscription.subscribed.only"]

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    val deleteDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = i18N["ui.common.delete"],
        state = deleteDialogState,
        data = rule,
        messageHint = { i18N["ui.delete.dialog.confirm", mapOf("wht" to rule.title)] },
        onConfirm = { handler.delete(rule) }
    )

    val ruleSetSelectDialogState = rememberRuleSetSelectorState(onRuleSetSelected = {
        handler.moveToSet(rule, it.id)
    })
    RuleSetSelectorDialog(state = ruleSetSelectDialogState)

    val extractAsDAInputDialog =
        rememberTextInputState(title = "ui.rule.extract.as.da", message = null) {
            handler.extractAsDA(it, rule, shortXProps.firstOrNull())
            Toast.makeText(context, i18N["ui.common.done"], Toast.LENGTH_SHORT).show()
        }
    TextInputDialog(state = extractAsDAInputDialog)

    val menuState = rememberDropdownSelectorState()


    DropdownSelector(
        widthFraction = menuDropdownFraction,
        state = menuState,
        items = listOf(
            DropdownItem(labelLines = listOf(i18N["ui.common.execute.actions"]),
                data = RuleItemMenuOptions.Execute,
                icon = {
                    RemixIcon(Remix.Media.play_fill)
                }),
            DropdownItem(labelLines = listOf(i18N["ui.common.dup"]),
                data = RuleItemMenuOptions.Dup,
                badge = badge,
                icon = {
                    RemixIcon(Remix.Document.file_copy_2_fill)
                }),
            DropdownItem(labelLines = listOf(i18N["ui.common.edit"]),
                data = RuleItemMenuOptions.Edit,
                icon = {
                    RemixIcon(Remix.Design.edit_line)
                }),
            DropdownItem(
                labelLines = listOf(i18N["ui.common.share"]),
                data = RuleItemMenuOptions.Share,
                badge = badge,
                icon = {
                    RemixIcon(Remix.System.share_line)
                }),
            DropdownItem(
                labelLines = listOf(i18N["ui.common.move.to.set"]),
                data = RuleItemMenuOptions.MoveToSet,
                badge = badge,
                icon = {
                    RemixIcon(Remix.Document.folders_fill)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current["ui.common.move.out.set"]),
                data = RuleItemMenuOptions.MoveOutSet,
                icon = {
                    RemixIcon(Remix.Document.folder_reduce_fill)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current["ui.settings.eva.record"]),
                data = RuleItemMenuOptions.ViewEvaRecord,
                icon = {
                    RemixIcon(Remix.System.history_fill)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current["ui.rule.extract.as.da"]),
                data = RuleItemMenuOptions.ExtractAsDA,
                badge = badge,
                icon = {
                    RemixIcon(Remix.Development.command_line)
                }),
            DropdownItem(
                labelLines = listOf(i18N["ui.common.delete"]),
                data = RuleItemMenuOptions.Delete,
                icon = {
                    RemixIcon(Remix.System.delete_bin_4_line)
                })
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

                RuleItemMenuOptions.Execute -> {
                    handler.run(rule)
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
            }
        })
    return menuState
}

enum class RuleItemMenuOptions {
    Dup, Delete, Edit, Share, Execute, MoveToSet, MoveOutSet, ViewEvaRecord, ExtractAsDA
}
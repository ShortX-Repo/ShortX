@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main.rule

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.main.da.RunningIndicator
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog


@Composable
fun RuleItem2(
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
            }
            .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RuleIcon(rule)
                ShortXSwitch(
                    modifier = Modifier,
                    checked = rule.isEnabled,
                    onCheckedChange = {
                        setRuleEnabled(rule.id, it)
                    },
                )
            }

            Column(
                modifier = Modifier
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
        }
    }
}

@Composable
fun RuleIcon(rule: RuleUM) {
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
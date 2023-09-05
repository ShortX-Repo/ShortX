@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main.rule

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog


@Composable
fun RuleSetItem(
    rule: RuleUM,
    isSubscribed: Boolean
) {
    val handler = LocalRuleSetItemHandler.current
    Box {
        val popMenuState = listItemOverflowMenu(
            rule = rule,
            isSubscribed = isSubscribed,
        )
        Column(modifier = Modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .combinedClickable(onLongClick = {
                popMenuState.open()
            }) {
                handler.click(rule)
            }
            .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ColoredIcon(
                    size = 38.dp,
                    padding = 0.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    RemixIcon(
                        modifier = Modifier.size(16.dp),
                        remixName = Remix.Document.folders_fill,
                    )
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
            ) {
                Text(
                    text = rule.title, style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun listItemOverflowMenu(
    rule: RuleUM,
    isSubscribed: Boolean,
): DropdownSelectorState {
    val handler = LocalRuleItemHandler.current

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
        onConfirm = { handler.delete(rule) }
    )

    val menuState = rememberDropdownSelectorState()

    DropdownSelector(
        widthFraction = 0.3f,
        state = menuState,
        items = listOf(
            DropdownItem(labelLines = listOf(i18N["ui.common.edit"]),
                data = RuleSetItemMenuOptions.Edit,
                icon = {
                    RemixIcon(Remix.Design.edit_line)
                }),
            DropdownItem(
                labelLines = listOf(i18N["ui.common.share"]),
                data = RuleSetItemMenuOptions.Share,
                badge = badge,
                icon = {
                    RemixIcon(Remix.System.share_line)
                }),
            DropdownItem(
                labelLines = listOf(i18N["ui.common.delete"]),
                data = RuleSetItemMenuOptions.Delete,
                icon = {
                    RemixIcon(Remix.System.delete_bin_4_line)
                })
        ), onSelect = {
            Logger("RuleMenu").d("onSelect: ${it.data}")
            when (it.data) {

                RuleSetItemMenuOptions.Delete -> {
                    deleteDialogState.show()
                }

                RuleSetItemMenuOptions.Edit -> {
                    handler.edit(rule)
                }

                RuleSetItemMenuOptions.Share -> {
                    if (isSubscribed) {
                        handler.share(rule)
                    } else {
                        subscribeDialogState.show()
                    }
                }
            }
        })
    return menuState
}

enum class RuleSetItemMenuOptions {
    Delete, Edit, Share
}
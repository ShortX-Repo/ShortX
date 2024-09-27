@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main.toggle

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.ClickAnimateContainer
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.LoadingAnimationDot
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberClickAnimateState
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.main.model.ToggleEnableState
import tornaco.apps.shortx.ui.main.model.ToggleUM
import tornaco.apps.shortx.ui.record.EvaRecordActivity
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog
import tornaco.apps.shortx.ui.theme.Dimens
import tornaco.apps.shortx.ui.theme.Dimens.menuDropdownFraction


@Composable
fun ToggleItem(
    modifier: Modifier = Modifier,
    toggle: ToggleUM,
    isSubscribed: Boolean
) {
    Box {
        val popMenuState = listItemOverflowMenu(
            toggle = toggle,
            isSubscribed = isSubscribed,
        )
        val clickAnimateState =
            rememberClickAnimateState(animateColor = MaterialTheme.colorScheme.primary)
        ClickAnimateContainer(clickAnimateState) {
            ToggleItem(modifier.then(it), popMenuState, toggle)
        }
    }
}

@Composable
private fun ToggleItem(
    modifier: Modifier,
    popMenuState: DropdownSelectorState,
    toggle: ToggleUM
) {
    val handler = LocalToggleItemHandler.current
    Column(modifier = modifier
        .fillMaxWidth()
        .clip(ShortXCardRoundedCornerShape)
        .combinedClickable(onLongClick = {
            popMenuState.open()
        }) {
            handler.click(toggle)
        }
        .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ToggleIcon(toggle) {
                popMenuState.open()
            }

            when (val enableState = toggle.enableState) {
                is ToggleEnableState.Loaded -> {
                    ShortXSwitch(
                        modifier = Modifier,
                        checked = enableState.isEnabled,
                        onCheckedChange = {
                            handler.setToggleEnabled(toggle, it)
                        },
                    )
                }

                ToggleEnableState.Loading -> {
                    LoadingAnimationDot()
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(top = 6.dp)
        ) {
            Text(
                text = toggle.title, style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp
                )
            )
            TinySpacer()
            Text(
                text = toggle.description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ToggleIcon(toggle: ToggleUM, onClick: (() -> Unit)) {
    ColoredIcon(
        onClick = onClick,
        size = Dimens.RuleIconSize,
        padding = 0.dp,
    ) {
        RemixIcon(
            modifier = Modifier.size(16.dp),
            remixName = Remix.Design.anticlockwise_fill,
        )
    }
}

@Composable
private fun listItemOverflowMenu(
    toggle: ToggleUM,
    isSubscribed: Boolean,
): DropdownSelectorState {
    val handler = LocalToggleItemHandler.current

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
        data = toggle,
        messageHint = { i18N["ui.delete.dialog.confirm", mapOf("wht" to toggle.title)] },
        onConfirm = { handler.delete(toggle) }
    )

    val menuState = rememberDropdownSelectorState()

    val context = LocalContext.current

    DropdownSelector(
        widthFraction = menuDropdownFraction,
        state = menuState,
        items = listOf(
            DropdownItem(labelLines = listOf(i18N["ui.common.dup"]),
                data = ToggleItemMenuOptions.Dup,
                badge = badge,
                icon = {
                    RemixIcon(Remix.Document.file_copy_2_fill)
                }),
            DropdownItem(labelLines = listOf(i18N["ui.common.edit"]),
                data = ToggleItemMenuOptions.Edit,
                icon = {
                    RemixIcon(Remix.Design.edit_line)
                }),
            DropdownItem(
                labelLines = listOf(i18N["ui.common.share"]),
                data = ToggleItemMenuOptions.Share,
                badge = badge,
                icon = {
                    RemixIcon(Remix.System.share_line)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current["ui.settings.eva.record"]),
                data = ToggleItemMenuOptions.ViewEvaRecord,
                icon = {
                    RemixIcon(Remix.System.history_fill)
                }),
            DropdownItem(
                labelLines = listOf(i18N["ui.common.delete"]),
                data = ToggleItemMenuOptions.Delete,
                icon = {
                    RemixIcon(Remix.System.delete_bin_4_line)
                })
        ), onSelect = {
            Logger("ToggleMenu").d("onSelect: ${it.data}")
            when (it.data) {
                ToggleItemMenuOptions.Dup -> {
                    if (isSubscribed) {
                        handler.dup(toggle)
                    } else {
                        subscribeDialogState.show()
                    }
                }

                ToggleItemMenuOptions.Delete -> {
                    deleteDialogState.show()
                }

                ToggleItemMenuOptions.Edit -> {
                    handler.edit(toggle)
                }

                ToggleItemMenuOptions.Share -> {
                    if (isSubscribed) {
                        handler.share(toggle)
                    } else {
                        subscribeDialogState.show()
                    }
                }

                ToggleItemMenuOptions.Execute -> {
                    handler.run(toggle)
                }

                ToggleItemMenuOptions.MoveToSet -> {
                }

                ToggleItemMenuOptions.MoveOutSet -> {
                    handler.moveOutSet(toggle)
                }

                ToggleItemMenuOptions.ViewEvaRecord -> {
                    EvaRecordActivity.start(context, toggle.id)
                }
            }
        })
    return menuState
}

enum class ToggleItemMenuOptions {
    Dup, Delete, Edit, Share, Execute, MoveToSet, MoveOutSet, ViewEvaRecord
}
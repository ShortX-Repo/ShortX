@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main.da

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.main.DeleteSetConfirmDialog
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.main.rememberDeleteSetConfirmDialogState
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog
import tornaco.apps.shortx.ui.theme.Dimens


@Composable
fun DirectActionSetItem(
    modifier: Modifier,
    da: DirectActionUM,
    isSubscribed: Boolean
) {
    Box {
        val popMenuState = daSetListItemOverflowMenu(
            da = da,
            isSubscribed = isSubscribed
        )

        val handler = LocalDASetItemHandler.current
        DASetListItem(
            modifier = modifier,
            title = da.title,
            onLongClick = { popMenuState.open() },
            onClick = {
                handler.click(da)
            }
        )
    }
}

@Composable
private fun DASetListItem(
    modifier: Modifier,
    title: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .clip(ShortXCardRoundedCornerShape)
        .combinedClickable(onLongClick = onLongClick) {
            onClick()
        }
        .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColoredIcon(
                size = Dimens.DAIconSize,
                padding = 0.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    onLongClick()
                }
            ) {
                RemixIcon(
                    modifier = Modifier.size(16.dp),
                    remixName = Remix.Document.folders_fill,
                )
            }

            MediumSpacer()
            Text(
                text = title, style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp
                )
            )
        }
    }
}


@Composable
fun DASetListItemCompat(
    modifier: Modifier,
    da: DirectActionUM,
    isSubscribed: Boolean,
) {
    Box {
        val popMenuState = daSetListItemOverflowMenu(
            da = da,
            isSubscribed = isSubscribed
        )

        val handler = LocalDASetItemHandler.current
        DAListItemCompat(
            modifier = modifier,
            title = da.title,
            icon = Remix.Document.folder_2_fill,
            iconColor = MaterialTheme.colorScheme.primary,
            onLongClick = { popMenuState.open() },
            onClick = {
                handler.click(da)
            }
        )
    }
}

@Composable
private fun daSetListItemOverflowMenu(
    da: DirectActionUM,
    isSubscribed: Boolean
): DropdownSelectorState {
    val handler = LocalDASetItemHandler.current
    val i18N = LocalI18N.current

    val badge = if (isSubscribed) {
        null
    } else i18N["ui.subscription.subscribed.only"]

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    val dismissDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = i18N["ui.common.dismiss"],
        state = dismissDialogState,
        data = da,
        messageHint = {
            i18N["ui.dismiss.dialog.confirm", mapOf("wht" to da.title)]
        },
        onConfirm = { handler.dismiss(da) }
    )

    val deleteConfirmDialogState = rememberDeleteSetConfirmDialogState(
        setTitle = da.title,
        itemsCount = da.itemsCount
    )
    DeleteSetConfirmDialog(
        state = deleteConfirmDialogState,
        onConfirm = { handler.delete(da, it) }
    )

    val renameDialogState =
        rememberTextInputState(title = i18N["ui.common.rename"], message = null, onSelected = {
            handler.rename(da, it)
        })
    TextInputDialog(state = renameDialogState)


    val menuState = rememberDropdownSelectorState()

    DropdownSelector(widthFraction = 0.33f, state = menuState, items = listOf(
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.rename"]),
            data = RuleSetItemMenuOptions.Rename,
            icon = {
                RemixIcon(Remix.Design.edit_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.share"]),
            data = RuleSetItemMenuOptions.Share,
            badge = badge,
            icon = {
                RemixIcon(Remix.System.share_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.dismiss"]),
            data = RuleSetItemMenuOptions.Dismiss,
            icon = {
                RemixIcon(Remix.System.close_circle_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.delete"]),
            data = RuleSetItemMenuOptions.Delete,
            icon = {
                RemixIcon(Remix.System.delete_bin_4_line)
            })
    ), onSelect = {
        when (it.data) {
            RuleSetItemMenuOptions.Dismiss -> {
                dismissDialogState.show()
            }

            RuleSetItemMenuOptions.Delete -> {
                deleteConfirmDialogState.show()
            }

            RuleSetItemMenuOptions.Rename -> {
                renameDialogState.show(da.title)
            }

            RuleSetItemMenuOptions.Share -> {
                if (isSubscribed) {
                    handler.share(da)
                } else {
                    subscribeDialogState.show()
                }
            }
        }
    })

    return menuState
}

enum class RuleSetItemMenuOptions {
    Dismiss, Delete, Rename, Share
}
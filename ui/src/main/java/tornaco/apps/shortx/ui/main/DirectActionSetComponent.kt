@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalSnackbarHostState
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog


@Composable
fun DirectActionSetItem(
    modifier: Modifier,
    da: DirectActionUM,
    isSubscribed: Boolean,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
    onShare: (DirectActionUM) -> Unit,
) {
    Box {
        val popMenuState = daSetListItemOverflowMenu(
            da = da,
            isSubscribed = isSubscribed,
            onDelete = onDelete,
            onEdit = onEdit,
            onShare = onShare
        )
        DASetListItem(
            modifier = modifier,
            title = da.title,
            description = da.description,
            updateTime = da.updateTime,
            icon = da.icon,
            iconColor = Color(da.iconColor),
            onLongClick = { popMenuState.open() },
            onClick = {

            }
        )
    }
}

@Composable
fun DASetListItem(
    modifier: Modifier,
    title: String,
    description: String,
    updateTime: String,
    icon: String,
    iconColor: Color,
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
                size = 32.dp,
                padding = 0.dp,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                RemixIcon(
                    modifier = Modifier.size(16.dp),
                    remixName = Remix.Document.folders_fill,
                    tint = iconColor
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
private fun daSetListItemOverflowMenu(
    da: DirectActionUM,
    isSubscribed: Boolean,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
    onShare: (DirectActionUM) -> Unit,
): DropdownSelectorState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = LocalSnackbarHostState.current
    val i18N = LocalI18N.current

    val badge = if (isSubscribed) {
        null
    } else i18N["ui.subscription.subscribed.only"]

    val subscribeDialogState = rememberCommonDialogState()
    FeatureAccessDialog(subscribeDialogState)

    val deleteDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = LocalI18N.current["ui.common.delete"],
        state = deleteDialogState,
        data = da,
        messageHint = {
            i18N["ui.delete.dialog.confirm", mapOf("wht" to da.title)]
        },
        onConfirm = onDelete
    )

    val menuState = rememberDropdownSelectorState()

    DropdownSelector(widthFraction = 0.33f, state = menuState, items = listOf(
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.edit"]),
            data = "edit",
            icon = {
                RemixIcon(Remix.Design.edit_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.share"]),
            data = "share",
            badge = badge,
            icon = {
                RemixIcon(Remix.System.share_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.delete"]),
            data = "delete",
            icon = {
                RemixIcon(Remix.System.delete_bin_4_line)
            })
    ), onSelect = {
        when (it.data) {
            "delete" -> {
                deleteDialogState.show()
            }

            "edit" -> {
                onEdit(da)
            }

            "share" -> {
                if (isSubscribed) {
                    onShare(da)
                } else {
                    subscribeDialogState.show()
                }
            }

            else -> {}
        }
    })

    return menuState
}
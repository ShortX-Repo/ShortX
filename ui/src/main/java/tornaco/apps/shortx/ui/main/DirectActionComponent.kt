@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
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
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberCommonDialogState
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.shortcut.ActionShortcutActivity
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog
import tornaco.apps.shortx.ui.theme.NoteTextColor
import tornaco.apps.shortx.ui.theme.VipColorGold


@Composable
fun DirectActionItem(
    modifier: Modifier,
    da: DirectActionUM,
    isSubscribed: Boolean,
    isLongClickEnabled: Boolean,
    run: () -> Unit,
    onDup: (DirectActionUM) -> Unit,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
    onDeepLink: (DirectActionUM) -> Unit,
    onShare: (DirectActionUM) -> Unit,
    stopAllJobs: (DirectActionUM) -> Unit
) {
    Box {
        val popMenuState = daListItemOverflowMenu(
            da = da,
            isSubscribed = isSubscribed,
            onDup = onDup,
            onDelete = onDelete,
            onEdit = onEdit,
            onDeepLink = onDeepLink,
            onShare = onShare
        )
        DAListItem(
            modifier = modifier,
            title = da.title,
            description = da.description,
            updateTime = da.updateTime,
            icon = da.icon,
            iconColor = Color(da.iconColor),
            runningInsCount = da.runningInsCount,
            edit = { onEdit(da) },
            run = run,
            onLongClick = { if (isLongClickEnabled) popMenuState.open() },
            stopAllJobs = {
                stopAllJobs(da)
            }
        )
    }
}

@Composable
fun DirectActionItemCompat(
    modifier: Modifier,
    da: DirectActionUM,
    isSubscribed: Boolean,
    isLongClickEnabled: Boolean,
    onClick: () -> Unit,
    onDup: (DirectActionUM) -> Unit,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
    onDeepLink: (DirectActionUM) -> Unit,
    onShare: (DirectActionUM) -> Unit,
    stopAllJobs: (DirectActionUM) -> Unit
) {
    Box {
        val popMenuState = daListItemOverflowMenu(
            da = da,
            isSubscribed = isSubscribed,
            onDup = onDup,
            onDelete = onDelete,
            onEdit = onEdit,
            onDeepLink = onDeepLink,
            onShare = onShare
        )
        DAListItemCompat(
            modifier = modifier,
            title = da.title,
            icon = da.icon,
            iconColor = Color(da.iconColor),
            onClick = onClick,
            onLongClick = { if (isLongClickEnabled) popMenuState.open() },
        )
    }
}

@Composable
private fun DAListItemCompat(
    modifier: Modifier,
    title: String,
    icon: String,
    iconColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .combinedClickable(onLongClick = onLongClick) {
                onClick()
            }
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ColoredIcon(
            size = 48.dp,
            padding = 0.dp,
            color = iconColor
        ) {
            RemixIcon(
                modifier = Modifier.size(24.dp),
                remixName = icon,
                tint = Color.White
            )
        }

        TinySpacer()

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 12.sp
            ),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}


@Composable
private fun DAListItem(
    modifier: Modifier,
    title: String,
    description: String,
    updateTime: String,
    runningInsCount: Int = 0,
    icon: String,
    iconColor: Color,
    edit: () -> Unit,
    run: () -> Unit,
    onLongClick: () -> Unit,
    stopAllJobs: () -> Unit
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .clip(ShortXCardRoundedCornerShape)
        .combinedClickable(onLongClick = onLongClick) {
            edit()
        }
        .padding(horizontal = 12.dp)
        .padding(top = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColoredIcon(
                size = 32.dp,
                padding = 0.dp,
                color = iconColor.copy(alpha = ActionColorBgAlpha)
            ) {
                RemixIcon(
                    modifier = Modifier.size(16.dp),
                    remixName = icon,
                    tint = iconColor
                )
            }

            IconButton(onClick = {
                run()
            }) {
                RemixIcon(remixName = Remix.Media.play_circle_line, tint = NoteTextColor)
            }
        }

        MediumSpacer()

        Column(modifier = Modifier) {
            Text(
                text = title, style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp
                )
            )
            SmallSpacer()
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            MediumSpacer()
            TinySpacer()
        }

        AnimatedVisibility(visible = runningInsCount > 0) {
            RunningIndicator(
                modifier = Modifier,
                runningInsCount = runningInsCount,
                stopAllJobs = stopAllJobs
            )
        }
    }
}

@Composable
fun RunningIndicator(modifier: Modifier = Modifier, runningInsCount: Int, stopAllJobs: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(12.dp),
                strokeWidth = 1.dp
            )
            SmallSpacer()
            Text(
                text = LocalI18N.current["ui.title.jobs.count", mapOf("count" to "$runningInsCount")],
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                maxLines = 1
            )
        }

        IconButton(onClick = {
            stopAllJobs()
        }) {
            RemixIcon(
                remixName = Remix.Media.stop_circle_line,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun daListItemOverflowMenu(
    da: DirectActionUM,
    isSubscribed: Boolean,
    onDup: (DirectActionUM) -> Unit,
    onDelete: (DirectActionUM) -> Unit,
    onEdit: (DirectActionUM) -> Unit,
    onDeepLink: (DirectActionUM) -> Unit,
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
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.create.shortcut"]),
            data = "shortcut",
            badge = badge,
            icon = {
                RemixIcon(Remix.System.external_link_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.dup"]),
            data = "dup",
            badge = badge,
            icon = {
                RemixIcon(Remix.Document.file_copy_2_fill)
            }),
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
        DropdownItem(labelLines = listOf("Deeplink"),
            data = "deeplink",
            icon = {
                RemixIcon(Remix.System.external_link_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.delete"]),
            data = "delete",
            icon = {
                RemixIcon(Remix.System.delete_bin_4_line)
            })
    ), onSelect = {
        when (it.data) {
            "shortcut" -> {
                if (isSubscribed) {
                    ActionShortcutActivity.addShortcut(context, da)
                    scope.launch {
                        val res = snackbar.showSnackbar(
                            i18N["ui.common.create.shortcut.snackbar.message"],
                            i18N["ui.common.settings"],
                            true,
                        )
                        if (res == SnackbarResult.ActionPerformed) {
                            kotlin.runCatching {
                                PkgUtils.launchPkgSettings(
                                    context,
                                    BuildProp.APP_PACKAGE_NAME
                                )
                            }
                        }
                    }
                } else {
                    subscribeDialogState.show()
                }
            }

            "dup" -> {
                if (isSubscribed) {
                    onDup(da)
                } else {
                    subscribeDialogState.show()
                }
            }

            "delete" -> {
                deleteDialogState.show()
            }

            "edit" -> {
                onEdit(da)
            }

            "deeplink" -> {
                onDeepLink(da)
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

@Composable
@Preview
private fun DAListItemPreview() {
    CompositionLocalProvider(LocalI18N provides object : I18N {
        override fun get(key: String, args: Map<String, String>): String {
            return "2 Jobs"
        }
    }) {
        DAListItem(
            modifier = Modifier,
            title = "Start QQ",
            description = "This is description",
            updateTime = "2022-12-22",
            runningInsCount = 2,
            icon = Remix.Media.broadcast_line,
            iconColor = VipColorGold,
            edit = {},
            run = {},
            onLongClick = {},
            stopAllJobs = {}
        )
    }
}
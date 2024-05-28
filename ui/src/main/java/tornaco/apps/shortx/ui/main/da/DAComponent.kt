@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.main.da

import android.widget.Toast
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
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlphaLarge
import tornaco.apps.shortx.ui.base.ClickAnimateContainer
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
import tornaco.apps.shortx.ui.base.rememberClickAnimateState
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.record.EvaRecordActivity
import tornaco.apps.shortx.ui.shortcut.ActionShortcutActivity
import tornaco.apps.shortx.ui.subscribe.FeatureAccessDialog
import tornaco.apps.shortx.ui.theme.Dimens
import tornaco.apps.shortx.ui.theme.Dimens.menuDropdownFraction
import tornaco.apps.shortx.ui.theme.NoteTextColor
import tornaco.apps.shortx.ui.theme.VipColorGold


@Composable
fun DirectActionItem(
    modifier: Modifier,
    da: DirectActionUM,
    isSubscribed: Boolean,
    isLongClickEnabled: Boolean
) {
    Box {
        val popMenuState = daListItemOverflowMenu(
            da = da,
            isSubscribed = isSubscribed
        )

        val clickAnimateState = rememberClickAnimateState(animateColor = Color(da.iconColor))
        ClickAnimateContainer(clickAnimateState) {
            val handler = LocalDAItemHandler.current
            DAListItem(
                modifier = modifier.then(it),
                title = da.title,
                description = da.description,
                updateTime = da.updateTime,
                icon = da.icon,
                iconColor = Color(da.iconColor),
                runningInsCount = da.runningInsCount,
                hasFuncParameter = da.hasAnyParameter,
                onClick = { handler.click(da) },
                run = {
                    clickAnimateState.startAllAnim()
                    handler.run(da)
                },
                onLongClick = { if (isLongClickEnabled) popMenuState.open() },
                stopAllJobs = {
                    handler.stopAllJobs(da)
                }
            )
        }
    }
}

@Composable
fun DirectActionItemCompat(
    modifier: Modifier,
    da: DirectActionUM,
    isSubscribed: Boolean,
    isLongClickEnabled: Boolean,
) {
    Box {
        val popMenuState = daListItemOverflowMenu(
            da = da,
            isSubscribed = isSubscribed
        )
        val handler = LocalDAItemHandler.current
        DAListItemCompat(
            modifier = modifier,
            title = da.title,
            icon = da.icon,
            iconColor = Color(da.iconColor),
            onClick = { handler.run(da) },
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
    hasFuncParameter: Boolean,
    onClick: () -> Unit,
    run: () -> Unit,
    onLongClick: () -> Unit,
    stopAllJobs: () -> Unit
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .clip(ShortXCardRoundedCornerShape)
        .combinedClickable(onLongClick = onLongClick) {
            onClick()
        }
        .padding(horizontal = 12.dp)
        .padding(top = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                ColoredIcon(
                    modifier = Modifier.align(Alignment.Center),
                    size = Dimens.DAIconSize,
                    padding = 0.dp,
                    color = iconColor.copy(alpha = ActionColorBgAlpha),
                    onClick = {
                        onLongClick()
                    }
                ) {
                    RemixIcon(
                        modifier = Modifier.size(16.dp),
                        remixName = icon,
                        tint = iconColor
                    )
                }

                if (hasFuncParameter) {
                    ColoredIcon(
                        modifier = Modifier.align(Alignment.TopEnd),
                        size = 12.dp,
                        padding = 0.dp,
                        color = iconColor.copy(alpha = ActionColorBgAlphaLarge),
                        onClick = {
                            onLongClick()
                        }
                    ) {
                        RemixIcon(
                            modifier = Modifier.size(8.dp),
                            remixName = Remix.Editor.functions,
                            tint = iconColor
                        )
                    }
                }
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
    isSubscribed: Boolean
): DropdownSelectorState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = LocalSnackbarHostState.current
    val i18N = LocalI18N.current
    val handler = LocalDAItemHandler.current

    val badge = if (isSubscribed) {
        null
    } else i18N["ui.subscription.subscribed.only"]

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    val deleteDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = LocalI18N.current["ui.common.delete"],
        state = deleteDialogState,
        data = da,
        messageHint = {
            i18N["ui.delete.dialog.confirm", mapOf("wht" to da.title)]
        },
        onConfirm = { handler.delete(da) }
    )

    val daSetSelectDialogState = rememberDASetSelectorState(onDASetSelected = {
        handler.moveToSet(da, it.id)
    })
    DASetSelectorDialog(state = daSetSelectDialogState)

    val createApkDialog = rememberConfirmDialogState()
    ConfirmDialog(
        title = i18N["ui.common.install.as.apk"],
        state = createApkDialog,
        data = da,
        messageHint = {
            i18N["ui.common.install.as.apk.description"]
        },
    ) {
        handler.createApk(it)
    }

    val menuState = rememberDropdownSelectorState()

    DropdownSelector(widthFraction = menuDropdownFraction, state = menuState, items = listOfNotNull(
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.create.shortcut"]),
            data = DAItemMenuOptions.Shortcut,
            badge = badge,
            icon = {
                RemixIcon(Remix.System.external_link_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.create.dynamic.shortcut"]),
            data = DAItemMenuOptions.DynamicShortcut,
            badge = badge,
            icon = {
                RemixIcon(Remix.System.external_link_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.remove.dynamic.shortcut"]),
            data = DAItemMenuOptions.RemoveDynamicShortcut,
            badge = badge,
            icon = {
                RemixIcon(Remix.System.delete_bin_5_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.install.as.apk"]),
            data = DAItemMenuOptions.CreateApk,
            badge = badge,
            icon = {
                RemixIcon(Remix.Logos.android_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.dup"]),
            data = DAItemMenuOptions.Dup,
            badge = badge,
            icon = {
                RemixIcon(Remix.Document.file_copy_2_fill)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.edit"]),
            data = DAItemMenuOptions.Edit,
            icon = {
                RemixIcon(Remix.Design.edit_line)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.share"]),
            data = DAItemMenuOptions.Share,
            badge = badge,
            icon = {
                RemixIcon(Remix.System.share_line)
            }),
        DropdownItem(labelLines = listOf("Deeplink"),
            data = DAItemMenuOptions.DeepLink,
            icon = {
                RemixIcon(Remix.System.external_link_line)
            }),
        DropdownItem(labelLines = listOf("Shell cmd"),
            data = DAItemMenuOptions.ShellCommand,
            icon = {
                RemixIcon(Remix.Editor.hashtag)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.move.to.set"]),
            data = DAItemMenuOptions.MoveToSet,
            badge = badge,
            icon = {
                RemixIcon(Remix.Document.folders_fill)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.move.out.set"]),
            data = DAItemMenuOptions.MoveOutSet,
            icon = {
                RemixIcon(Remix.Document.folder_reduce_fill)
            }),
        DropdownItem(
            labelLines = listOf(LocalI18N.current["ui.settings.eva.record"]),
            data = DAItemMenuOptions.ViewEvaRecord,
            icon = {
                RemixIcon(Remix.System.history_fill)
            }),
        DropdownItem(labelLines = listOf(LocalI18N.current["ui.common.delete"]),
            data = DAItemMenuOptions.Delete,
            icon = {
                RemixIcon(Remix.System.delete_bin_4_line)
            })
    ), onSelect = {
        when (it.data) {
            DAItemMenuOptions.Shortcut -> {
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
                                    BuildPropInternal.APP_PACKAGE_NAME
                                )
                            }
                        }
                    }
                } else {
                    subscribeDialogState.show()
                }
            }

            DAItemMenuOptions.Dup -> {
                if (isSubscribed) {
                    handler.dup(da)
                } else {
                    subscribeDialogState.show()
                }
            }

            DAItemMenuOptions.Delete -> {
                deleteDialogState.show()
            }

            DAItemMenuOptions.Edit -> {
                handler.edit(da)
            }

            DAItemMenuOptions.DeepLink -> {
                handler.deepLink(da)
            }

            DAItemMenuOptions.ShellCommand -> {
                handler.shellCommand(da)
            }

            DAItemMenuOptions.Share -> {
                if (isSubscribed) {
                    handler.share(da)
                } else {
                    subscribeDialogState.show()
                }
            }

            DAItemMenuOptions.MoveToSet -> {
                if (isSubscribed) {
                    daSetSelectDialogState.show()
                } else {
                    subscribeDialogState.show()
                }
            }

            DAItemMenuOptions.MoveOutSet -> {
                handler.moveOutSet(da)
            }

            DAItemMenuOptions.ViewEvaRecord -> {
                EvaRecordActivity.start(context, da.id)
            }

            DAItemMenuOptions.CreateApk -> {
                if (isSubscribed) {
                    createApkDialog.show()
                } else {
                    subscribeDialogState.show()
                }
            }

            DAItemMenuOptions.DynamicShortcut -> {
                if (isSubscribed) {
                    if (ActionShortcutActivity.addDynamicShortcut(context, da)) {
                        Toast.makeText(context, i18N["ui.common.done"], Toast.LENGTH_SHORT).show()
                    }
                } else {
                    subscribeDialogState.show()
                }
            }

            DAItemMenuOptions.RemoveDynamicShortcut -> {
                ActionShortcutActivity.removeDynamicShortcut(context, da)
            }
        }
    })

    return menuState
}


enum class DAItemMenuOptions {
    Dup, DeepLink, ShellCommand, Delete, Edit, Share, Shortcut, DynamicShortcut, RemoveDynamicShortcut, CreateApk, MoveToSet, MoveOutSet, ViewEvaRecord
}

@Composable
@Preview
private fun DAListItemPreview() {
    CompositionLocalProvider(LocalI18N provides object : I18N {
        override fun get(key: String, args: Map<String, String>, fallback: String?): String {
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
            hasFuncParameter = true,
            onClick = {},
            run = {},
            onLongClick = {},
            stopAllJobs = {}
        )
    }
}
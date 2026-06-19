@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)

package tornaco.apps.shortx.ui.main.da

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.res.isBase64Image
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.base.AnimatedTextContainer
import tornaco.apps.shortx.ui.base.ClickAnimateContainer
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.DropdownSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalSnackbarHostState
import tornaco.apps.shortx.ui.base.LocalUiSettings
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.longPressZoom
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
import tornaco.apps.shortx.ui.util.darkenColor
import tornaco.apps.shortx.ui.util.themedTextColor
import tornaco.apps.shortx.core.i18n.I18N_R

private const val COMPAT_MODE_ICON_SIZE = 48

@Composable
fun DirectActionItem(
    modifier: Modifier,
    da: DirectActionUM,
    isSubscribed: Boolean,
    isLongClickEnabled: Boolean,
    colorfulCard: Boolean,
    runningInsCount: Int,
    isSelectionMode: Boolean = false,
    selected: Boolean = false,
    onSelectionChange: (Boolean) -> Unit = {}
) {
    val iconColor = daIconColor(da)
    Box {
        val popMenuState = daListItemOverflowMenu(
            da = da,
            isSubscribed = isSubscribed
        )

        val clickAnimateState = rememberClickAnimateState(animateColor = iconColor)

        ClickAnimateContainer(clickAnimateState) {
            val handler = LocalDAItemHandler.current
            DAListItem(
                modifier = modifier.then(it),
                title = da.title,
                description = da.description,
                updateTime = da.updateTime,
                icon = da.icon,
                iconColor = iconColor,
                runningInsCount = runningInsCount,
                hasFuncParameter = da.hasAnyParameter,
                colorfulCard = colorfulCard,
                isPinned = da.isPinned,
                onClick = {
                    if (isSelectionMode) {
                        onSelectionChange(!selected)
                    } else {
                        handler.click(da)
                    }
                },
                run = {
                    if (isSelectionMode) {
                        onSelectionChange(!selected)
                    } else {
                        clickAnimateState.startAllAnim()
                        handler.run(da)
                    }
                },
                onLongClick = {
                    if (isSelectionMode) {
                        onSelectionChange(!selected)
                    } else if (isLongClickEnabled) {
                        popMenuState.open()
                    }
                },
                stopAllJobs = {
                    handler.stopAllJobs(da)
                },
                isSelectionMode = isSelectionMode,
                selected = selected
            )
        }
    }
}

@Composable
fun DAListItemCompat(
    modifier: Modifier,
    da: DirectActionUM,
    isSubscribed: Boolean,
    isLongClickEnabled: Boolean,
    isSelectionMode: Boolean = false,
    selected: Boolean = false,
    onSelectionChange: (Boolean) -> Unit = {}
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
            iconColor = daIconColor(da),
            selected = selected,
            isSelectionMode = isSelectionMode,
            onClick = {
                if (isSelectionMode) {
                    onSelectionChange(!selected)
                } else {
                    handler.run(da)
                }
            },
            onLongClick = {
                if (isSelectionMode) {
                    onSelectionChange(!selected)
                } else if (isLongClickEnabled) {
                    popMenuState.open()
                }
            },
        )
    }
}

@Composable
fun daIconColor(da: DirectActionUM): Color = if (da.useDynamicMonetIconColor) {
    MaterialTheme.colorScheme.primary
} else {
    Color(da.iconColor)
}

@Composable
fun DAListItemCompat(
    modifier: Modifier,
    title: String,
    icon: String,
    iconColor: Color,
    selected: Boolean = false,
    isSelectionMode: Boolean = false,
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
        val isImage = icon.isBase64Image()
        Box {
            ColoredIcon(
                size = COMPAT_MODE_ICON_SIZE.dp,
                padding = 0.dp,
                color = if (isImage) Color.Transparent else iconColor
            ) {
                RemixIcon(
                    modifier = Modifier
                        .size(if (isImage) COMPAT_MODE_ICON_SIZE.dp else 24.dp)
                        .alpha(if (isSelectionMode && selected) 0.2f else 1f),
                    remixName = icon,
                    tint = Color.White
                )
            }
            if (isSelectionMode) {
                RemixIcon(
                    modifier = Modifier.align(Alignment.Center).size(18.dp),
                    remixName = if (selected) Remix.System.check_line else Remix.System.checkbox_blank_circle_line,
                    tint = if (selected) Color.White else MaterialTheme.colorScheme.primary
                )
            }
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
    colorfulCard: Boolean,
    isPinned: Boolean = false,
    onClick: () -> Unit,
    run: () -> Unit,
    onLongClick: () -> Unit,
    stopAllJobs: () -> Unit,
    isSelectionMode: Boolean = false,
    selected: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ShortXCardRoundedCornerShape)
            .longPressZoom(onLongClick = onLongClick) {
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
            val isRunning = runningInsCount > 0
            if (isRunning && !isSelectionMode) {
                RunningIndicator(runningInsCount, stopAllJobs, iconColor)
            } else {
                val isImage = icon.isBase64Image()
                if (colorfulCard) {
                    Box {
                        ColoredIcon(
                            modifier = Modifier,
                            size = Dimens.DAIconSize,
                            padding = 0.dp,
                            color = if (isImage) Color.Transparent else iconColor,
                            shape = if (LocalUiSettings.current.uiExperimental) MaterialShapes.Cookie12Sided.toShape() else CircleShape,
                            onClick = {
                                onLongClick()
                            }
                        ) {
                            RemixIcon(
                                modifier = Modifier
                                    .size(if (isImage) Dimens.DAIconSize else 16.dp)
                                    .alpha(if (isSelectionMode && selected) 0.2f else 1f),
                                remixName = icon,
                                tint = Color.White
                            )
                        }
                        if (isSelectionMode) {
                            RemixIcon(
                                modifier = Modifier.align(Alignment.Center).size(18.dp),
                                remixName = if (selected) Remix.System.check_line else Remix.System.checkbox_blank_circle_line,
                                tint = Color.White
                            )
                        }
                    }
                } else {
                    Box {
                        ColoredIcon(
                            modifier = Modifier,
                            size = Dimens.DAIconSize,
                            padding = 0.dp,
                            color = if (isImage) Color.Transparent else iconColor.copy(alpha = ActionColorBgAlpha),
                            shape = if (LocalUiSettings.current.uiExperimental) MaterialShapes.Cookie12Sided.toShape() else CircleShape,
                            onClick = {
                                onLongClick()
                            }
                        ) {
                            RemixIcon(
                                modifier = Modifier
                                    .size(if (isImage) Dimens.DAIconSize else 16.dp)
                                    .alpha(if (isSelectionMode && selected) 0.2f else 1f),
                                remixName = icon,
                                tint = iconColor
                            )
                        }
                        if (isSelectionMode) {
                            RemixIcon(
                                modifier = Modifier.align(Alignment.Center).size(18.dp),
                                remixName = if (selected) Remix.System.check_line else Remix.System.checkbox_blank_circle_line,
                                tint = iconColor
                            )
                        }
                    }
                }
            }

            if (isSelectionMode) {
                RemixIcon(
                    modifier = Modifier.size(20.dp),
                    remixName = if (selected) Remix.System.checkbox_circle_fill else Remix.System.checkbox_blank_circle_line,
                    tint = if (selected) MaterialTheme.colorScheme.primary else Color.Unspecified
                )
            } else {
                IconButton(onClick = {
                    run()
                }) {
                    if (colorfulCard) {
                        RemixIcon(
                            remixName = Remix.Media.play_line,
                            tint = darkenColor(iconColor, 0.4f)
                        )
                    } else {
                        RemixIcon(remixName = Remix.Media.play_circle_line, tint = NoteTextColor)
                    }
                }
            }
        }

        MediumSpacer()

        Column(modifier = Modifier) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.5.sp
                    ),
                    color = if (colorfulCard) themedTextColor(iconColor) else Color.Unspecified,
                    modifier = Modifier.weight(1f)
                )
                if (isPinned) {
                    TinySpacer()
                    RemixIcon(
                        remixName = Remix.Map.pushpin_fill,
                        tint = if (colorfulCard) themedTextColor(iconColor) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            SmallSpacer()
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = if (colorfulCard) themedTextColor(iconColor) else Color.Unspecified
            )
            MediumSpacer()
            TinySpacer()
        }
    }
}

@Composable
fun RunningIndicator(
    runningInsCount: Int, stopAllJobs: () -> Unit,
    iconColor: Color
) {
    BadgedBox(
        modifier = Modifier.size(Dimens.DAIconSize),
        badge = {
            Badge {
                AnimatedTextContainer(text = runningInsCount.toString()) {
                    Text(text = it)
                }
            }
        }) {
        Box(contentAlignment = Alignment.Center) {
            val circularIndicatorStroke = Stroke(
                width =
                    with(LocalDensity.current) {
                        6.dp.toPx()
                    },
                cap = StrokeCap.Round
            )

            if (LocalUiSettings.current.uiExperimental) {
                CircularWavyProgressIndicator(
                    modifier = Modifier.size(Dimens.DAIconSize),
                    color = iconColor,
                    stroke = Stroke(
                        width =
                            with(LocalDensity.current) {
                                6.dp.toPx()
                            },
                        cap = StrokeCap.Round
                    ),
                    trackColor = iconColor.copy(alpha = ActionColorBgAlpha)
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens.DAIconSize),
                    color = iconColor,
                    strokeWidth = 6.dp,
                    trackColor = iconColor.copy(alpha = ActionColorBgAlpha)
                )
            }
            IconButton(onClick = {
                stopAllJobs()
            }) {
                RemixIcon(
                    remixName = Remix.Media.stop_fill,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
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
    } else i18N[I18N_R.UI_SUBSCRIPTION_SUBSCRIBED_ONLY]

    val subscribeDialogState = rememberShortXBottomSheetState()
    FeatureAccessDialog(subscribeDialogState)

    val deleteDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = LocalI18N.current[I18N_R.UI_COMMON_DELETE],
        state = deleteDialogState,
        data = da,
        messageHint = {
            i18N[I18N_R.UI_DELETE_DIALOG_CONFIRM, mapOf("wht" to da.title)]
        },
        onConfirm = { handler.delete(da) }
    )

    val daSetSelectDialogState = rememberDASetSelectorState(onDASetSelected = {
        handler.moveToSet(da, it.id)
    })
    DASetSelectorDialog(state = daSetSelectDialogState)

    val createApkDialog = rememberConfirmDialogState()
    ConfirmDialog(
        title = i18N[I18N_R.UI_COMMON_INSTALL_AS_APK],
        state = createApkDialog,
        data = da,
        messageHint = {
            i18N[I18N_R.UI_COMMON_INSTALL_AS_APK_DESCRIPTION]
        },
    ) {
        handler.createApk(it)
    }

    val menuState = rememberDropdownSelectorState()

    DropdownSelector(
        widthFraction = menuDropdownFraction, state = menuState, items = listOfNotNull(
            // 添加置顶/取消置顶选项（放在第一个位置）
            if (da.isPinned) {
                DropdownItem(
                    labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_UNPIN]),
                    data = DAItemMenuOptions.Unpin,
                    icon = {
                        RemixIcon(Remix.Map.unpin_line)
                    })
            } else {
                DropdownItem(
                    labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_PINNED]),
                    data = DAItemMenuOptions.Pin,
                    icon = {
                        RemixIcon(Remix.Map.pushpin_2_line)
                    })
            },
            // 添加锁定/解锁选项
            if (da.isLocked) {
                DropdownItem(
                    labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_UNLOCK]),
                    data = DAItemMenuOptions.Unlock,
                    icon = {
                        RemixIcon(Remix.System.lock_unlock_line)
                    })
            } else {
                DropdownItem(
                    labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_LOCK]),
                    data = DAItemMenuOptions.Lock,
                    icon = {
                        RemixIcon(Remix.System.lock_line)
                    })
            },
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_SELECT]),
                data = DAItemMenuOptions.Select,
                icon = {
                    RemixIcon(Remix.System.checkbox_multiple_line)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_CREATE_SHORTCUT]),
                data = DAItemMenuOptions.Shortcut,
                badge = badge,
                icon = {
                    RemixIcon(Remix.System.external_link_line)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_CREATE_DYNAMIC_SHORTCUT]),
                data = DAItemMenuOptions.DynamicShortcut,
                badge = badge,
                icon = {
                    RemixIcon(Remix.System.external_link_line)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_REMOVE_DYNAMIC_SHORTCUT]),
                data = DAItemMenuOptions.RemoveDynamicShortcut,
                badge = badge,
                icon = {
                    RemixIcon(Remix.System.delete_bin_5_line)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_INSTALL_AS_APK]),
                data = DAItemMenuOptions.CreateApk,
                badge = badge,
                icon = {
                    RemixIcon(Remix.Logos.android_line)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_DUP]),
                data = DAItemMenuOptions.Dup,
                badge = badge,
                icon = {
                    RemixIcon(Remix.Document.file_copy_2_fill)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_EDIT]),
                data = DAItemMenuOptions.Edit,
                icon = {
                    RemixIcon(Remix.Design.edit_line)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_SHARE]),
                data = DAItemMenuOptions.Share,
                badge = badge,
                icon = {
                    RemixIcon(Remix.System.share_line)
                }),
            DropdownItem(
                labelLines = listOf("Deeplink"),
                data = DAItemMenuOptions.DeepLink,
                icon = {
                    RemixIcon(Remix.System.external_link_line)
                }),
            DropdownItem(
                labelLines = listOf("Shell cmd"),
                data = DAItemMenuOptions.ShellCommand,
                icon = {
                    RemixIcon(Remix.Editor.hashtag)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_MOVE_TO_SET]),
                data = DAItemMenuOptions.MoveToSet,
                badge = badge,
                icon = {
                    RemixIcon(Remix.Document.folders_fill)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_MOVE_OUT_SET]),
                data = DAItemMenuOptions.MoveOutSet,
                icon = {
                    RemixIcon(Remix.Document.folder_reduce_fill)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_SETTINGS_EVA_RECORD]),
                data = DAItemMenuOptions.ViewEvaRecord,
                icon = {
                    RemixIcon(Remix.System.history_fill)
                }),
            DropdownItem(
                labelLines = listOf(LocalI18N.current[I18N_R.UI_COMMON_DELETE]),
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
                                i18N[I18N_R.UI_COMMON_CREATE_SHORTCUT_SNACKBAR_MESSAGE],
                                i18N[I18N_R.UI_COMMON_SETTINGS],
                                true,
                            )
                            if (res == SnackbarResult.ActionPerformed) {
                                runCatching {
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
                            Toast.makeText(context, i18N[I18N_R.UI_COMMON_DONE], Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        subscribeDialogState.show()
                    }
                }

                DAItemMenuOptions.RemoveDynamicShortcut -> {
                    ActionShortcutActivity.removeDynamicShortcut(context, da)
                }

                DAItemMenuOptions.Pin -> {
                    handler.togglePin(da)
                }

                DAItemMenuOptions.Unpin -> {
                    handler.togglePin(da)
                }

                DAItemMenuOptions.Lock -> {
                    handler.toggleLock(da)
                }

                DAItemMenuOptions.Unlock -> {
                    handler.toggleLock(da)
                }

                DAItemMenuOptions.Select -> {
                    handler.select(da)
                }
            }
        })

    return menuState
}


enum class DAItemMenuOptions {
    Select, Dup, DeepLink, ShellCommand, Delete, Edit, Share, Shortcut, DynamicShortcut, RemoveDynamicShortcut, CreateApk, MoveToSet, MoveOutSet, ViewEvaRecord, Pin, Unpin, Lock, Unlock
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
            colorfulCard = true,
            onClick = {},
            run = {},
            onLongClick = {},
            stopAllJobs = {}
        )
    }
}

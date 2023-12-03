package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import okhttp3.internal.toLongOrDefault
import tornaco.apps.shortx.core.proto.action.MediaPlaybackAction
import tornaco.apps.shortx.core.proto.action.RingerMode
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.common.VolumeDirection
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.ActionCategory
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.base.rememberCategoryState
import tornaco.apps.shortx.ui.addrule.condition.SIMSlotSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.rememberSIMSlotSelectorState
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.fact.selector.BrightnessValueSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.CombineKeyEventSelectorActivity
import tornaco.apps.shortx.ui.addrule.fact.selector.KeyEventSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.OnOffAnyStateSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.QSTileSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.VibratePatternSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberBrightnessValueSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberKeyEventSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberOnOffAnyStateSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberQSTileSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberVibratePatternSelectorState
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.toBaseSelectorActionOrNull
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.SelectionDialog
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TipDialog
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberSelectionDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.base.rememberTipDialogState
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly
import kotlin.reflect.full.createInstance


@Composable
fun systemActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val i18N = LocalI18N.current
    val context = LocalContext.current

    val ringerModeSelectDialogState = rememberSelectionDialogState(
        title = i18N["ui.action.set.ringer.mode"],
        message = null,
        selectionLabel2Ids = listOf(RingerMode.normal, RingerMode.vibrate, RingerMode.silent).map {
            i18N[it.labelKey()] to it.name
        },
        onSelect = {
            val mode: RingerMode = RingerMode.valueOf(it)
            addAction(Action.SetRingerMode(mode = mode))
        }
    )
    SelectionDialog(state = ringerModeSelectDialogState)


    val setBrightnessState = rememberBrightnessValueSelectorState {
        addAction(Action.SetBrightness(value = it))
    }
    BrightnessValueSelector(state = setBrightnessState)

    val writeClipboardSelectorDialogState = rememberTextInputState(
        title = LocalI18N.current["ui.action.write.clipboard"],
        message = null,
        inputValidator = { InputValidateResult.OK },
        onSelected = {
            addAction(Action.WriteClipboard(text = it))
        })
    TextInputDialog(state = writeClipboardSelectorDialogState)

    val injectKeyCodeSelectorState = rememberKeyEventSelectorState(selected = { code ->
        addAction(Action.InjectKeyCode(code = code))
    })
    KeyEventSelector(injectKeyCodeSelectorState)

    val injectCombineKeyCodeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.toBaseSelectorActionOrNull<Action.InjectCombineKeyCode>()
            action?.let {
                addAction(it)
            }
        }
    )

    val adjustVolumeSelectorState = rememberMenuDialogState<String>(
        title = i18N["ui.action.adjust.volume"],
        menuItems = listOf(
            VolumeDirection.ADJUST_LOWER,
            VolumeDirection.ADJUST_RAISE,
            VolumeDirection.ADJUST_MUTE,
            VolumeDirection.ADJUST_UNMUTE,
            VolumeDirection.ADJUST_TOGGLE_MUTE
        ).map {
            MenuDialogItem(id = it.name, title = i18N[it.labelKey()])
        },
        onItemSelected = { _, id ->
            val direction = VolumeDirection.valueOf(id)
            addAction(
                Action.AdjustVolume(direction = direction)
            )
        }
    )
    MenuDialog(state = adjustVolumeSelectorState)

    val vibratePatternDialogState = rememberVibratePatternSelectorState(selected = {
        addAction(it)
    })
    VibratePatternSelector(state = vibratePatternDialogState)

    val audioRecordingState = rememberAudioRecordingDialogState(onSelected = {
        addAction(it)
    })
    AudioRecordingDialog(state = audioRecordingState)

    val comingSoonDialogState = rememberTipDialogState(
        title = LocalI18N.current["ui.common.coming.soon"],
        tip = LocalI18N.current["ui.common.coming.soon"]
    )
    TipDialog(state = comingSoonDialogState)

    val switchMobileDataSelectorState = rememberSIMSlotSelectorState(
        canSelectAny = false,
        onSelected = {
            addAction(Action.SwitchMobileDataSlot(slotId = it))
        })
    SIMSlotSelectorDialog(state = switchMobileDataSelectorState)

    val wallpaperFileSelectorState = rememberSetWallpaperState(
        onSelected = {
            addAction(it)
        }
    )
    SetWallpaperSelectorDialog(state = wallpaperFileSelectorState)

    val clickTileSelectorState = rememberQSTileSelectorState(
        title = LocalI18N.current["ui.action.click.tile"],
        onSelected = {
            addAction(Action.ClickTile(tile = it, isLongClick = false))
        }
    )
    QSTileSelectorDialog(state = clickTileSelectorState)

    val mediaPlayMenuDialogState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.action.media.play.back"],
        menuItems = MediaPlaybackAction.values()
            .filter { it != MediaPlaybackAction.UNRECOGNIZED }
            .map {
                MenuDialogItem(id = it.name, title = LocalI18N.current[it.labelKey()])
            },
        onItemSelected = { _, id ->
            val action = MediaPlaybackAction.valueOf(id)
            addAction(Action.MediaPlayback(action = action))
        }
    )
    MenuDialog(state = mediaPlayMenuDialogState)

    val setVolumeDialogState = rememberSetVolumeDialogState(onSelected = {
        addAction(it)
    })
    SetVolumeDialog(state = setVolumeDialogState)

    val biometricVerifyLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.toBaseSelectorActionOrNull<Action.BiometricVerify>()
            action?.let {
                addAction(it)
            }
        }
    )

    val apmModeSelectorState = rememberOnOffAnyStateSelectorState(
        title = LocalI18N.current["ui.action.set.apm.mode"],
        options = listOf(OnOffAny.On, OnOffAny.Off),
        selected = {
            addAction(Action.SetAPMModeEnabled(isEnableAPM = (it == OnOffAny.On)))
        })
    OnOffAnyStateSelectorDialog(state = apmModeSelectorState)

    val screenTimeoutInputDialogState = rememberTextInputState(
        title = LocalI18N.current["ui.action.set.screen.timeout"],
        message = LocalI18N.current["ui.action.set.screen.timeout.message"],
        onSelected = {
            val timeoutLong = it.toLongOrDefault(15 * 1000)
            addAction(Action.SetScreenTimeout(timeout = timeoutLong))
        }
    )
    TextInputDialog(state = screenTimeoutInputDialogState)

    val screenRotateSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.action.set.screen.rotate"],
        menuItems = ScreenRotateDegree.values().filterNot { it == ScreenRotateDegree.UNRECOGNIZED }
            .map {
                MenuDialogItem(id = it.name, title = LocalI18N.current[it.labelKey()])
            },
        onItemSelected = { data, id ->
            val degree = ScreenRotateDegree.valueOf(id)
            addAction(Action.SetScreenRotate(degree = degree))
        }
    )
    MenuDialog(state = screenRotateSelectorState)

    return { source ->
        when (source.clazz) {
            Action.MediaPlayback::class -> {
                mediaPlayMenuDialogState.show()
                true
            }

            Action.ClickTile::class -> {
                clickTileSelectorState.show()
                true
            }

            Action.WakeupScreen::class -> {
                addAction(Action.WakeupScreen())
                true
            }

            Action.SleepScreen::class -> {
                addAction(Action.SleepScreen())
                true
            }

            Action.ReadClipboard::class -> {
                addAction(Action.ReadClipboard())
                true
            }

            Action.WriteClipboard::class -> {
                writeClipboardSelectorDialogState.show(source.getActionAs<Action.WriteClipboard>()?.text)
                true
            }

            Action.SetRingerMode::class -> {
                ringerModeSelectDialogState.show()
                true
            }

            Action.SetBrightness::class -> {
                setBrightnessState.show(100)
                true
            }

            Action.InjectKeyCode::class -> {
                injectKeyCodeSelectorState.show()
                true
            }

            Action.InjectCombineKeyCode::class -> {
                injectCombineKeyCodeLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getActionAs<Action.InjectCombineKeyCode>(),
                        CombineKeyEventSelectorActivity::class.java
                    )
                )
                true
            }

            Action.AdjustVolume::class -> {
                adjustVolumeSelectorState.show()
                true
            }

            Action.SetVolume::class -> {
                setVolumeDialogState.show(source.getActionAs())
                true
            }

            Action.Vibrate::class -> {
                vibratePatternDialogState.show(source.getActionAs())
                true
            }

            Action.AudioRecording::class -> {
                audioRecordingState.show(source.getActionAs())
                true
            }

            Action.StopAudioRecording::class -> {
                addAction(Action.StopAudioRecording())
                true
            }

            Action.SwitchMobileDataSlot::class -> {
                switchMobileDataSelectorState.show()
                true
            }

            Action.SetWallpaper::class -> {
                wallpaperFileSelectorState.show(source.getActionAs())
                true
            }

            Action.BiometricVerify::class -> {
                biometricVerifyLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getActionAs<Action.BiometricVerify>(),
                        BiometricVerifySelectorActivity::class.java
                    )
                )
                true
            }

            Action.DisableBT::class,
            Action.DisableLocation::class,
            Action.DisableDND::class,
            Action.DisableDarkMode::class,
            Action.DisableData::class,
            Action.DisableFlashLight::class,
            Action.DisableHotSpot::class,
            Action.DisableNFC::class,
            Action.DisableWifi::class,
            Action.DisableSensorsOff::class,
            Action.DisableAutoBrightness::class,

            Action.EnableBT::class,
            Action.EnableLocation::class,
            Action.EnableDND::class,
            Action.EnableDarkMode::class,
            Action.EnableData::class,
            Action.EnableFlashLight::class,
            Action.EnableHotSpot::class,
            Action.EnableNFC::class,
            Action.EnableAutoBrightness::class,
            Action.EnableSensorsOff::class,
            Action.EnableWifi::class -> {
                addAction(source.clazz.createInstance())
                true
            }

            Action.SetAPMModeEnabled::class -> {
                apmModeSelectorState.show()
                true
            }

            Action.SetScreenTimeout::class -> {
                screenTimeoutInputDialogState.show(source.getActionAs<Action.SetScreenTimeout>()?.timeout?.toString())
                true
            }

            Action.SetScreenRotate::class -> {
                screenRotateSelectorState.show()
                true
            }

            else -> false
        }
    }
}

@Composable
fun SystemActions(
    keyword: String,
    subscribeState: LVLStateHolder.State,
    addAction: (Action) -> Unit
) {
    val i18N = LocalI18N.current
    val actionItems = listOfNotNull(
        Action.WakeupScreen::class,
        Action.SleepScreen::class,
        Action.InjectKeyCode::class,
        Action.InjectCombineKeyCode::class,
        Action.MediaPlayback::class,
        Action.SetRingerMode::class,
        Action.AdjustVolume::class,
        Action.SetVolume::class,
        Action.ReadClipboard::class,
        Action.WriteClipboard::class,
        Action.EnableWifi::class,
        Action.DisableWifi::class,
        Action.EnableLocation::class,
        Action.DisableLocation::class,
        Action.EnableBT::class,
        Action.DisableBT::class,
        Action.EnableData::class,
        Action.DisableData::class,
        Action.EnableHotSpot::class,
        Action.DisableHotSpot::class,
        Action.EnableNFC::class,
        Action.DisableNFC::class,
        Action.EnableDND::class,
        Action.DisableDND::class,
        Action.EnableFlashLight::class,
        Action.DisableFlashLight::class,
        Action.EnableDarkMode::class,
        Action.DisableDarkMode::class,
        Action.EnableAutoBrightness::class,
        Action.DisableAutoBrightness::class,
        Action.SetAPMModeEnabled::class,
        Action.SetBrightness::class,
        Action.EnableSensorsOff::class,
        Action.DisableSensorsOff::class,
        Action.Vibrate::class,
        OsUtils.isQOrAbove().select(Action.AudioRecording::class, null),
        OsUtils.isQOrAbove().select(Action.StopAudioRecording::class, null),
        Action.SwitchMobileDataSlot::class,
        Action.SetWallpaper::class,
        Action.ClickTile::class,
        Action.BiometricVerify::class,
        Action.SetScreenTimeout::class,
        Action.SetScreenRotate::class
    ).map {
        SelectorItem(
            type = it,
            labelAndDescription = labelAndDescriptionForActionSelector(i18N, it),
            icon = imageVectorForAction(it),
            isForSubscribedOnly = remember(subscribeState) {
                !subscribeState.isSubscribed && isForSubscribedOnly(it)
            },
            iconColor = colorForAction(it)
        )
    }.filter {
        keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
    }

    val categoryState = rememberCategoryState(categoryTitle = i18N[ActionCategory.Settings.i18NKey])
    if (actionItems.isNotEmpty()) {
        ActionCategory(i18N[ActionCategory.Settings.i18NKey], actionItems.size, categoryState)
    }

    val selectHandler = systemActionSelectHandler(addAction = addAction)
    AnimatedVisibility(visible = categoryState.isExpand) {
        SelectorList(
            items = actionItems,
            itemSelected = { it ->
                selectHandler(ActionSelectSource.FromClass(it))
            }
        )
    }
}
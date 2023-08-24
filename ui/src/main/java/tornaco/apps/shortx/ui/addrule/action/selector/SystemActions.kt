package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import tornaco.apps.shortx.core.proto.action.RingerMode
import tornaco.apps.shortx.core.proto.common.VolumeDirection
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.condition.SIMSlotSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.rememberSIMSlotSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.BrightnessValueSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.KeyEventSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.VibratePatternSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberBrightnessValueSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberKeyEventSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberVibratePatternSelectorState
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

    return { source ->
        when (source.clazz) {
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

            Action.AdjustVolume::class -> {
                adjustVolumeSelectorState.show()
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
        Action.SetRingerMode::class,
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
        Action.SetBrightness::class,
        Action.EnableSensorsOff::class,
        Action.DisableSensorsOff::class,
        Action.AdjustVolume::class,
        Action.Vibrate::class,
        OsUtils.isQOrAbove().select(Action.AudioRecording::class, null),
        OsUtils.isQOrAbove().select(Action.StopAudioRecording::class, null),
        Action.SwitchMobileDataSlot::class,
        Action.SetWallpaper::class,
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

    if (actionItems.isNotEmpty()) {
        ActionCategoryTitle(ActionCategory.Settings)
    }

    val selectHandler = systemActionSelectHandler(addAction = addAction)
    SelectorList(
        items = actionItems,
        itemSelected = { it ->
            selectHandler(ActionSelectSource.FromClass(it))
        }
    )
}
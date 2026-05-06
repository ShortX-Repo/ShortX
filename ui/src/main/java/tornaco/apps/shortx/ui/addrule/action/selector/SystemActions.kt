package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import okhttp3.internal.toLongOrDefault
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.proto.action.CurrentLocationProviderPreference
import tornaco.apps.shortx.core.proto.action.MediaPlaybackAction
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.common.OnOffToggle
import tornaco.apps.shortx.core.proto.common.RingerMode
import tornaco.apps.shortx.core.proto.common.ScreenOnTimeStartFrom
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.common.VolumeDirection
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.action.mapper.actionLabelKey
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
import tornaco.apps.shortx.ui.addrule.condition.Toggle5GSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.ToggleSlotSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.rememberSIMSlotSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberToggle5GSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberToggleSlotSelectorState
import tornaco.apps.shortx.ui.addrule.fact.mapper.factLabelKey
import tornaco.apps.shortx.ui.addrule.fact.selector.BrightnessValueSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.CombineKeyEventSelectorActivity
import tornaco.apps.shortx.ui.addrule.fact.selector.KeyEventSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.OnOffAnyStateSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.QSTileSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.VibratePatternSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.WifiSSIDSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberBrightnessValueSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberKeyEventWithOptionsSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberOnOffAnyStateSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberQSTileSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberVibratePatternSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberWifiSSIDSelectorState
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.SelectionDialog
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TipDialog
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberSelectionDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.base.rememberTipDialogState
import tornaco.apps.shortx.ui.ringtone.RingtonePickerDialog
import tornaco.apps.shortx.ui.ringtone.rememberRingtonePickerState
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
        selectionLabel2Ids = listOf(RingerMode.normal, RingerMode.vibrate, RingerMode.silent, RingerMode.switchMode).map {
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

    val continuousBrightnessDialogState = rememberContinuousBrightnessDialogState {
        addAction(it)
    }
    ContinuousBrightnessDialog(state = continuousBrightnessDialogState)

    val writeClipboardSelectorDialogState = rememberTextInputState(
        title = LocalI18N.current["ui.action.write.clipboard"],
        message = null,
        inputValidator = { InputValidateResult.OK },
        onSelected = {
            addAction(Action.WriteClipboard(text = it))
        })
    TextInputDialog(state = writeClipboardSelectorDialogState)

    val injectKeyCodeSelectorState =
        rememberKeyEventWithOptionsSelectorState(selected = { code, options ->
            addAction(
                Action.InjectKeyCode(
                    code = code,
                    longpress = options.isLongPress,
                    doublepress = options.isDoublePress
                )
            )
        })
    KeyEventSelector(injectKeyCodeSelectorState)

    val injectCombineKeyCodeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.InjectCombineKeyCode>()
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

    val requestAudioFocusSelectorState = rememberMenuDialogState<String>(
        title = i18N["ui.action.request.audio.focus"],
        menuItems = listOf(
            MenuDialogItem(
                id = true.toString(),
                title = i18N["ui.action.request.audio.focus.request"]
            ),
            MenuDialogItem(
                id = false.toString(),
                title = i18N["ui.action.request.audio.focus.abandon"]
            ),
        ),
        onItemSelected = { _, id ->
            val isRequest = id.toBoolean()
            addAction(
                Action.RequestAudioFocus(isRequest = isRequest)
            )
        }
    )
    MenuDialog(state = requestAudioFocusSelectorState)

    val screenTimeDialogState = rememberMenuDialogState<String>(
        title = i18N["ui.common.get"] + i18N["ui.fact.screen.on.time"],
        menuItems = listOf(
            ScreenOnTimeStartFrom.ScreenOnTimeStartFrom_LastScreenOff,
            ScreenOnTimeStartFrom.ScreenOnTimeStartFrom_SystemReady,
        ).map {
            MenuDialogItem(id = it.name, title = i18N[it.factLabelKey()])
        },
        onItemSelected = { _, id ->
            val from = ScreenOnTimeStartFrom.valueOf(id)
            addAction(
                Action.GetScreenOnTime(from = from)
            )
        }
    )
    MenuDialog(state = screenTimeDialogState)

    var currentLocationProviderPreference by remember {
        mutableStateOf(CurrentLocationProviderPreference.CurrentLocationProviderPreference_Auto)
    }
    var currentLocationTimeoutMillis by remember { mutableStateOf(3000L) }

    val currentLocationTimeoutDialogState = rememberTextInputState(
        title = i18N["ui.action.get.current.location.info.timeout"],
        message = i18N["ui.action.get.current.location.info.timeout.message"],
        onSelected = {
            currentLocationTimeoutMillis = it.toLongOrDefault(currentLocationTimeoutMillis)
            addAction(
                Action.GetCurrentLocationInfo(
                    providerPreference = currentLocationProviderPreference,
                    timeoutMillis = currentLocationTimeoutMillis
                )
            )
        }
    )
    TextInputDialog(state = currentLocationTimeoutDialogState)

    val currentLocationProviderDialogState = rememberMenuDialogState<String>(
        title = i18N["ui.action.get.current.location.info.provider"],
        menuItems = CurrentLocationProviderPreference.entries
            .filterNot { it == CurrentLocationProviderPreference.UNRECOGNIZED }
            .map {
                MenuDialogItem(id = it.name, title = i18N[it.labelKey()])
            },
        onItemSelected = { _, id ->
            currentLocationProviderPreference = CurrentLocationProviderPreference.valueOf(id)
            currentLocationTimeoutDialogState.show(currentLocationTimeoutMillis.toString())
        }
    )
    MenuDialog(state = currentLocationProviderDialogState)

    val currentLocationInfoNoticeDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = "ui.action.get.current.location.info",
        state = currentLocationInfoNoticeDialogState,
        data = Unit,
        messageHint = { i18N["ui.action.current.location.notice"] },
        onConfirm = {
            currentLocationProviderDialogState.show()
        }
    )

    var currentLocationAddressProviderPreference by remember {
        mutableStateOf(CurrentLocationProviderPreference.CurrentLocationProviderPreference_Auto)
    }
    var currentLocationAddressTimeoutMillis by remember { mutableStateOf(3000L) }

    val currentLocationAddressTimeoutDialogState = rememberTextInputState(
        title = i18N["ui.action.get.current.location.address.timeout"],
        message = i18N["ui.action.get.current.location.address.timeout.message"],
        onSelected = {
            currentLocationAddressTimeoutMillis = it.toLongOrDefault(currentLocationAddressTimeoutMillis)
            addAction(
                Action.GetCurrentLocationAddress(
                    providerPreference = currentLocationAddressProviderPreference,
                    timeoutMillis = currentLocationAddressTimeoutMillis
                )
            )
        }
    )
    TextInputDialog(state = currentLocationAddressTimeoutDialogState)

    val currentLocationAddressProviderDialogState = rememberMenuDialogState<String>(
        title = i18N["ui.action.get.current.location.address.provider"],
        menuItems = CurrentLocationProviderPreference.entries
            .filterNot { it == CurrentLocationProviderPreference.UNRECOGNIZED }
            .map {
                MenuDialogItem(id = it.name, title = i18N[it.labelKey()])
            },
        onItemSelected = { _, id ->
            currentLocationAddressProviderPreference = CurrentLocationProviderPreference.valueOf(id)
            currentLocationAddressTimeoutDialogState.show(currentLocationAddressTimeoutMillis.toString())
        }
    )
    MenuDialog(state = currentLocationAddressProviderDialogState)

    val currentLocationAddressNoticeDialogState = rememberConfirmDialogState()
    ConfirmDialog(
        title = "ui.action.get.current.location.address",
        state = currentLocationAddressNoticeDialogState,
        data = Unit,
        messageHint = { i18N["ui.action.current.location.notice"] },
        onConfirm = {
            currentLocationAddressProviderDialogState.show()
        }
    )

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

    val toggle5GSelectorState = rememberToggle5GSelectorState {
        addAction(it)
    }
    Toggle5GSelectorDialog(dialogState = toggle5GSelectorState)

    val toggleSlotState = rememberToggleSlotSelectorState {
        addAction(it)
    }
    ToggleSlotSelectorDialog(dialogState = toggleSlotState)

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
        menuItems = MediaPlaybackAction.entries
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

    val continuousVolumeDialogState = rememberContinuousVolumeDialogState(onSelected = {
        addAction(it)
    })
    ContinuousVolumeDialog(state = continuousVolumeDialogState)

    val continuousGesturePointerDialogState = rememberContinuousGesturePointerDialogState(onSelected = {
        addAction(it)
    })
    ContinuousGesturePointerDialog(state = continuousGesturePointerDialogState)

    val biometricVerifyLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.BiometricVerify>()
            action?.let {
                addAction(it)
            }
        }
    )

    val insetsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.ShowHideInsets>()
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

    val showRecentAppsState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.action.recent.apps"],
        menuItems = OnOffToggle.entries.filterNot { it == OnOffToggle.OnOffToggle_Toggle || it == OnOffToggle.UNRECOGNIZED }
            .map {
                MenuDialogItem(id = it.name, title = LocalI18N.current[it.labelKey()])
            },
        onItemSelected = { _, id ->
            val state = OnOffToggle.valueOf(id)
            addAction(Action.ShowRecentApps(state = state))
        }
    )
    MenuDialog(state = showRecentAppsState)

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
        menuItems = ScreenRotateDegree.entries.filterNot { it == ScreenRotateDegree.UNRECOGNIZED }
            .map {
                MenuDialogItem(id = it.name, title = LocalI18N.current[it.actionLabelKey()])
            },
        onItemSelected = { data, id ->
            val degree = ScreenRotateDegree.valueOf(id)
            addAction(Action.SetScreenRotate(degree = degree))
        }
    )
    MenuDialog(state = screenRotateSelectorState)

    val stayAwakeSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.action.stay.awake"],
        menuItems = OnOffToggle.entries.filterNot { it == OnOffToggle.UNRECOGNIZED }
            .map {
                MenuDialogItem(id = it.name, title = LocalI18N.current[it.labelKey()])
            },
        onItemSelected = { data, id ->
            val stay = OnOffToggle.valueOf(id)
            addAction(Action.StayAwake(stay = stay))
        }
    )
    MenuDialog(state = stayAwakeSelectorState)

    val setMasterSyncSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.action.sync"],
        menuItems = OnOffToggle.entries.filterNot { it == OnOffToggle.UNRECOGNIZED }
            .map {
                MenuDialogItem(id = it.name, title = LocalI18N.current[it.labelKey()])
            },
        onItemSelected = { data, id ->
            val sync = OnOffToggle.valueOf(id)
            addAction(Action.SetMasterSync(sync = sync))
        }
    )
    MenuDialog(state = setMasterSyncSelectorState)

    val wifiConnectedSelectorState = rememberWifiSSIDSelectorState(
        title = LocalI18N.current["ui.action.connect.wifi"],
        message = LocalI18N.current["ui.fact.wifi.connect.to.input.ssid"],
        loadSSID = {
            shortXManager.getPrivilegedConfiguredNetworks().filterNot { it.isBlank() }
        },
        showSymbolButton = false,
        onSelected = {
            addAction(Action.ConnectWifi(ssid = it))
        }
    )
    WifiSSIDSelectorDialog(state = wifiConnectedSelectorState)

    val ringtonePickerState =
        rememberRingtonePickerState(title = LocalI18N.current["ui.action.ringtone"]) {
            addAction(Action.PlayRingtone(ringtone = it))
        }
    RingtonePickerDialog(state = ringtonePickerState)

    val sendSMSSelectorState = rememberSendSMSSelectorState {
        addAction(it)
    }
    SendSMSSelectorDialog(state = sendSMSSelectorState)

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

            Action.GetCurrentLocationInfo::class -> {
                val current = source.getActionAs<Action.GetCurrentLocationInfo>()
                currentLocationProviderPreference = current?.providerPreference
                    ?: CurrentLocationProviderPreference.CurrentLocationProviderPreference_Auto
                currentLocationTimeoutMillis = current?.timeoutMillis ?: 3000L
                currentLocationInfoNoticeDialogState.show()
                true
            }

            Action.GetCurrentLocationAddress::class -> {
                val current = source.getActionAs<Action.GetCurrentLocationAddress>()
                currentLocationAddressProviderPreference = current?.providerPreference
                    ?: CurrentLocationProviderPreference.CurrentLocationProviderPreference_Auto
                currentLocationAddressTimeoutMillis = current?.timeoutMillis ?: 3000L
                currentLocationAddressNoticeDialogState.show()
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

            Action.ContinuousAdjustBrightness::class -> {
                continuousBrightnessDialogState.show(source.getActionAs())
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

            Action.ContinuousAdjustVolume::class -> {
                continuousVolumeDialogState.show(source.getActionAs())
                true
            }

            Action.ContinuousGesturePointer::class -> {
                continuousGesturePointerDialogState.show(source.getActionAs())
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

            Action.ConnectWifi::class -> {
                wifiConnectedSelectorState.show(source.getActionAs<Action.ConnectWifi>()?.ssid)
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

            Action.ShowHideInsets::class -> {
                insetsLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getActionAs<Action.ShowHideInsets>(),
                        ShowHideInsetsSelectorActivity::class.java
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

            Action.StayAwake::class -> {
                stayAwakeSelectorState.show()
                true
            }

            Action.LockDeviceNow::class -> {
                addAction(Action.LockDeviceNow())
                true
            }

            Action.SetMasterSync::class -> {
                setMasterSyncSelectorState.show()
                true
            }

            Action.PlayRingtone::class -> {
                ringtonePickerState.show()
                true
            }

            Action.Toggle5G::class -> {
                toggle5GSelectorState.show(source.getActionAs())
                true
            }

            Action.ToggleSlot::class -> {
                toggleSlotState.show(source.getActionAs())
                true
            }

            Action.SendSMS::class -> {
                sendSMSSelectorState.show(source.getActionAs())
                true
            }

            Action.DisconnectCurrentWifi::class -> {
                addAction(Action.DisconnectCurrentWifi())
                true
            }

            Action.ShowRecentApps::class -> {
                showRecentAppsState.show()
                true
            }

            Action.ShowGlobalActionsMenu::class -> {
                addAction(Action.ShowGlobalActionsMenu())
                true
            }

            Action.GetScreenOnTime::class -> {
                screenTimeDialogState.show()
                true
            }

            Action.RequestAudioFocus::class -> {
                requestAudioFocusSelectorState.show()
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
    selectedFilter: ActionCategory,
    addAction: (Action) -> Unit
) {
    val i18N = LocalI18N.current
    val actionItems = listOfNotNull(
        Action.WakeupScreen::class,
        Action.SleepScreen::class,
        Action.LockDeviceNow::class,
        Action.StayAwake::class,
        Action.InjectKeyCode::class,
        Action.InjectCombineKeyCode::class,
        Action.ShowRecentApps::class,
        Action.ShowGlobalActionsMenu::class,
        BuildPropInternal.APP_IS_DEBUG.select(Action.ShowHideInsets::class, null),
        Action.MediaPlayback::class,
        Action.SetRingerMode::class,
        Action.AdjustVolume::class,
        Action.SetVolume::class,
        Action.ContinuousAdjustVolume::class,
        Action.ContinuousGesturePointer::class,
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
        Action.SwitchMobileDataSlot::class,
        Action.Toggle5G::class,
        Action.ToggleSlot::class,
        Action.SetAPMModeEnabled::class,
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
        Action.ContinuousAdjustBrightness::class,
        Action.EnableSensorsOff::class,
        Action.DisableSensorsOff::class,
        Action.Vibrate::class,
        Action.PlayRingtone::class,
        Action.RequestAudioFocus::class,
        OsUtils.isQOrAbove().select(Action.AudioRecording::class, null),
        OsUtils.isQOrAbove().select(Action.StopAudioRecording::class, null),
        Action.SetWallpaper::class,
        Action.ClickTile::class,
        Action.BiometricVerify::class,
        Action.SetScreenTimeout::class,
        Action.SetScreenRotate::class,
        Action.ConnectWifi::class,
        Action.DisconnectCurrentWifi::class,
        Action.SetMasterSync::class,
        Action.SendSMS::class,
        Action.GetCurrentLocationInfo::class,
        Action.GetCurrentLocationAddress::class,
        Action.GetScreenOnTime::class,
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
    if (actionItems.isNotEmpty() && selectedFilter == ActionCategory.All) {
        ActionCategory(i18N[ActionCategory.Settings.i18NKey], actionItems.size, categoryState)
    }

    val selectHandler = systemActionSelectHandler(addAction = addAction)

    val isVisible =
        (categoryState.isExpand && selectedFilter == ActionCategory.All) || selectedFilter == ActionCategory.Settings
    AnimatedVisibility(visible = isVisible) {
        SelectorList(
            items = actionItems,
            itemSelected = { it ->
                selectHandler(ActionSelectSource.FromClass(it))
            }
        )
    }
}
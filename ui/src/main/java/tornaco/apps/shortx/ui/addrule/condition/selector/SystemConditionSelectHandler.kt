package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.common.QSTileState
import tornaco.apps.shortx.core.proto.common.RingerMode
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.condition.CalendarDayType
import tornaco.apps.shortx.core.proto.condition.PlugType
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectSource
import tornaco.apps.shortx.ui.addrule.condition.EvaluateGlobalVarSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.EvaluateScreenOnTimeSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.IntOpSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.IntOpSelectorRes
import tornaco.apps.shortx.ui.addrule.condition.SIMSlotSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.getConditionAs
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.toProtoCondition
import tornaco.apps.shortx.ui.addrule.condition.rememberEvaluateGlobalVarSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberEvaluateScreenOnTimeSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberIntOpSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberSIMSlotSelectorState
import tornaco.apps.shortx.ui.addrule.fact.mapper.factLabelKey
import tornaco.apps.shortx.ui.addrule.fact.selector.BTDeviceSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.OnOffAnyStateSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.QSTileSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.WifiSSIDSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.calendarDayTypeDetailsText
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberBTDeviceSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberOnOffAnyStateSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberQSTileSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberWifiSSIDSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.SelectionDialog
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberSelectionDialogState
import java.util.UUID
import tornaco.apps.shortx.core.i18n.I18N_R

@Composable
fun systemConditionSelectHandler(addCondition: (Condition) -> Unit): (ConditionSelectSource) -> Boolean {
    val evaluateGlobalVarSelectorDialogState = rememberEvaluateGlobalVarSelectorState {
        addCondition(it)
    }
    EvaluateGlobalVarSelectorDialog(state = evaluateGlobalVarSelectorDialogState)

    val batteryPercentDialog = rememberIntOpSelectorState(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_BATTERY_PERCENT],
        valueTitle = LocalI18N.current[I18N_R.UI_CONDITIONS_BATTERY_PERCENT_VALUE],
        maxValue = 100,
        evaluate = {
            val res = shortXManager.evaluateCondition(
                context = EvaluateContext.newBuilder()
                    .setSrcId(UUID.randomUUID().toString())
                    .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
                    .setSrcTitle("Testing battery level")
                    .build(),
                condition = Condition.BatteryPercent(value = it.value, op = it.op)
                    .toProtoCondition().pack_()
            )
            res?.toString() ?: "FAIL TO EVALUATE"
        }
    ) {
        addCondition(Condition.BatteryPercent(value = it.value, op = it.op))
    }
    IntOpSelectorDialog(state = batteryPercentDialog)

    val availableMemoryDialog = rememberIntOpSelectorState(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_MEMORY_AVAILABLE],
        valueTitle = LocalI18N.current[I18N_R.UI_CONDITIONS_MEMORY_AVAILABLE_VALUE],
        evaluate = {
            val res = shortXManager.evaluateCondition(
                context = EvaluateContext.newBuilder()
                    .setSrcId(UUID.randomUUID().toString())
                    .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
                    .setSrcTitle("Testing available memory")
                    .build(),
                condition = Condition.AvailableMemory(valueMb = it.value, op = it.op)
                    .toProtoCondition().pack_()
            )
            res?.toString() ?: "FAIL TO EVALUATE"
        }
    ) {
        addCondition(Condition.AvailableMemory(valueMb = it.value, op = it.op))
    }
    IntOpSelectorDialog(state = availableMemoryDialog)

    val wifiSignalSelectorState = rememberIntOpSelectorState(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_WIFI_SIGNAL],
        valueTitle = LocalI18N.current[I18N_R.UI_CONDITIONS_WIFI_SIGNAL_LEVEL, mapOf(
            "max" to shortXManager.getMaxSignalLevel().toString()
        )],
        maxValue = shortXManager.getMaxSignalLevel(),
        evaluate = {
            val res = shortXManager.evaluateCondition(
                context = EvaluateContext.newBuilder()
                    .setSrcId(UUID.randomUUID().toString())
                    .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
                    .setSrcTitle("Testing wifi level")
                    .build(),
                condition = Condition.ConnectedWifiSignal(level = it.value, op = it.op)
                    .toProtoCondition().pack_()
            )
            res?.toString() ?: "FAIL TO EVALUATE"
        }
    ) {
        addCondition(Condition.ConnectedWifiSignal(level = it.value, op = it.op))
    }
    IntOpSelectorDialog(state = wifiSignalSelectorState)

    val timeInRangeSelectorState = rememberTimeOfADayRangeSelectorState(selected = {
        addCondition(Condition.TimeInRange(range = it))
    })
    TimeOfADayRangeSelector(state = timeInRangeSelectorState)

    val calendarDayMatchSelectDialogState = rememberSelectionDialogState(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_CALENDAR_DAY_MATCH],
        message = calendarDayTypeDetailsText(LocalI18N.current),
        selectionLabel2Ids = listOf(
            CalendarDayType.AnyDay,
            CalendarDayType.WorkdayOnly,
            CalendarDayType.HolidayOnly,
            CalendarDayType.WeekendOnly,
            CalendarDayType.TransferWorkdayOnly,
        ).map {
            LocalI18N.current[it.labelKey()] to it.name
        },
        onSelect = {
            addCondition(Condition.CalendarDayMatch(dayType = CalendarDayType.valueOf(it)))
        }
    )
    SelectionDialog(state = calendarDayMatchSelectDialogState)

    val chargeStateSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_CHARGE_STATE],
        menuItems = listOf(
            MenuDialogItem(
                id = "charge",
                title = LocalI18N.current[I18N_R.UI_CONDITIONS_CHARGE_STATE_CHARGE],
            ),
            MenuDialogItem(
                id = "notCharge",
                title = LocalI18N.current[I18N_R.UI_CONDITIONS_CHARGE_STATE_NOT_CHARGE],
            ),
        ),
        onItemSelected = { data, id ->
            addCondition(Condition.ChargeState(requireIsCharge = id == "charge"))
        }
    )
    MenuDialog(state = chargeStateSelectorState)

    val plugStateSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_PLUG_STATE],
        menuItems = PlugType.entries.filterNot { it == PlugType.UNRECOGNIZED }.map {
            MenuDialogItem(
                id = it.name,
                title = LocalI18N.current["ui.conditions.plug.type.${it.name.lowercase()}"]
            )
        },
        onItemSelected = { _, id ->
            addCondition(Condition.PlugState(type = PlugType.valueOf(id)))
        }
    )
    MenuDialog(state = plugStateSelectorState)

    val wifiConnectedToSelectorState = rememberWifiSSIDSelectorState(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_WIFI_CONNECTED],
        message = LocalI18N.current[I18N_R.UI_CONDITIONS_WIFI_CONNECTED_TARGET],
        loadSSID = {
            shortXManager.getWifiScanResults().filterNot { it.isBlank() }
        },
        showSymbolButton = false,
        onSelected = {
            addCondition(Condition.RequireWifiConnected(requiredSSID = it))
        }
    )
    WifiSSIDSelectorDialog(state = wifiConnectedToSelectorState)

    val btConnectedSelectorState = rememberBTDeviceSelectorState(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_BT_CONNECTED],
        onSelected = { name, address, timeout ->
            addCondition(Condition.RequireBTConnected(requiredDevice = name))
        }
    )
    BTDeviceSelectorDialog(state = btConnectedSelectorState)

    val btFoundSelectorState = rememberBTDeviceSelectorState(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_BT_DEVICE_FOUND],
        message = LocalI18N.current[I18N_R.UI_CONDITIONS_BT_DEVICE_FOUND_TIP],
        showTimeoutSelector = true,
        onSelected = { name, address, timeout ->
            addCondition(
                Condition.RequireBTDeviceFound(
                    device = name,
                    address = address,
                    timeout = timeout
                )
            )
        }
    )
    BTDeviceSelectorDialog(state = btFoundSelectorState)

    val mobileDataEnableSelectorState = rememberSIMSlotSelectorState(onSelected = {
        addCondition(Condition.RequireMobileDataEnabled(slot = it))
    })
    SIMSlotSelectorDialog(state = mobileDataEnableSelectorState)

    val screenLockStateSelectorState = rememberMenuDialogState<Boolean>(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_SCREEN_LOCK_STATE],
        menuItems = listOf(
            MenuDialogItem(
                id = false.toString(),
                title = LocalI18N.current[I18N_R.UI_CONDITIONS_SCREEN_LOCKED_Y],
            ),
            MenuDialogItem(
                id = true.toString(),
                title = LocalI18N.current[I18N_R.UI_CONDITIONS_SCREEN_UNLOCKED],
            ),
        ),
        onItemSelected = { _, id ->
            addCondition(Condition.KeyguardIsLocked(isInvert = id.toBoolean()))
        }
    )
    MenuDialog(state = screenLockStateSelectorState)

    val screenStayAwakeStateSelectorState = rememberMenuDialogState<Boolean>(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_SCREEN_STAY_AWAKE_STATE],
        menuItems = listOf(
            MenuDialogItem(
                id = false.toString(),
                title = LocalI18N.current[I18N_R.UI_CONDITIONS_SCREEN_STAY_AWAKE_ON],
            ),
            MenuDialogItem(
                id = true.toString(),
                title = LocalI18N.current[I18N_R.UI_CONDITIONS_SCREEN_STAY_AWAKE_OFF],
            ),
        ),
        onItemSelected = { _, id ->
            addCondition(Condition.ScreenStayAwake(isInvert = id.toBoolean()))
        }
    )
    MenuDialog(state = screenStayAwakeStateSelectorState)

    val vpnStateSelectorState = rememberMenuDialogState<Boolean>(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_VPN_STATE],
        menuItems = listOf(
            MenuDialogItem(
                id = false.toString(),
                title = LocalI18N.current[I18N_R.UI_CONDITIONS_VPN_CONNECTED],
            ),
            MenuDialogItem(
                id = true.toString(),
                title = LocalI18N.current[I18N_R.UI_CONDITIONS_VPN_DISCONNECTED],
            ),
        ),
        onItemSelected = { _, id ->
            addCondition(Condition.VPNIsConnected(isInvert = id.toBoolean()))
        }
    )
    MenuDialog(state = vpnStateSelectorState)

    val screenOrientationStateSelectorState = rememberMenuDialogState<Boolean>(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_SCREEN_ORIENTATION],
        menuItems = listOf(
            MenuDialogItem(
                id = false.toString(),
                title = LocalI18N.current[I18N_R.UI_CONDITIONS_SCREEN_ORIENTATION_PORTRAIT],
            ),
            MenuDialogItem(
                id = true.toString(),
                title = LocalI18N.current[I18N_R.UI_CONDITIONS_SCREEN_ORIENTATION_LANDSCAPE],
            ),
        ),
        onItemSelected = { _, id ->
            addCondition(Condition.ScreenOrientationIsPort(isInvert = id.toBoolean()))
        }
    )
    MenuDialog(state = screenOrientationStateSelectorState)

    val callStateSelectorState = rememberMenuDialogState<Boolean>(
        title = LocalI18N.current[I18N_R.UI_CONDITION_CALL_STATE],
        menuItems = listOf(
            MenuDialogItem(
                id = false.toString(),
                title = LocalI18N.current[I18N_R.UI_CONDITION_CALL_STATE_INCALL],
            ),
            MenuDialogItem(
                id = true.toString(),
                title = LocalI18N.current[I18N_R.UI_CONDITION_CALL_STATE_NOT_INCALL],
            ),
        ),
        onItemSelected = { _, id ->
            addCondition(Condition.IsInCall(isInvert = id.toBoolean()))
        }
    )
    MenuDialog(state = callStateSelectorState)

    val ringingStateSelectorState = rememberMenuDialogState<Boolean>(
        title = LocalI18N.current[I18N_R.UI_CONDITION_RINGING_STATE],
        menuItems = listOf(
            MenuDialogItem(
                id = false.toString(),
                title = LocalI18N.current[I18N_R.UI_CONDITION_RINGING_STATE_RINGING],
            ),
            MenuDialogItem(
                id = true.toString(),
                title = LocalI18N.current[I18N_R.UI_CONDITION_RINGING_STATE_NOT_RINGING],
            ),
        ),
        onItemSelected = { _, id ->
            addCondition(Condition.IsRinging(isInvert = id.toBoolean()))
        }
    )
    MenuDialog(state = ringingStateSelectorState)


    val headsetPlugMenuState = rememberMenuDialogState<String>(
        title = LocalI18N.current[I18N_R.UI_FACT_HEADSET_PLUG],
        menuItems = listOf(
            MenuDialogItem(id = "in", title = LocalI18N.current[I18N_R.UI_CONDITION_HEADSET_PLUGGED]),
            MenuDialogItem(id = "out", title = LocalI18N.current[I18N_R.UI_CONDITION_HEADSET_UNPLUGGED]),
        ),
        onItemSelected = { _, id ->
            addCondition(Condition.IsHeadsetPlug(isPlug = id == "in"))
        })
    MenuDialog(state = headsetPlugMenuState)

    val screenRotateSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current[I18N_R.UI_FACT_SCREEN_ROTATE],
        menuItems = ScreenRotateDegree.entries
            .filterNot { it == ScreenRotateDegree.UNRECOGNIZED || it == ScreenRotateDegree.ScreenRotateDegree_Any }
            .map {
                MenuDialogItem(it.name, title = LocalI18N.current[it.factLabelKey()])
            },
        onItemSelected = { data, id ->
            val degree = ScreenRotateDegree.valueOf(id)
            addCondition(Condition.RequireScreenRotate(degree = degree))
        }
    )
    MenuDialog(state = screenRotateSelectorState)

    val windowRotationSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current[I18N_R.UI_FACT_WINDOW_ROTATE],
        menuItems = ScreenRotateDegree.entries
            .filterNot { it == ScreenRotateDegree.UNRECOGNIZED || it == ScreenRotateDegree.ScreenRotateDegree_Any }
            .map {
                MenuDialogItem(it.name, title = LocalI18N.current[it.factLabelKey()])
            },
        onItemSelected = { data, id ->
            val degree = ScreenRotateDegree.valueOf(id)
            addCondition(Condition.RequireWindowRotation(degree = degree))
        }
    )
    MenuDialog(state = windowRotationSelectorState)

    val sensorOffSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current[I18N_R.UI_ACTION_SENSORS_OFF],
        menuItems = listOf(
            MenuDialogItem(
                "on",
                LocalI18N.current[I18N_R.UI_COMMON_ON],
            ),
            MenuDialogItem(
                "off",
                LocalI18N.current[I18N_R.UI_COMMON_OFF],
            )
        ),
        onItemSelected = { data, id ->
            val isOn = "on" == id
            addCondition(Condition.RequireSensorOff(isRequireOn = isOn))
        }
    )
    MenuDialog(state = sensorOffSelectorState)


    val tileStateSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_TILE_STATE],
        menuItems = QSTileState.entries
            .filterNot { it == QSTileState.UNRECOGNIZED }
            .map {
                MenuDialogItem(it.name, title = LocalI18N.current[it.labelKey()])
            },
        onItemSelected = { data, id ->
            val state = QSTileState.valueOf(id)
            addCondition(
                Condition.RequireTileState(
                    state = state,
                    spec = data.fallbackOnEmpty("ERROR")
                )
            )
        }
    )
    MenuDialog(state = tileStateSelectorState)

    val targetTileSelectorState = rememberQSTileSelectorState(
        title = LocalI18N.current[I18N_R.UI_CONDITIONS_TILE_STATE],
        onSelected = {
            tileStateSelectorState.show(it.tileSpec)
        }
    )
    QSTileSelectorDialog(state = targetTileSelectorState)

    val apmModeSelectorState = rememberOnOffAnyStateSelectorState(
        title = LocalI18N.current[I18N_R.UI_CONDITION_APM_MODE],
        options = listOf(OnOffAny.On, OnOffAny.Off),
        selected = {
            addCondition(Condition.RequireAPMMode(isEnableAPM = (it == OnOffAny.On)))
        })
    OnOffAnyStateSelectorDialog(state = apmModeSelectorState)

    val imeVisibilitySelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current[I18N_R.UI_CONDITION_IME_VISIBILITY],
        menuItems = listOf(
            MenuDialogItem(
                id = true.toString(),
                title = LocalI18N.current[I18N_R.UI_FACT_IME_VISIBILITY_SHOW],
            ),
            MenuDialogItem(
                id = false.toString(),
                title = LocalI18N.current[I18N_R.UI_FACT_IME_VISIBILITY_HIDE],
            ),
        ),
        onItemSelected = { data, id ->
            val isShow = id.toBoolean()
            addCondition(Condition.RequireIMEVisibility(isShown = isShow))
        }
    )
    MenuDialog(state = imeVisibilitySelectorState)

    val notificationPanelExpandState = rememberMenuDialogState<String>(
        title = LocalI18N.current[I18N_R.UI_CONDITION_N_PANEL_EXPANDED],
        menuItems = listOf(
            MenuDialogItem(
                id = true.toString(),
                title = LocalI18N.current[I18N_R.UI_CONDITION_N_PANEL_EXPANDED_Y],
            ),
            MenuDialogItem(
                id = false.toString(),
                title = LocalI18N.current[I18N_R.UI_CONDITION_N_PANEL_EXPANDED_N],
            ),
        ),
        onItemSelected = { _, id ->
            val isExpand = id.toBoolean()
            addCondition(Condition.RequireNotificationPanelExpanded(isExpand = isExpand))
        }
    )
    MenuDialog(state = notificationPanelExpandState)

    val ringerModeSelectDialogState = rememberSelectionDialogState(
        title = LocalI18N.current[I18N_R.UI_CONDITION_RINGER_MODE],
        message = null,
        selectionLabel2Ids = listOf(RingerMode.normal, RingerMode.vibrate, RingerMode.silent).map {
            LocalI18N.current[it.labelKey()] to it.name
        },
        onSelect = {
            val mode: RingerMode = RingerMode.valueOf(it)
            addCondition(Condition.RequireRingerMode(mode = mode))
        }
    )
    SelectionDialog(state = ringerModeSelectDialogState)

    val screenOnTimeState = rememberEvaluateScreenOnTimeSelectorState {
        addCondition(it)
    }
    EvaluateScreenOnTimeSelectorDialog(screenOnTimeState)

    return { source ->
        when (source.clazz) {
            Condition.IsHeadsetPlug::class -> {
                headsetPlugMenuState.show()
                true
            }

            Condition.ScreenIsOn::class -> {
                addCondition(Condition.ScreenIsOn())
                true
            }

            Condition.ScreenStayAwake::class -> {
                screenStayAwakeStateSelectorState.show(source.getConditionAs<Condition.ScreenStayAwake>()?.isInvert)
                true
            }

            Condition.VPNIsConnected::class -> {
                vpnStateSelectorState.show(source.getConditionAs<Condition.VPNIsConnected>()?.isInvert)
                true
            }

            Condition.BatteryPercent::class -> {
                batteryPercentDialog.show(source.getConditionAs<Condition.BatteryPercent>()?.let {
                    IntOpSelectorRes(it.value, it.op)
                })
                true
            }

            Condition.AvailableMemory::class -> {
                availableMemoryDialog.show(source.getConditionAs<Condition.AvailableMemory>()?.let {
                    IntOpSelectorRes(it.valueMb, it.op)
                })
                true
            }

            Condition.ConnectedWifiSignal::class -> {
                wifiSignalSelectorState.show(
                    source.getConditionAs<Condition.ConnectedWifiSignal>()?.let {
                        IntOpSelectorRes(it.level, it.op)
                    })
                true
            }

            Condition.ChargeState::class -> {
                chargeStateSelectorState.show()
                true
            }

            Condition.PlugState::class -> {
                plugStateSelectorState.show()
                true
            }

            Condition.RequireWifiConnected::class -> {
                wifiConnectedToSelectorState.show(source.getConditionAs<Condition.RequireWifiConnected>()?.requiredSSID)
                true
            }

            Condition.RequireWifiDisconnected::class -> {
                addCondition(Condition.RequireWifiDisconnected())
                true
            }

            Condition.RequireBTDeviceFound::class -> {
                val ori = source.getConditionAs<Condition.RequireBTDeviceFound>()
                btFoundSelectorState.show(ori?.device, ori?.address, ori?.timeout)
                true
            }

            Condition.RequireBTConnected::class -> {
                btConnectedSelectorState.show(source.getConditionAs<Condition.RequireBTConnected>()?.requiredDevice)
                true
            }

            Condition.RequireBTDisconnected::class -> {
                addCondition(Condition.RequireBTDisconnected())
                true
            }

            Condition.TimeInRange::class -> {
                timeInRangeSelectorState.show(source.getConditionAs<Condition.TimeInRange>()?.range)
                true
            }

            Condition.CalendarDayMatch::class -> {
                calendarDayMatchSelectDialogState.show()
                true
            }

            Condition.RequireMobileDataEnabled::class -> {
                mobileDataEnableSelectorState.show()
                true
            }

            Condition.KeyguardIsLocked::class -> {
                screenLockStateSelectorState.show(source.getConditionAs<Condition.KeyguardIsLocked>()?.isInvert)
                true
            }

            Condition.ScreenOrientationIsPort::class -> {
                screenOrientationStateSelectorState.show(source.getConditionAs<Condition.ScreenOrientationIsPort>()?.isInvert)
                true
            }

            Condition.IsInCall::class -> {
                callStateSelectorState.show(source.getConditionAs<Condition.IsInCall>()?.isInvert)
                true
            }

            Condition.IsRinging::class -> {
                ringingStateSelectorState.show(source.getConditionAs<Condition.IsRinging>()?.isInvert)
                true
            }

            Condition.RequireScreenRotate::class -> {
                screenRotateSelectorState.show()
                true
            }

            Condition.RequireWindowRotation::class -> {
                windowRotationSelectorState.show()
                true
            }

            Condition.RequireSensorOff::class -> {
                sensorOffSelectorState.show()
                true
            }

            Condition.RequireTileState::class -> {
                targetTileSelectorState.show()
                true
            }

            Condition.RequireAPMMode::class -> {
                apmModeSelectorState.show()
                true
            }

            Condition.RequireIMEVisibility::class -> {
                imeVisibilitySelectorState.show()
                true
            }

            Condition.RequireNotificationPanelExpanded::class -> {
                notificationPanelExpandState.show()
                true
            }

            Condition.RequireRingerMode::class -> {
                ringerModeSelectDialogState.show()
                true
            }

            Condition.EvaluateScreenOnTime::class -> {
                screenOnTimeState.show()
                true
            }

            else -> false
        }
    }
}

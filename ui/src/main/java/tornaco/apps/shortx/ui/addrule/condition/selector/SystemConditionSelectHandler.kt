package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.common.QSTileState
import tornaco.apps.shortx.core.proto.common.RingerMode
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.condition.PlugType
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectSource
import tornaco.apps.shortx.ui.addrule.condition.EvaluateGlobalVarSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.IntOpSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.IntOpSelectorRes
import tornaco.apps.shortx.ui.addrule.condition.SIMSlotSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.getConditionAs
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.toProtoCondition
import tornaco.apps.shortx.ui.addrule.condition.rememberEvaluateGlobalVarSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberIntOpSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberSIMSlotSelectorState
import tornaco.apps.shortx.ui.addrule.fact.mapper.factLabelKey
import tornaco.apps.shortx.ui.addrule.fact.selector.BTDeviceSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.OnOffAnyStateSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.QSTileSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.WifiSSIDSelectorDialog
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

@Composable
fun systemConditionSelectHandler(addCondition: (Condition) -> Unit): (ConditionSelectSource) -> Boolean {
    val evaluateGlobalVarSelectorDialogState = rememberEvaluateGlobalVarSelectorState {
        addCondition(it)
    }
    EvaluateGlobalVarSelectorDialog(state = evaluateGlobalVarSelectorDialogState)

    val batteryPercentDialog = rememberIntOpSelectorState(
        title = LocalI18N.current["ui.conditions.battery.percent"],
        valueTitle = LocalI18N.current["ui.conditions.battery.percent.value"],
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

    val wifiSignalSelectorState = rememberIntOpSelectorState(
        title = LocalI18N.current["ui.conditions.wifi.signal"],
        valueTitle = LocalI18N.current["ui.conditions.wifi.signal.level", mapOf(
            "max" to shortXManager.getMaxSignalLevel().toString()
        )],
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

    val chargeStateSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.conditions.charge.state"],
        menuItems = listOf(
            MenuDialogItem(
                id = "charge",
                title = LocalI18N.current["ui.conditions.charge.state.charge"],
            ),
            MenuDialogItem(
                id = "notCharge",
                title = LocalI18N.current["ui.conditions.charge.state.not.charge"],
            ),
        ),
        onItemSelected = { data, id ->
            addCondition(Condition.ChargeState(requireIsCharge = id == "charge"))
        }
    )
    MenuDialog(state = chargeStateSelectorState)

    val plugStateSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.conditions.plug.state"],
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
        title = LocalI18N.current["ui.conditions.wifi.connected"],
        message = LocalI18N.current["ui.conditions.wifi.connected.target"],
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
        title = LocalI18N.current["ui.conditions.bt.connected"],
        onSelected = { name, address, timeout ->
            addCondition(Condition.RequireBTConnected(requiredDevice = name))
        }
    )
    BTDeviceSelectorDialog(state = btConnectedSelectorState)

    val btFoundSelectorState = rememberBTDeviceSelectorState(
        title = LocalI18N.current["ui.conditions.bt.device.found"],
        message = LocalI18N.current["ui.conditions.bt.device.found.tip"],
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


    val headsetPlugMenuState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.fact.headset.plug"],
        menuItems = listOf(
            MenuDialogItem(id = "in", title = LocalI18N.current["ui.condition.headset.plugged"]),
            MenuDialogItem(id = "out", title = LocalI18N.current["ui.condition.headset.unplugged"]),
        ),
        onItemSelected = { _, id ->
            addCondition(Condition.IsHeadsetPlug(isPlug = id == "in"))
        })
    MenuDialog(state = headsetPlugMenuState)

    val screenRotateSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.fact.screen.rotate"],
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
        title = LocalI18N.current["ui.fact.window.rotate"],
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
        title = LocalI18N.current["ui.action.sensors.off"],
        menuItems = listOf(
            MenuDialogItem(
                "on",
                LocalI18N.current["ui.common.on"],
            ),
            MenuDialogItem(
                "off",
                LocalI18N.current["ui.common.off"],
            )
        ),
        onItemSelected = { data, id ->
            val isOn = "on" == id
            addCondition(Condition.RequireSensorOff(isRequireOn = isOn))
        }
    )
    MenuDialog(state = sensorOffSelectorState)


    val tileStateSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.conditions.tile.state"],
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
        title = LocalI18N.current["ui.conditions.tile.state"],
        onSelected = {
            tileStateSelectorState.show(it.tileSpec)
        }
    )
    QSTileSelectorDialog(state = targetTileSelectorState)

    val apmModeSelectorState = rememberOnOffAnyStateSelectorState(
        title = LocalI18N.current["ui.condition.apm.mode"],
        options = listOf(OnOffAny.On, OnOffAny.Off),
        selected = {
            addCondition(Condition.RequireAPMMode(isEnableAPM = (it == OnOffAny.On)))
        })
    OnOffAnyStateSelectorDialog(state = apmModeSelectorState)

    val imeVisibilitySelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.condition.ime.visibility"],
        menuItems = listOf(
            MenuDialogItem(
                id = true.toString(),
                title = LocalI18N.current["ui.fact.ime.visibility.show"],
            ),
            MenuDialogItem(
                id = false.toString(),
                title = LocalI18N.current["ui.fact.ime.visibility.hide"],
            ),
        ),
        onItemSelected = { data, id ->
            val isShow = id.toBoolean()
            addCondition(Condition.RequireIMEVisibility(isShown = isShow))
        }
    )
    MenuDialog(state = imeVisibilitySelectorState)

    val notificationPanelExpandState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.condition.n.panel.expanded"],
        menuItems = listOf(
            MenuDialogItem(
                id = true.toString(),
                title = LocalI18N.current["ui.condition.n.panel.expanded.y"],
            ),
            MenuDialogItem(
                id = false.toString(),
                title = LocalI18N.current["ui.condition.n.panel.expanded.n"],
            ),
        ),
        onItemSelected = { _, id ->
            val isExpand = id.toBoolean()
            addCondition(Condition.RequireNotificationPanelExpanded(isExpand = isExpand))
        }
    )
    MenuDialog(state = notificationPanelExpandState)

    val ringerModeSelectDialogState = rememberSelectionDialogState(
        title = LocalI18N.current["ui.condition.ringer.mode"],
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

            Condition.VPNIsConnected::class -> {
                addCondition(Condition.VPNIsConnected())
                true
            }

            Condition.BatteryPercent::class -> {
                batteryPercentDialog.show(source.getConditionAs<Condition.BatteryPercent>()?.let {
                    IntOpSelectorRes(it.value, it.op)
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

            Condition.RequireMobileDataEnabled::class -> {
                mobileDataEnableSelectorState.show()
                true
            }

            Condition.KeyguardIsLocked::class -> {
                addCondition(Condition.KeyguardIsLocked())
                true
            }

            Condition.ScreenOrientationIsPort::class -> {
                addCondition(Condition.ScreenOrientationIsPort())
                true
            }

            Condition.IsInCall::class -> {
                addCondition(Condition.IsInCall())
                true
            }

            Condition.IsRinging::class -> {
                addCondition(Condition.IsRinging())
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

            else -> false
        }
    }
}
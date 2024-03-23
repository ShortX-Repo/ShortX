package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import tornaco.apps.shortx.core.alarm.toLocalTime
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.proto.common.CallState
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.fact.mapper.factLabelKey
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.selector.AdvancedKeyEventSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.AlarmSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.AppOpSelectorData
import tornaco.apps.shortx.ui.addrule.fact.selector.AppOpSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.BTDeviceSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.EdgeGestureSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.FixedPeriodSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.KeyEventSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.OnOffAnyStateSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.PeriodSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.SystemSettingsUrlSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.WifiSSIDSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAdvancedKeyEventSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAlarmSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAppOpSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberBTDeviceSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberEdgeGestureSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberFixedInPeriodSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberKeyEventSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberOnOffAnyStateSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberRandomInPeriodSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberSystemSettingsUrlSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberWifiSSIDSelectorState
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.RegexTextInputDialog
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberRegexTextInputState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import java.time.LocalTime

@Composable
fun systemFactSelectHandler(addFact: (Fact) -> Unit): (FactSelectSource) -> Boolean {
    val keyEventSelectorState = rememberKeyEventSelectorState(selected = { code ->
        addFact(Fact.KeyEvent(code))
    })
    KeyEventSelector(keyEventSelectorState)

    val advKeyEventSelectorState = rememberAdvancedKeyEventSelectorState(selected = { event ->
        addFact(event)
    })
    AdvancedKeyEventSelector(advKeyEventSelectorState)

    val edgeGestureSelectorState = rememberEdgeGestureSelectorState(
        selected = { eg ->
            addFact(Fact.EdgeGesture(eg.first, eg.second, eg.third))
        },
        showEdgeHint = {
            shortXManager.showEdgeHint(it)
        })
    EdgeGestureSelector(edgeGestureSelectorState)

    val timeOfADaySelector = rememberAlarmSelectorState { alarm ->
        addFact(Fact.Alarm(timeOfADay = alarm.triggerAt, repeatDays = alarm.repeat))
    }
    AlarmSelector(timeOfADaySelector)

    val randomInPeriodSelectorState = rememberRandomInPeriodSelectorState(selected = {
        addFact(it)
    })
    PeriodSelector(state = randomInPeriodSelectorState)

    val fixedPeriodSelectorState = rememberFixedInPeriodSelectorState {
        addFact(it)
    }
    FixedPeriodSelector(fixedPeriodSelectorState)

    val wifiSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.wifi.state.changed"]) {
            addFact(Fact.WifiStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(wifiSelectorState)

    val headsetPlugMenuState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.fact.headset.plug"],
        menuItems = listOf(
            MenuDialogItem(id = "in", title = LocalI18N.current["ui.fact.headset.plug.in"]),
            MenuDialogItem(id = "out", title = LocalI18N.current["ui.fact.headset.plug.out"]),
        ),
        onItemSelected = { data, id ->
            addFact(Fact.HeadsetPlug(isPlug = id == "in"))
        })
    MenuDialog(state = headsetPlugMenuState)

    val wifiConnectedToSelectorState = rememberWifiSSIDSelectorState(
        title = LocalI18N.current["ui.fact.wifi.connect.to"],
        message = LocalI18N.current["ui.fact.wifi.connect.to.input.ssid"],
        loadSSID = {
            shortXManager.getWifiScanResults().filterNot { it.isBlank() }
        },
        showSymbolButton = false,
        onSelected = {
            addFact(Fact.WifiConnectedTo(ssidList = listOf(it)))
        }
    )
    WifiSSIDSelectorDialog(state = wifiConnectedToSelectorState)

    val wifiDisconnectedFromSelectorState = rememberWifiSSIDSelectorState(
        title = LocalI18N.current["ui.fact.wifi.disconnected.from"],
        message = LocalI18N.current["ui.fact.wifi.disconnected.from.input.ssid"],
        loadSSID = {
            shortXManager.getWifiScanResults().filterNot { it.isBlank() }
        },
        showSymbolButton = false,
        onSelected = {
            addFact(Fact.WifiDisconnectedFrom(ssidList = listOf(it)))
        }
    )
    WifiSSIDSelectorDialog(state = wifiDisconnectedFromSelectorState)

    val btSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.bt.state.changed"]) {
            addFact(Fact.BTStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(btSelectorState)

    val btConnectedToState = rememberBTDeviceSelectorState(
        title = LocalI18N.current["ui.fact.bt.connected.to"],
        onSelected = { name, address, timeout ->
            addFact(Fact.BTConnectedTo(devices = listOf(name)))
        }
    )
    BTDeviceSelectorDialog(state = btConnectedToState)

    val btDisConnectedFromState = rememberBTDeviceSelectorState(
        title = LocalI18N.current["ui.fact.bt.disconnected.from"],
        onSelected = { name, address, timeout ->
            addFact(Fact.BTDisconnectedFrom(devices = listOf(name)))
        }
    )
    BTDeviceSelectorDialog(state = btDisConnectedFromState)

    val dndSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.dnd.state.changed"]) {
            addFact(Fact.DNDStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(dndSelectorState)

    val apmSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.condition.apm.mode"]) {
            addFact(Fact.APMStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(apmSelectorState)

    val locationSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.location.state.changed"]) {
            addFact(Fact.LocationStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(locationSelectorState)

    val darkModeSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.dark.mode.state.changed"]) {
            addFact(Fact.DarkModeStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(darkModeSelectorState)

    val mobileDataSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.mobile.data.state.changed"]) {
            addFact(Fact.MobileDataStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(mobileDataSelectorState)

    val flashLightSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.flash.light.state.changed"]) {
            addFact(Fact.FlashLightStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(flashLightSelectorState)

    val nfcSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.nfc.state.changed"]) {
            addFact(Fact.NFCStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(nfcSelectorState)

    val hotspotSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.hotspot.state.changed"]) {
            addFact(Fact.HotSpotStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(hotspotSelectorState)

    val batteryLevelSelectorState = rememberTextInputState(
        title = LocalI18N.current["ui.fact.battery.level.changed"],
        message = LocalI18N.current["ui.fact.battery.level.changed.level"],
        inputValidator = {
            val isOK = (it.isNotEmpty() && it.isDigitsOnly() && ((it.toIntOrNull() ?: 0) <= 100))
            isOK.select(InputValidateResult.OK, InputValidateResult.Error(""))
        },
        showSymbolButton = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onSelected = { levelString ->
            val level = levelString.toIntOrNull()
            level?.let {
                addFact(Fact.BatteryLevelChanged(level = it))
            }
        }
    )
    TextInputDialog(state = batteryLevelSelectorState)

    val batteryTempSelectorState = rememberTextInputState(
        title = LocalI18N.current["ui.fact.battery.temp.changed"],
        message = LocalI18N.current["ui.fact.battery.temp.changed.temp"],
        inputValidator = {
            (it.isNotEmpty() && ((it.toFloatOrNull() ?: 0f) <= 100f)).select(
                InputValidateResult.OK,
                InputValidateResult.Error("")
            )
        },
        showSymbolButton = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onSelected = { levelString ->
            val level = levelString.toFloatOrNull()
            level?.let {
                addFact(Fact.BatteryTemperatureChanged(temp = it))
            }
        }
    )
    TextInputDialog(state = batteryTempSelectorState)

    val broadcastSelectorState = rememberTextInputState(
        title = LocalI18N.current["ui.fact.broadcast"],
        message = LocalI18N.current["ui.common.action"],
        showSymbolButton = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        onSelected = { actionString ->
            addFact(Fact.Broadcast(actions = listOf(actionString)))
        }
    )
    TextInputDialog(state = broadcastSelectorState)


    val mediaInsertSelectorState = rememberRegexTextInputState(
        title = LocalI18N.current["ui.fact.media.insert"],
        message = "${LocalI18N.current["ui.fact.media.insert.summary", mapOf("mediaPkgName" to PackageNames.mediaProvider)]} \n\n ${LocalI18N.current["ui.fact.media.insert.input.path"]}",
        showSymbolButton = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        onSelected = { pathString, options ->
            addFact(
                Fact.MediaStoreInsert(
                    filterPathRegex = pathString.fallbackOnEmpty(".*"),
                    matchOptions = options
                )
            )
        }
    )
    RegexTextInputDialog(state = mediaInsertSelectorState)

    val onStartOpSelectorState = rememberAppOpSelectorState(
        title = LocalI18N.current["ui.fact.op.start"],
        message = null,
        onSelected = {
            addFact(Fact.OnStartOp(apps = it.appList, pkgSets = it.pkgSetList, codes = it.opCodes))
        })
    AppOpSelectorDialog(state = onStartOpSelectorState)

    val onFinishOpSelectorState = rememberAppOpSelectorState(
        title = LocalI18N.current["ui.fact.op.finish"],
        message = null,
        onSelected = {
            addFact(Fact.OnFinishOp(apps = it.appList, pkgSets = it.pkgSetList, codes = it.opCodes))
        })
    AppOpSelectorDialog(state = onFinishOpSelectorState)

    val systemSettingsChangeDialogState = rememberSystemSettingsUrlSelectorState(onSelected = {
        addFact(it)
    })
    SystemSettingsUrlSelectorDialog(state = systemSettingsChangeDialogState)

    val callStateSelectState =
        rememberMenuDialogState<String>(title = LocalI18N.current["ui.fact.call.state.changed"],
            menuItems =
            CallState.entries.filterNot { it == CallState.UNRECOGNIZED }.map {
                MenuDialogItem(
                    it.name,
                    LocalI18N.current["ui.fact.call.state.${it.name.lowercase()}"],
                )
            }, onItemSelected = { _, id ->
                addFact(Fact.CallStateChanged(callState = CallState.valueOf(id)))
            }
        )
    MenuDialog(state = callStateSelectState)

    val clipboardRegexDialogState = rememberRegexTextInputState(
        title = LocalI18N.current["ui.fact.clipboard.content.changed"],
        message = LocalI18N.current["ui.fact.clipboard.content.changed.regex"],
        inputValidator = {
            InputValidateResult.OK
        },
        onSelected = { text, options ->
            addFact(Fact.ClipboardContentChanged(content = text, options = options))
        }
    )
    RegexTextInputDialog(state = clipboardRegexDialogState)

    val screenRotateSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.fact.screen.rotate"],
        menuItems = ScreenRotateDegree.entries.filterNot { it == ScreenRotateDegree.UNRECOGNIZED }
            .map {
                MenuDialogItem(it.name, title = LocalI18N.current[it.factLabelKey()])
            },
        onItemSelected = { data, id ->
            val degree = ScreenRotateDegree.valueOf(id)
            addFact(Fact.ScreenRotate(degree = degree))
        }
    )
    MenuDialog(state = screenRotateSelectorState)

    val windowRotateSelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.fact.window.rotate"],
        menuItems = ScreenRotateDegree.entries.filterNot { it == ScreenRotateDegree.UNRECOGNIZED }
            .map {
                MenuDialogItem(it.name, title = LocalI18N.current[it.factLabelKey()])
            },
        onItemSelected = { data, id ->
            val degree = ScreenRotateDegree.valueOf(id)
            addFact(Fact.WindowRotationChange(degree = degree))
        }
    )
    MenuDialog(state = windowRotateSelectorState)


    val imeVisibilitySelectorState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.fact.ime.visibility.change"],
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
            addFact(Fact.IMEVisibilityChange(isShown = isShow))
        }
    )
    MenuDialog(state = imeVisibilitySelectorState)

    val connectedWifiSignalState = rememberTextInputState(
        title = LocalI18N.current["ui.fact.wifi.signal.changed"],
        message = LocalI18N.current["ui.fact.wifi.signal.changed.message", mapOf(
            "max" to shortXManager.getMaxSignalLevel().toString()
        )]
    ) {
        addFact(Fact.ConnectedWifiSignalLevelChanged(level = it.toIntOrNull() ?: 0, rssi = 0))
    }
    TextInputDialog(state = connectedWifiSignalState)

    return { source ->
        when (source.clazz) {
            Fact.KeyEvent::class -> {
                keyEventSelectorState.show()
                true
            }

            Fact.AdvancedKeyEvent::class -> {
                advKeyEventSelectorState.show(source.getFactAs())
                true
            }

            Fact.EdgeGesture::class -> {
                edgeGestureSelectorState.show(
                    edge = source.getFactAs<Fact.EdgeGesture>()?.edge,
                    gesture = source.getFactAs<Fact.EdgeGesture>()?.gesture,
                    isIntercept = source.getFactAs<Fact.EdgeGesture>()?.isIntercept,
                )
                true
            }

            Fact.ScreenOff::class -> {
                addFact(Fact.ScreenOff())
                true
            }

            Fact.ScreenOn::class -> {
                addFact(Fact.ScreenOn())
                true
            }

            Fact.UserPresent::class -> {
                addFact(Fact.UserPresent())
                true
            }

            Fact.UserPresentAtTheFirstTime::class -> {
                addFact(Fact.UserPresentAtTheFirstTime())
                true
            }

            Fact.SystemReady::class -> {
                addFact(Fact.SystemReady())
                true
            }

            Fact.Alarm::class -> {

                val localTime =
                    source.getFactAs<Fact.Alarm>()?.timeOfADay?.toLocalTime() ?: LocalTime.now()
                val repeat = source.getFactAs<Fact.Alarm>()?.repeatDays?.daysList ?: emptyList()
                timeOfADaySelector.show(localTime, repeat)
                true
            }

            Fact.RandomInPeriod::class -> {
                randomInPeriodSelectorState.show(source.getFactAs())
                true
            }

            Fact.FixedInPeriod::class -> {
                fixedPeriodSelectorState.show(source.getFactAs())
                true
            }

            Fact.WifiStatusChanged::class -> {
                wifiSelectorState.show()
                true
            }

            Fact.WifiConnectedTo::class -> {
                wifiConnectedToSelectorState.show(source.getFactAs<Fact.WifiConnectedTo>()?.ssidList?.let {
                    if (it.isEmpty()) null else it[0]
                })
                true
            }

            Fact.WifiDisconnectedFrom::class -> {
                wifiDisconnectedFromSelectorState.show(source.getFactAs<Fact.WifiDisconnectedFrom>()?.ssidList?.let {
                    if (it.isEmpty()) null else it[0]
                })
                true
            }

            Fact.ConnectedWifiSignalLevelChanged::class -> {
                connectedWifiSignalState.show(source.getFactAs<Fact.ConnectedWifiSignalLevelChanged>()?.level?.toString())
                true
            }

            Fact.BTStatusChanged::class -> {
                btSelectorState.show()
                true
            }

            Fact.BTConnectedTo::class -> {
                btConnectedToState.show(source.getFactAs<Fact.BTConnectedTo>()?.devices?.let {
                    if (it.isEmpty()) null else it[0]
                })
                true
            }

            Fact.BTDisconnectedFrom::class -> {
                btDisConnectedFromState.show(source.getFactAs<Fact.BTDisconnectedFrom>()?.devices?.let {
                    if (it.isEmpty()) null else it[0]
                })
                true
            }

            Fact.DNDStatusChanged::class -> {
                dndSelectorState.show()
                true
            }

            Fact.APMStatusChanged::class -> {
                apmSelectorState.show()
                true
            }

            Fact.DarkModeStatusChanged::class -> {
                darkModeSelectorState.show()
                true
            }

            Fact.FlashLightStatusChanged::class -> {
                flashLightSelectorState.show()
                true
            }

            Fact.HotSpotStatusChanged::class -> {
                hotspotSelectorState.show()
                true
            }

            Fact.LocationStatusChanged::class -> {
                locationSelectorState.show()
                true
            }

            Fact.MobileDataStatusChanged::class -> {
                mobileDataSelectorState.show()
                true
            }

            Fact.NFCStatusChanged::class -> {
                nfcSelectorState.show()
                true
            }

            Fact.BatteryLevelChanged::class -> {
                batteryLevelSelectorState.show(source.getFactAs<Fact.BatteryLevelChanged>()?.level?.toString())
                true
            }

            Fact.BatteryTemperatureChanged::class -> {
                batteryTempSelectorState.show(source.getFactAs<Fact.BatteryTemperatureChanged>()?.temp?.toString())
                true
            }

            Fact.ChargerPlug::class -> {
                addFact(Fact.ChargerPlug())
                true
            }

            Fact.ChargerUnplug::class -> {
                addFact(Fact.ChargerUnplug())
                true
            }

            Fact.Broadcast::class -> {
                broadcastSelectorState.show(source.getFactAs<Fact.Broadcast>()?.actions?.let {
                    if (it.isEmpty()) null else it[0]
                })
                true
            }

            Fact.VPNConnected::class -> {
                addFact(Fact.VPNConnected())
                true
            }

            Fact.VPNDisconnected::class -> {
                addFact(Fact.VPNDisconnected())
                true
            }

            Fact.MediaStoreInsert::class -> {
                mediaInsertSelectorState.show(
                    initialValue = source.getFactAs<Fact.MediaStoreInsert>()?.filterPathRegex,
                    options = source.getFactAs<Fact.MediaStoreInsert>()?.matchOptions
                )
                true
            }

            Fact.OnStartOp::class -> {
                onStartOpSelectorState.show(
                    AppOpSelectorData(
                        source.getFactAs<Fact.OnStartOp>()?.apps ?: emptyList(),
                        source.getFactAs<Fact.OnStartOp>()?.pkgSets ?: emptyList(),
                        source.getFactAs<Fact.OnStartOp>()?.codes ?: emptyList(),
                    )
                )
                true
            }

            Fact.OnFinishOp::class -> {
                onFinishOpSelectorState.show(
                    AppOpSelectorData(
                        source.getFactAs<Fact.OnFinishOp>()?.apps ?: emptyList(),
                        source.getFactAs<Fact.OnFinishOp>()?.pkgSets ?: emptyList(),
                        source.getFactAs<Fact.OnFinishOp>()?.codes ?: emptyList(),
                    )
                )
                true
            }

            Fact.SystemSettingsChanged::class -> {
                systemSettingsChangeDialogState.show(
                    source.getFactAs()
                )
                true
            }

            Fact.CallStateChanged::class -> {
                callStateSelectState.show()
                true
            }

            Fact.ClipboardContentChanged::class -> {
                clipboardRegexDialogState.show(
                    initialValue = source.getFactAs<Fact.ClipboardContentChanged>()?.content ?: "",
                    options = source.getFactAs<Fact.ClipboardContentChanged>()?.options
                )
                true
            }

            Fact.HeadsetPlug::class -> {
                headsetPlugMenuState.show()
                true
            }

            Fact.ScreenRotate::class -> {
                screenRotateSelectorState.show()
                true
            }

            Fact.WindowRotationChange::class -> {
                windowRotateSelectorState.show()
                true
            }

            Fact.IMEVisibilityChange::class -> {
                imeVisibilitySelectorState.show()
                true
            }


            else -> false
        }
    }
}

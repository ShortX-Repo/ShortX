package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.selector.AdvancedKeyEventSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.AlarmSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.EdgeGestureSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.KeyEventSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.OnOffAnyStateSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.PeriodSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAdvancedKeyEventSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAlarmSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberEdgeGestureSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberKeyEventSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberOnOffAnyStateSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberPeriodSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.TextInputDialog
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
            addFact(Fact.EdgeGesture(eg.first, eg.second))
        },
        showEdgeHint = {
            shortXManager.showEdgeHint(it)
        })
    EdgeGestureSelector(edgeGestureSelectorState)

    val timeOfADaySelector = rememberAlarmSelectorState(
        initialRepeat = emptyArray(),
        initialTime = LocalTime.now()
    ) { alarm ->
        addFact(Fact.Alarm(timeOfADay = alarm.triggerAt, repeatDays = alarm.repeat))
    }
    AlarmSelector(timeOfADaySelector)

    val randomInPeriodSelectorState = rememberPeriodSelectorState(selected = {
        addFact(it)
    })
    PeriodSelector(state = randomInPeriodSelectorState)

    val wifiSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.wifi.state.changed"]) {
            addFact(Fact.WifiStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(wifiSelectorState)

    val wifiConnectedToSelectorState = rememberTextInputState(
        title = LocalI18N.current["ui.fact.wifi.connect.to"],
        message = LocalI18N.current["ui.fact.wifi.connect.to.input.ssid"],
        showSymbolButton = false,
        onSelected = {
            addFact(Fact.WifiConnectedTo(ssidList = listOf(it)))
        }
    )
    TextInputDialog(state = wifiConnectedToSelectorState)

    val wifiDisconnectedFromSelectorState = rememberTextInputState(
        title = LocalI18N.current["ui.fact.wifi.disconnected.from"],
        message = LocalI18N.current["ui.fact.wifi.disconnected.from.input.ssid"],
        showSymbolButton = false,
        onSelected = {
            addFact(Fact.WifiDisconnectedFrom(ssidList = listOf(it)))
        }
    )
    TextInputDialog(state = wifiDisconnectedFromSelectorState)

    val btSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.bt.state.changed"]) {
            addFact(Fact.BTStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(btSelectorState)

    val dndSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.dnd.state.changed"]) {
            addFact(Fact.DNDStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(dndSelectorState)

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
            (it.isNotEmpty() && it.isDigitsOnly() && ((it.toIntOrNull() ?: 0) <= 100)) to ""
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
            (it.isNotEmpty() && ((it.toFloatOrNull() ?: 0f) <= 100f)) to ""
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
        inputValidator = {
            (it.isNotEmpty()) to ""
        },
        showSymbolButton = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        onSelected = { actionString ->
            addFact(Fact.Broadcast(actions = listOf(actionString)))
        }
    )
    TextInputDialog(state = broadcastSelectorState)


    val mediaInsertSelectorState = rememberTextInputState(
        title = LocalI18N.current["ui.fact.media.insert"],
        message = "${LocalI18N.current["ui.fact.media.insert.summary", mapOf("mediaPkgName" to PackageNames.mediaProvider)]} \n\n ${LocalI18N.current["ui.fact.media.insert.input.path"]}",
        inputValidator = {
            it.isNotEmpty() to ""
        },
        showSymbolButton = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        onSelected = { pathString ->
            addFact(Fact.MediaStoreInsert(filterPathRegex = pathString.fallbackOnEmpty(".*")))
        }
    )
    TextInputDialog(state = mediaInsertSelectorState)


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
                edgeGestureSelectorState.show()
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

            Fact.SystemReady::class -> {
                addFact(Fact.SystemReady())
                true
            }

            Fact.Alarm::class -> {
                timeOfADaySelector.show()
                true
            }

            Fact.RandomInPeriod::class -> {
                randomInPeriodSelectorState.show(source.getFactAs())
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

            Fact.BTStatusChanged::class -> {
                btSelectorState.show()
                true
            }

            Fact.DNDStatusChanged::class -> {
                dndSelectorState.show()
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
                mediaInsertSelectorState.show(source.getFactAs<Fact.MediaStoreInsert>()?.filterPathRegex)
                true
            }


            else -> false
        }
    }
}

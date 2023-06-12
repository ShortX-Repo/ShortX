package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.selector.AlarmSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.EdgeGestureSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.KeyEventSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.OnOffAnyStateSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAlarmSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberEdgeGestureSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberKeyEventSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberOnOffAnyStateSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberTextInputState
import java.time.LocalTime

@Composable
fun systemFactSelectHandler(setFact: (Fact) -> Unit): (FactSelectSource) -> Boolean {
    // Fact selectors
    val keyEventSelectorState = rememberKeyEventSelectorState(selected = { code ->
        setFact(Fact.KeyEvent(code))
    })
    KeyEventSelector(keyEventSelectorState)

    val edgeGestureSelectorState = rememberEdgeGestureSelectorState(
        selected = { eg ->
            setFact(Fact.EdgeGesture(eg.first, eg.second))
        },
        showEdgeHint = {
            shortXManager.showEdgeHint(it)
        })
    EdgeGestureSelector(edgeGestureSelectorState)

    val timeOfADaySelector = rememberAlarmSelectorState(
        initialRepeat = emptyArray(),
        initialTime = LocalTime.now()
    ) { alarm ->
        // Use rule id as alarm tag.
        setFact(Fact.Alarm(timeOfADay = alarm.triggerAt, repeatDays = alarm.repeat))
    }
    AlarmSelector(timeOfADaySelector)

    val wifiSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.wifi.state.changed"]) {
            setFact(Fact.WifiStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(wifiSelectorState)

    val btSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.bt.state.changed"]) {
            setFact(Fact.BTStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(btSelectorState)

    val dndSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.dnd.state.changed"]) {
            setFact(Fact.DNDStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(dndSelectorState)

    val locationSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.location.state.changed"]) {
            setFact(Fact.LocationStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(locationSelectorState)

    val darkModeSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.dark.mode.state.changed"]) {
            setFact(Fact.DarkModeStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(darkModeSelectorState)

    val mobileDataSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.mobile.data.state.changed"]) {
            setFact(Fact.MobileDataStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(mobileDataSelectorState)

    val flashLightSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.flash.light.state.changed"]) {
            setFact(Fact.FlashLightStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(flashLightSelectorState)

    val nfcSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.nfc.state.changed"]) {
            setFact(Fact.NFCStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(nfcSelectorState)

    val hotspotSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.hotspot.state.changed"]) {
            setFact(Fact.HotSpotStatusChanged(it))
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
                setFact(Fact.BatteryLevelChanged(level = it))
            }
        }
    )
    TextInputDialog(state = batteryLevelSelectorState)

    return { source ->
        when (source.clazz) {
            Fact.KeyEvent::class -> {
                keyEventSelectorState.show()
                true
            }

            Fact.EdgeGesture::class -> {
                edgeGestureSelectorState.show()
                true
            }

            Fact.ScreenOff::class -> {
                setFact(Fact.ScreenOff())
                true
            }

            Fact.ScreenOn::class -> {
                setFact(Fact.ScreenOn())
                true
            }

            Fact.UserPresent::class -> {
                setFact(Fact.UserPresent())
                true
            }

            Fact.SystemReady::class -> {
                setFact(Fact.SystemReady())
                true
            }

            Fact.Alarm::class -> {
                timeOfADaySelector.show()
                true
            }

            Fact.WifiStatusChanged::class -> {
                wifiSelectorState.show()
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


            else -> false
        }
    }
}

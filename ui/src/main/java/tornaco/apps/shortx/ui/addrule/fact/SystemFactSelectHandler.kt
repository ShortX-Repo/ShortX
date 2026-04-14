package tornaco.apps.shortx.ui.addrule.fact

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import tornaco.apps.shortx.core.alarm.toLocalTime
import tornaco.apps.shortx.core.proto.condition.CalendarDayType
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.ScreenCorner
import tornaco.apps.shortx.core.proto.fact.SunRise
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.fact.mapper.displayNameKey
import tornaco.apps.shortx.ui.addrule.fact.mapper.factLabelKey
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.selector.AdvancedKeyEventSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.AlarmSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.BTDeviceSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.CombineKeyEventSelectorActivity
import tornaco.apps.shortx.ui.addrule.fact.selector.EdgeGestureSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.FixedPeriodSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.KeyEventSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.OnOffAnyStateSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.PeriodSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.CornerSwipeToCenterSelector
import tornaco.apps.shortx.ui.addrule.fact.selector.WifiSSIDSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAdvancedKeyEventSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAlarmSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberBTDeviceSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberCornerSwipeToCenterSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberEdgeGestureSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberFixedInPeriodSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberKeyEventSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberOnOffAnyStateSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberRandomInPeriodSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberWifiSSIDSelectorState
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.composeLogger
import java.time.LocalTime

@Composable
fun systemFactSelectHandler(addFact: (Fact) -> Unit): (FactSelectSource) -> Boolean {
    val context = LocalContext.current

    val keyEventSelectorState = rememberKeyEventSelectorState(selected = { code ->
        addFact(Fact.KeyEvent(code))
    })
    KeyEventSelector(keyEventSelectorState)

    val advKeyEventSelectorState = rememberAdvancedKeyEventSelectorState(selected = { event ->
        addFact(event)
    })
    AdvancedKeyEventSelector(advKeyEventSelectorState)

    val combineKeyCodeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.popupSelectorResult<Action.InjectCombineKeyCode>()
            action?.let {
                if (it.codes.size >= 2) {
                    addFact(
                        Fact.CombineKeyEvent(
                            keyCode1 = it.codes[0],
                            keyCode2 = it.codes[1],
                        )
                    )
                }
            }
        }
    )

    val edgeGestureSelectorState = rememberEdgeGestureSelectorState(
        selected = { eg ->
            composeLogger.d("EdgeGestureSelectorState selected: $eg")
            addFact(
                Fact.EdgeGesture(
                    edge = eg.edge,
                    gesture = eg.gesture,
                    childSteps = eg.childSteps,
                    isIntercept = eg.isIntercept,
                    apps = eg.apps,
                    pkgSets = eg.pkgSet,
                    customRect = eg.customRect,
                    enableGestureHint = eg.enableGestureHint,
                    enableGestureHaptic = eg.enableGestureHaptic,
                )
            )
        },
        showEdgeHint = {
            shortXManager.showEdgeHint(listOf(it))
        })
    EdgeGestureSelector(edgeGestureSelectorState)

    val cornerSwipeToCenterSelectorState = rememberCornerSwipeToCenterSelectorState(
        showCornerHint = {
            shortXManager.showCornerHint(listOf(it))
        },
        selected = { addFact(it) }
    )
    CornerSwipeToCenterSelector(cornerSwipeToCenterSelectorState)

    val screenGestureMenuState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.fact.screen.gesture"],
        menuItems = Gesture.entries.filter { it != Gesture.UNRECOGNIZED }.map {
            MenuDialogItem(it.name, title = LocalI18N.current[it.displayNameKey()])
        },
        onItemSelected = { _, id ->
            val gesture = Gesture.valueOf(id)
            addFact(Fact.ScreenGesture(gesture))
        })
    MenuDialog(state = screenGestureMenuState)

    val timeOfADaySelector = rememberAlarmSelectorState { alarm ->
        addFact(
            Fact.Alarm(
                timeOfADay = alarm.triggerAt,
                repeatDays = alarm.repeat,
                workdayFilter = if (alarm.hasWorkdayFilter()) alarm.workdayFilter else tornaco.apps.shortx.core.proto.fact.WorkdayFilter.getDefaultInstance()
            )
        )
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

    val sunRiseSetState = rememberMenuDialogState<String>(
        title = LocalI18N.current["ui.fact.sun.rise.set"],
        menuItems = listOf(
            SunRise.SunRise_Rise,
            SunRise.SunRise_Set
        ).map {
            MenuDialogItem(id = it.name, title = LocalI18N.current[it.factLabelKey()])
        },
        onItemSelected = { _, id ->
            addFact(Fact.SunRiseSet(rise = SunRise.valueOf(id)))
        })
    MenuDialog(state = sunRiseSetState)

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

    return { source ->
        when (source.clazz) {
            Fact.CombineKeyEvent::class -> {
                val currentKey1 = source.getFactAs<Fact.CombineKeyEvent>()?.keyCode1
                val currentKey2 = source.getFactAs<Fact.CombineKeyEvent>()?.keyCode2
                val dummyAction = Action.InjectCombineKeyCode(
                    codes = listOfNotNull(currentKey1, currentKey2)
                )
                combineKeyCodeLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        dummyAction,
                        CombineKeyEventSelectorActivity::class.java
                    )
                )
                true
            }

            Fact.KeyEvent::class -> {
                keyEventSelectorState.show()
                true
            }

            Fact.AdvancedKeyEvent::class -> {
                advKeyEventSelectorState.show(source.getFactAs())
                true
            }

            Fact.EdgeGesture::class -> {
                edgeGestureSelectorState.show(source.getFactAs())
                true
            }

            Fact.CornerSwipeToCenter::class -> {
                cornerSwipeToCenterSelectorState.show(source.getFactAs())
                true
            }

            Fact.ScreenGesture::class -> {
                screenGestureMenuState.show(
                    currentSelection = listOfNotNull(
                        source.getFactAs<Fact.ScreenGesture>()?.gesture?.name
                    )
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
                val currentAlarm = source.getFactAs<Fact.Alarm>()
                val localTime = currentAlarm?.timeOfADay?.toLocalTime() ?: LocalTime.now()
                val repeat = currentAlarm?.repeatDays?.daysList ?: emptyList()
                val dayType = currentAlarm?.workdayFilter?.dayType ?: CalendarDayType.AnyDay
                timeOfADaySelector.show(localTime, repeat, dayType)
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

            Fact.ChargerPlug::class -> {
                addFact(Fact.ChargerPlug())
                true
            }

            Fact.ChargerUnplug::class -> {
                addFact(Fact.ChargerUnplug())
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


            Fact.HeadsetPlug::class -> {
                headsetPlugMenuState.show()
                true
            }

            Fact.SunRiseSet::class -> {
                sunRiseSetState.show()
                true
            }

            else -> false
        }
    }
}

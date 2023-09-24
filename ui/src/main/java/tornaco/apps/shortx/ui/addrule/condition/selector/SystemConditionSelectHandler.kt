package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.condition.BatteryPercentSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectSource
import tornaco.apps.shortx.ui.addrule.condition.EvaluateGlobalVarSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.SIMSlotSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.getConditionAs
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.rememberBatteryPercentSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberEvaluateGlobalVarSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberSIMSlotSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.BTDeviceSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.WifiSSIDSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberBTDeviceSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberWifiSSIDSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.rememberMenuDialogState

@Composable
fun systemConditionSelectHandler(addCondition: (Condition) -> Unit): (ConditionSelectSource) -> Boolean {
    val evaluateGlobalVarSelectorDialogState = rememberEvaluateGlobalVarSelectorState {
        addCondition(it)
    }
    EvaluateGlobalVarSelectorDialog(state = evaluateGlobalVarSelectorDialogState)

    val batteryPercentDialog = rememberBatteryPercentSelectorState {
        addCondition(it)
    }
    BatteryPercentSelectorDialog(state = batteryPercentDialog)

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
        menuItems = listOf(
            MenuDialogItem(
                id = "plug",
                title = LocalI18N.current["ui.conditions.plug.state.plug"],
            ),
            MenuDialogItem(
                id = "notPlug",
                title = LocalI18N.current["ui.conditions.plug.state.not.plug"],
            ),
        ),
        onItemSelected = { data, id ->
            addCondition(Condition.PlugState(requireIsPlug = id == "plug"))
        }
    )
    MenuDialog(state = plugStateSelectorState)

    val wifiConnectedToSelectorState = rememberWifiSSIDSelectorState(
        title = LocalI18N.current["ui.conditions.wifi.connected"],
        message = LocalI18N.current["ui.conditions.wifi.connected.target"],
        showSymbolButton = false,
        onSelected = {
            addCondition(Condition.RequireWifiConnected(requiredSSID = it))
        }
    )
    WifiSSIDSelectorDialog(state = wifiConnectedToSelectorState)

    val btConnectedSelectorState = rememberBTDeviceSelectorState(
        title = LocalI18N.current["ui.conditions.bt.connected"],
        message = LocalI18N.current["ui.conditions.bt.connected.target"],
        showSymbolButton = false,
        onSelected = {
            addCondition(Condition.RequireBTConnected(requiredDevice = it))
        }
    )
    BTDeviceSelectorDialog(state = btConnectedSelectorState)

    val mobileDataEnableSelectorState = rememberSIMSlotSelectorState(onSelected = {
        addCondition(Condition.RequireMobileDataEnabled(slot = it))
    })
    SIMSlotSelectorDialog(state = mobileDataEnableSelectorState)

    return { source ->
        when (source.clazz) {
            Condition.ScreenIsOn::class -> {
                addCondition(Condition.ScreenIsOn())
                true
            }

            Condition.VPNIsConnected::class -> {
                addCondition(Condition.VPNIsConnected())
                true
            }

            Condition.BatteryPercent::class -> {
                batteryPercentDialog.show(source.getConditionAs())
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

            else -> false
        }
    }
}
package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.condition.BatteryPercentSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.ConditionSelectSource
import tornaco.apps.shortx.ui.addrule.condition.EvaluateGlobalVarSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.getConditionAs
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.rememberBatteryPercentSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberEvaluateGlobalVarSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState

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

    val wifiConnectedToSelectorState = rememberTextInputState(
        title = LocalI18N.current["ui.conditions.wifi.connected"],
        message = LocalI18N.current["ui.conditions.wifi.connected.target"],
        showSymbolButton = false,
        onSelected = {
            addCondition(Condition.RequireWifiConnected(requiredSSID = it))
        }
    )
    TextInputDialog(state = wifiConnectedToSelectorState)

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
                batteryPercentDialog.show()
                true
            }

            Condition.ChargeState::class -> {
                chargeStateSelectorState.show()
                true
            }

            Condition.RequireWifiConnected::class -> {
                wifiConnectedToSelectorState.show()
                true
            }

            Condition.RequireWifiDisconnected::class -> {
                addCondition(Condition.RequireWifiDisconnected())
                true
            }

            Condition.TimeInRange::class -> {
                timeInRangeSelectorState.show(source.getConditionAs<Condition.TimeInRange>()?.range)
                true
            }

            else -> false
        }
    }
}
package tornaco.apps.shortx.services.rule.actor.mvel.handle

data class BatteryState(
    val batteryLevel: Int = 0,
    val isCharging: Boolean = false,
    val isAcPlug: Boolean = false,
    val isUsbPlug: Boolean = false,
    val isWirelessPlug: Boolean = false,
    val isAnyPlug: Boolean = false
)
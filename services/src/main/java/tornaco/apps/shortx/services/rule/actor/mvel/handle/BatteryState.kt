package tornaco.apps.shortx.services.rule.actor.mvel.handle

data class BatteryState(
    val batteryLevel: Int = 0,
    val isCharging: Boolean = false,
    val isAcCharge: Boolean = false,
    val isUsbCharge: Boolean = false,
)
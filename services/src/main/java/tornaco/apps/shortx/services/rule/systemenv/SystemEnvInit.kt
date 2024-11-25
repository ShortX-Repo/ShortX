package tornaco.apps.shortx.services.rule.systemenv

import android.content.Context
import android.os.BatteryManager
import tornaco.apps.shortx.core.context.SystemEnv
import tornaco.apps.shortx.services.ShortXContext

object SystemEnvInit {
    fun install(context: Context, shortXContext: ShortXContext) {
        Env.entries.forEach {
            SystemEnv.addEnvInflater(it.key to {
                it.getValue(GetValueInput(context, shortXContext))
            })
        }
    }
}

data class GetValueInput(
    val context: Context,
    val shortXContext: ShortXContext
)

enum class Env(
    val displayName: String,
    val key: String,
    val getValue: GetValueInput.() -> String
) {
    BatteryLevel(
        displayName = "BatteryLevel",
        key = "BatteryLevel",
        getValue = {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toString()
        }
    ),


}
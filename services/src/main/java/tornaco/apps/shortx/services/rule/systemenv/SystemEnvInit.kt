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

    fun getAllEnv(): List<tornaco.apps.shortx.core.proto.common.SystemEnv> {
        return Env.entries.map {
            tornaco.apps.shortx.core.proto.common.SystemEnv.newBuilder()
                .setDisplayName(it.displayName)
                .setKeyDecored(with(SystemEnv) {
                    it.key.asEnvName()
                })
                .build()
        }
    }
}

data class GetValueInput(
    val context: Context,
    val shortXContext: ShortXContext
)

private const val I18N_KEY_PREFIX = "ui.system.env"

enum class Env(
    val displayName: String,
    val key: String,
    val getValue: GetValueInput.() -> String
) {
    BatteryLevel(
        displayName = "$I18N_KEY_PREFIX.battery.level",
        key = "BatteryLevel",
        getValue = {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toString()
        }
    ),
}
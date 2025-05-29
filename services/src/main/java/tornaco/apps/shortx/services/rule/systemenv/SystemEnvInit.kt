package tornaco.apps.shortx.services.rule.systemenv

import android.content.Context
import android.os.BatteryManager
import android.provider.Settings
import tornaco.apps.shortx.core.context.SystemEnv
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.trigger.BatteryTemperatureTrigger
import kotlin.math.roundToInt

object SystemEnvInit {
    fun install(context: Context, shortXContext: ShortXContext) {
        Env.entries.forEach { env ->
            SystemEnv.addEnvInflater(env.key to { param ->
                runCatching {
                    env.getValue(GetValueInput(context, shortXContext, param))
                }.getOrElse {
                    shortXContext.watchDog.logger.e(it, "SystemEnv getValue error: ${env.key}")
                    null
                }
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
                .setTip(it.tip.orEmpty())
                .setRequireParam(it.requireParam)
                .build()
        }
    }
}

data class GetValueInput(
    val context: Context,
    val shortXContext: ShortXContext,
    val param: String?
)

private const val I18N_KEY_PREFIX = "ui.system.env"

enum class Env(
    val displayName: String,
    val key: String,
    val getValue: GetValueInput.() -> String?,
    val tip: String? = null,
    val requireParam: Boolean = false
) {
    UUID(
        displayName = "$I18N_KEY_PREFIX.uuid",
        key = "UUID",
        getValue = {
            java.util.UUID.randomUUID().toString()
        }
    ),
    BatteryLevel(
        displayName = "$I18N_KEY_PREFIX.battery.level",
        key = "BatteryLevel",
        getValue = {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toString()
        }
    ),
    CurrentTimeMillis(
        displayName = "$I18N_KEY_PREFIX.current.time.millis",
        key = "CurrentTimeMillis",
        getValue = {
            System.currentTimeMillis().toString()
        }
    ),
    CurrentTimeFormated(
        displayName = "$I18N_KEY_PREFIX.current.time.formated",
        key = "CurrentTimeFormated",
        getValue = {
            DateUtils.formatLongForMessageTime(System.currentTimeMillis())
        }
    ),
    ClipboardText(
        displayName = "$I18N_KEY_PREFIX.clipboard.text",
        key = "ClipboardText",
        getValue = {
            context.clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
        }
    ),
    FrontAppLabel(
        displayName = "$I18N_KEY_PREFIX.front.app.label",
        key = "FrontAppLabel",
        getValue = {
            shortXContext.appState.currentFrontPkgs().joinToString {
                shortXContext.appState.getAppLabel(it) ?: it.pkgName
            }
        }
    ),
    FrontAppPkgName(
        displayName = "$I18N_KEY_PREFIX.front.app.pkg.name",
        key = "FrontAppPkgName",
        getValue = {
            shortXContext.appState.currentFrontPkgs().joinToString {
                it.pkgName
            }
        }
    ),
    SystemSettings(
        displayName = "$I18N_KEY_PREFIX.system.settings",
        key = "SystemSettings",
        getValue = {
            Settings.System.getString(context.contentResolver, requireNotNull(param) {
                "Settings name is missing."
            })
        },
        tip = "$I18N_KEY_PREFIX.system.settings.tip",
        requireParam = true
    ),
    CpuAvailabilityPercent(
        displayName = "$I18N_KEY_PREFIX.cpu.availability.percent",
        key = "CpuAvailabilityPercent",
        getValue = {
            if (param.isNullOrBlank()) {
                shortXContext.cpu.getCpuTotalAvailablePercentFallback().toString()
            } else {
                val core = param.toIntOrNull()
                if (core == null) {
                    shortXContext.cpu.getCpuTotalAvailablePercent().toString()
                } else {
                    shortXContext.cpu.getCpuAvailablePercents()[core]?.toString() ?: "N/A"
                }
            }
        }
    ),
    CpuTemperature(
        displayName = "$I18N_KEY_PREFIX.cpu.temperature",
        key = "CpuTemperature",
        getValue = {
            shortXContext.cpu.getCpuTemperature().roundToInt().toString()
        }
    ),
    MemAvailabilityPercent(
        displayName = "$I18N_KEY_PREFIX.mem.availability.percent",
        key = "MemAvailabilityPercent",
        getValue = {
            (100 - shortXContext.cpu.getMemAvailableInfo().memUsedPercent).toString()
        }
    ),
    SwapAvailabilityPercent(
        displayName = "$I18N_KEY_PREFIX.swap.availability.percent",
        key = "SwapAvailabilityPercent",
        getValue = {
            (100 - shortXContext.cpu.getMemAvailableInfo().swapUsedPercent).toString()
        }
    ),
    BatteryTemperature(
        displayName = "$I18N_KEY_PREFIX.battery.temperature",
        key = "BatteryTemperature",
        getValue = {
            (BatteryTemperatureTrigger.temperature.value / 10f).roundToInt().toString()
        }
    ),
}
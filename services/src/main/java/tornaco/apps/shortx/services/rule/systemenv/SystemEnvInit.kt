package tornaco.apps.shortx.services.rule.systemenv

import android.content.Context
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import tornaco.apps.shortx.core.context.SystemEnv
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.audioManager
import tornaco.apps.shortx.core.util.batteryManager
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.core.util.connectivityManager
import tornaco.apps.shortx.core.util.notificationManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.trigger.BatteryTemperatureTrigger
import kotlin.math.roundToInt
import kotlin.random.Random
import tornaco.apps.shortx.core.i18n.I18N_R

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

    fun resolveEnvValue(context: Context, shortXContext: ShortXContext, rawKey: String): String? {
        val env = Env.entries.firstOrNull { it.key == rawKey } ?: return null
        return runCatching {
            env.getValue(GetValueInput(context, shortXContext, null))
        }.getOrElse { null }
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

private const val I18N_KEY_PREFIX = I18N_R.UI_SYSTEM_ENV

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
                    shortXContext.cpu.getCpuTotalAvailablePercentFallback().toString()
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
    NetworkType(
        displayName = "$I18N_KEY_PREFIX.network.type",
        key = "NetworkType",
        getValue = {
            val cm = context.connectivityManager
            val network = cm.activeNetwork
            if (network == null) {
                "disconnected"
            } else {
                val caps = cm.getNetworkCapabilities(network)
                when {
                    caps == null -> "unknown"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "cellular"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "bluetooth"
                    else -> "other"
                }
            }
        }
    ),
    WifiSSID(
        displayName = "$I18N_KEY_PREFIX.wifi.ssid",
        key = "WifiSSID",
        getValue = {
            shortXContext.network.connectedWifiSSID
        }
    ),
    IsConnected(
        displayName = "$I18N_KEY_PREFIX.is.connected",
        key = "IsConnected",
        getValue = {
            (context.connectivityManager.activeNetwork != null).toString()
        }
    ),
    RingerMode(
        displayName = "$I18N_KEY_PREFIX.ringer.mode",
        key = "RingerMode",
        getValue = {
            when (context.audioManager.ringerMode) {
                AudioManager.RINGER_MODE_SILENT -> "silent"
                AudioManager.RINGER_MODE_VIBRATE -> "vibrate"
                AudioManager.RINGER_MODE_NORMAL -> "normal"
                else -> "unknown"
            }
        }
    ),
    DndMode(
        displayName = "$I18N_KEY_PREFIX.dnd.mode",
        key = "DndMode",
        getValue = {
            val filter = try {
                context.notificationManager.currentInterruptionFilter
            } catch (_: Exception) {
                0
            }
            when (filter) {
                android.app.NotificationManager.INTERRUPTION_FILTER_NONE -> "none"
                android.app.NotificationManager.INTERRUPTION_FILTER_PRIORITY -> "priority"
                android.app.NotificationManager.INTERRUPTION_FILTER_ALARMS -> "alarms"
                android.app.NotificationManager.INTERRUPTION_FILTER_ALL -> "all"
                else -> "off"
            }
        }
    ),
    MediaVolume(
        displayName = "$I18N_KEY_PREFIX.media.volume",
        key = "MediaVolume",
        getValue = {
            context.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toString()
        }
    ),
    IsCharging(
        displayName = "$I18N_KEY_PREFIX.is.charging",
        key = "IsCharging",
        getValue = {
            context.batteryManager.isCharging.toString()
        }
    ),
    ScreenBrightness(
        displayName = "$I18N_KEY_PREFIX.screen.brightness",
        key = "ScreenBrightness",
        getValue = {
            Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS
            ).toString()
        }
    ),
    DeviceUpTime(
        displayName = "$I18N_KEY_PREFIX.device.uptime",
        key = "DeviceUpTime",
        getValue = {
            (SystemClock.elapsedRealtime() / 1000).toString()
        },
        tip = "$I18N_KEY_PREFIX.device.uptime.tip"
    ),
    DeviceModel(
        displayName = "$I18N_KEY_PREFIX.device.model",
        key = "DeviceModel",
        getValue = {
            Build.MODEL
        }
    ),
    AndroidVersion(
        displayName = "$I18N_KEY_PREFIX.android.version",
        key = "AndroidVersion",
        getValue = {
            Build.VERSION.RELEASE
        }
    ),
    RandomInt(
        displayName = "$I18N_KEY_PREFIX.random.int",
        key = "RandomInt",
        getValue = {
            if (param.isNullOrBlank()) {
                Random.nextInt(0, 100).toString()
            } else {
                val parts = param.split("-")
                if (parts.size == 2) {
                    val from = parts[0].trim().toIntOrNull() ?: 0
                    val to = parts[1].trim().toIntOrNull() ?: 100
                    Random.nextInt(from, (to + 1).coerceAtLeast(from + 1)).toString()
                } else {
                    Random.nextInt(0, 100).toString()
                }
            }
        },
        tip = "$I18N_KEY_PREFIX.random.int.tip",
        requireParam = true
    ),
    LastRebootTime(
        displayName = "$I18N_KEY_PREFIX.last.reboot.time",
        key = "LastRebootTime",
        getValue = {
            (System.currentTimeMillis() - SystemClock.elapsedRealtime()).toString()
        },
        tip = "$I18N_KEY_PREFIX.last.reboot.time.tip"
    ),
}
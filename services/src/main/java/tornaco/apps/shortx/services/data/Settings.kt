package tornaco.apps.shortx.services.data

import android.os.Bundle
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.settings.DANMU_COLOR_AUTO
import tornaco.apps.shortx.core.settings.DANMU_DEFAULT_ALPHA
import tornaco.apps.shortx.core.settings.DANMU_DEFAULT_DURATION
import tornaco.apps.shortx.core.settings.DANMU_DEFAULT_TEXT_SIZE

sealed class Settings<T>(val key: String, val defaultValue: T) {

    sealed class BoolSettings(key: String, defaultValue: Boolean) :
        Settings<Boolean>(key, defaultValue) {

        data object EnableEvaluateRecorder :
            BoolSettings(
                key = "EnableEvaluateRecorder",
                defaultValue = BuildProp.APP_IS_DEBUG
            )

        data object EnableKeyEventPrompt :
            BoolSettings(key = "EnableKeyEventPrompt", defaultValue = BuildProp.APP_IS_DEBUG)

        data object EnableInjectedShell :
            BoolSettings(key = "EnableInjectedShell", defaultValue = false)

        data object EnableQSTileOpt :
            BoolSettings(key = "EnableQSTileOpt", defaultValue = false)

        data object EnableSettingsCatcher :
            BoolSettings(key = "EnableSettingsCatcher", defaultValue = false)
    }

    sealed class StringSettings(key: String, defaultValue: String?) :
        Settings<String?>(key, defaultValue) {
        // Unique ID for Service ins, usually used for data enc.
        data object ServiceIns :
            StringSettings(key = "ServiceIns", defaultValue = null)
    }

    sealed class IntSettings(key: String, defaultValue: Int) :
        Settings<Int>(key, defaultValue) {
        object OverlayButtonPosX : IntSettings(key = "OverlayButtonPosX", defaultValue = -1)
        object OverlayButtonPosY : IntSettings(key = "OverlayButtonPosY", defaultValue = -1)

        object DanmuBgColor : IntSettings(key = "DanmuBgColor", defaultValue = DANMU_COLOR_AUTO)
        object DanmuDurationSeconds :
            IntSettings(key = "DanmuDurationSeconds", defaultValue = DANMU_DEFAULT_DURATION.toInt())

        object DanmuTextColor : IntSettings(key = "DanmuTextColor", defaultValue = DANMU_COLOR_AUTO)
        object DanmuTextSize :
            IntSettings(key = "DanmuTextSize", defaultValue = DANMU_DEFAULT_TEXT_SIZE)

        object GestureAreaSizeDp :
            IntSettings(key = "GestureAreaSize", defaultValue = 28)
    }

    sealed class FloatSettings(key: String, defaultValue: Float) :
        Settings<Float>(key, defaultValue) {
        object DanmuAlpha : FloatSettings(key = "DanmuAlpha", defaultValue = DANMU_DEFAULT_ALPHA)
    }

    sealed class MapSettings(key: String, defaultValue: Map<String, String> = emptyMap()) :
        Settings<Map<String, String>>(key, defaultValue) {
    }
}

fun SettingsStore.write(settings: Settings.BoolSettings, boolValue: Boolean) =
    putBool(settings.key, boolValue)

fun SettingsStore.read(settings: Settings.BoolSettings) =
    getBool(settings.key, settings.defaultValue)

fun SettingsStore.write(settings: Settings.MapSettings, map: Map<String, String>) =
    putStringMap(settings.key, map)

fun SettingsStore.read(settings: Settings.MapSettings) =
    getStringMap(settings.key, settings.defaultValue)


fun SettingsStore.write(settings: Settings.StringSettings, value: String) =
    putString(settings.key, value)

fun SettingsStore.read(settings: Settings.StringSettings) =
    getString(settings.key, settings.defaultValue)

fun SettingsStore.write(settings: Settings.IntSettings, value: Int) =
    putInt(settings.key, value)

fun SettingsStore.write(settings: Settings.FloatSettings, value: Float) =
    putFloat(settings.key, value)

fun SettingsStore.read(settings: Settings.IntSettings): Int =
    getInt(settings.key, settings.defaultValue)

fun SettingsStore.read(settings: Settings.FloatSettings): Float =
    getFloat(settings.key, settings.defaultValue)

fun Bundle.toStringMap(): Map<String, String> {
    return keySet().associateWith { getString(it) }
}

fun Map<String, String>.toBundle(): Bundle {
    return Bundle().apply {
        forEach {
            putString(it.key, it.value)
        }
    }
}
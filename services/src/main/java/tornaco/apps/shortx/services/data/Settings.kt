package tornaco.apps.shortx.services.data

import android.content.Context
import android.os.Bundle
import tornaco.apps.shortx.core.settings.DANMU_COLOR_AUTO
import tornaco.apps.shortx.core.settings.DANMU_DEFAULT_ALPHA
import tornaco.apps.shortx.core.settings.DANMU_DEFAULT_DURATION
import tornaco.apps.shortx.core.settings.DANMU_DEFAULT_TEXT_SIZE

sealed class Settings<T>(val key: String, val defaultValue: (Context) -> T) {

    sealed class BoolSettings(key: String, defaultValue: (Context) -> Boolean) :
        Settings<Boolean>(key, defaultValue) {

        data class EnableEvaluateRecorder(val id: String) :
            BoolSettings(key = "EnableEvaluateRecorder_$id", defaultValue = { false })

        data object EnableKeyEventPrompt :
            BoolSettings(key = "EnableKeyEventPrompt", defaultValue = { false })

        data object EnableInjectedShell :
            BoolSettings(key = "EnableInjectedShell", defaultValue = { true })

        data object EnableQSTileOpt :
            BoolSettings(key = "EnableQSTileOpt", defaultValue = { false })

        @Deprecated("Remove since it affect device Performance")
        data object EnableSettingsCatcher :
            BoolSettings(key = "EnableSettingsCatcher", defaultValue = { false })

        data class EnableRuleEngine(val engineName: String, val enabledByDefault: Boolean) :
            BoolSettings(
                key = "EnableRuleEngine_${engineName}",
                defaultValue = { enabledByDefault })

        data object EnableRuleFeature :
            BoolSettings(key = "EnableRuleFeature", defaultValue = { true })

        data object PostNByApp :
            BoolSettings(key = "PostNByApp", defaultValue = { false })

        data object EnableHttpRequestLog :
            BoolSettings(key = "EnableHttpRequestLog", defaultValue = { true })

        data object EnableForceDisableShortXByKeyPress :
            BoolSettings(key = "EnableForceDisableShortXByKeyPress", defaultValue = { true })

        data class EnableToggle(val id: String) :
            BoolSettings(key = "EnableToggle${id}", defaultValue = { false })
    }

    sealed class StringSettings(key: String, defaultValue: (Context) -> String?) :
        Settings<String?>(key, defaultValue) {
        // Unique ID for Service ins, usually used for data enc.
        data object ServiceIns :
            StringSettings(key = "ServiceIns", defaultValue = { null })
    }

    sealed class IntSettings(key: String, defaultValue: (Context) -> Int) :
        Settings<Int>(key, defaultValue) {
        data class OverlayButtonPosX(val tag: String) :
            IntSettings(key = "OverlayButtonPosX_$tag", defaultValue = { -1 })

        data class OverlayButtonPosY(val tag: String) :
            IntSettings(key = "OverlayButtonPosY_$tag", defaultValue = { -1 })

        data object DanmuBgColor :
            IntSettings(key = "DanmuBgColor", defaultValue = { DANMU_COLOR_AUTO })

        data object DanmuDurationSeconds :
            IntSettings(
                key = "DanmuDurationSeconds",
                defaultValue = { DANMU_DEFAULT_DURATION.toInt() })

        data object DanmuTextColor :
            IntSettings(key = "DanmuTextColor", defaultValue = { DANMU_COLOR_AUTO })

        data object DanmuTextSize :
            IntSettings(key = "DanmuTextSize", defaultValue = { DANMU_DEFAULT_TEXT_SIZE })

        data object GestureAreaWidthDp :
            IntSettings(key = "GestureAreaSize", defaultValue = { 28 })

        data object SwipeDistanceThresholdDp :
            IntSettings(key = "SwipeDistanceThresholdDp", defaultValue = { 24 })

        data class DidSomethingAtDay(val day: String, val what: String) :
            IntSettings(key = "DidSomethingAtDay_${day}_${what}", defaultValue = { 0 })
    }

    sealed class FloatSettings(key: String, defaultValue: (Context) -> Float) :
        Settings<Float>(key, defaultValue) {
        object DanmuAlpha :
            FloatSettings(key = "DanmuAlpha", defaultValue = { DANMU_DEFAULT_ALPHA })
    }

    sealed class MapSettings(
        key: String,
        defaultValue: (Context) -> Map<String, String> = { emptyMap() }
    ) :
        Settings<Map<String, String>>(key, defaultValue) {
    }
}

fun SettingsStore.write(settings: Settings.BoolSettings, boolValue: Boolean) =
    putBool(settings.key, boolValue)

fun SettingsStore.read(settings: Settings.BoolSettings) =
    getBool(settings.key, settings.defaultValue(context))

fun SettingsStore.write(settings: Settings.MapSettings, map: Map<String, String>) =
    putStringMap(settings.key, map)

fun SettingsStore.read(settings: Settings.MapSettings) =
    getStringMap(settings.key, settings.defaultValue(context))


fun SettingsStore.write(settings: Settings.StringSettings, value: String) =
    putString(settings.key, value)

fun SettingsStore.read(settings: Settings.StringSettings) =
    getString(settings.key, settings.defaultValue(context))

fun SettingsStore.write(settings: Settings.IntSettings, value: Int) =
    putInt(settings.key, value)

fun SettingsStore.write(settings: Settings.FloatSettings, value: Float) =
    putFloat(settings.key, value)

fun SettingsStore.read(settings: Settings.IntSettings): Int =
    getInt(settings.key, settings.defaultValue(context))

fun SettingsStore.read(settings: Settings.FloatSettings): Float =
    getFloat(settings.key, settings.defaultValue(context))

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
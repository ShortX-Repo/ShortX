package tornaco.apps.shortx.services.data

import android.content.Context
import android.os.Bundle
import tornaco.apps.shortx.core.settings.DANMU_COLOR_AUTO
import tornaco.apps.shortx.core.settings.DANMU_DEFAULT_ALPHA
import tornaco.apps.shortx.core.settings.DANMU_DEFAULT_DURATION
import tornaco.apps.shortx.core.settings.DANMU_DEFAULT_TEXT_SIZE

sealed class Settings<T>(val key: String, val defaultValue: (Context) -> T) {
    companion object {
        const val DEFAULT_SU_PATH = "su --mount-master"
    }

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

        data class EnableFeature(val id: String, val defValue: Boolean = true) :
            BoolSettings(key = "EnableFeature${id}", defaultValue = { defValue })

        data object ClipboardEnableDeDup :
            BoolSettings(key = "ClipboardEnableDeDup", defaultValue = { false })
    }

    sealed class StringSettings(key: String, defaultValue: (Context) -> String?) :
        Settings<String?>(key, defaultValue) {
        // Unique ID for Service ins, usually used for data enc.
        data object ServiceIns :
            StringSettings(key = "ServiceIns", defaultValue = { null })

        data object ShellCustomSUPath :
            StringSettings(key = "ShellCustomSUPath", defaultValue = { DEFAULT_SU_PATH })
    }

    sealed class IntSettings(key: String, defaultValue: (Context) -> Int) :
        Settings<Int>(key, defaultValue) {
        data class OverlayButtonPosX(val tag: String) :
            IntSettings(key = "OverlayButtonPosX_$tag", defaultValue = { -1 })

        data class OverlayButtonPosY(val tag: String) :
            IntSettings(key = "OverlayButtonPosY_$tag", defaultValue = { -1 })

        data class FloatWindowPosX(val tag: String) :
            IntSettings(key = "FloatWindowPosX_$tag", defaultValue = { 500 })

        data class FloatWindowPosY(val tag: String) :
            IntSettings(key = "FloatWindowPosY_$tag", defaultValue = { 500 })

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

        data object CornerGestureRegionRadiusRatioPercent :
            IntSettings(key = "CornerGestureRegionRadiusRatioPercent", defaultValue = { 18 })

        data object CornerGestureMinSwipeDistanceDp :
            IntSettings(key = "CornerGestureMinSwipeDistanceDp", defaultValue = { 72 })

        data object EdgeGestureSequenceHoverDurationMs :
            IntSettings(key = "EdgeGestureSequenceHoverDurationMs", defaultValue = { 300 })

        data object EdgeGestureSequenceHoverRadiusDp :
            IntSettings(key = "EdgeGestureSequenceHoverRadiusDp", defaultValue = { 24 })

        data object EdgeGestureSequenceChildSwipeMinDistanceDp :
            IntSettings(key = "EdgeGestureSequenceChildSwipeMinDistanceDp", defaultValue = { 72 })

        data object EdgeGestureSequenceChildSwipeMaxAngleDeviationDeg :
            IntSettings(key = "EdgeGestureSequenceChildSwipeMaxAngleDeviationDeg", defaultValue = { 35 })

        data object KeyDetectorLongPressTimeoutMS :
            IntSettings(key = "KeyDetectorLongPressTimeoutMS", defaultValue = { 499 })

        data class DidSomethingAtDay(val day: String, val what: String) :
            IntSettings(key = "DidSomethingAtDay_${day}_${what}", defaultValue = { 0 })

        data object StatusBarChipPaddingStart :
            IntSettings(key = "StatusBarChipPaddingStart", defaultValue = { 0 })

        data object StatusBarChipPaddingEnd :
            IntSettings(key = "StatusBarChipPaddingEnd", defaultValue = { 0 })

        data object StatusBarChipPaddingTop :
            IntSettings(key = "StatusBarChipPaddingTop", defaultValue = { 0 })

        data object StatusBarChipFontSize :
            IntSettings(key = "StatusBarChipFontSize", defaultValue = { 10 })

        data object StatusBarChipIconSize :
            IntSettings(key = "StatusBarChipIconSize", defaultValue = { 12 })

        data object StatusBarChipPaddingBottom :
            IntSettings(key = "StatusBarChipPaddingBottom", defaultValue = { 0 })

        data object StatusBarChipUiPlacement :
            IntSettings(
                key = "StatusBarChipUiPlacement",
                defaultValue = { tornaco.apps.shortx.core.proto.common.StatusBarClipUiPlacement.StatusBarClipUiPlacement_Start.number })

        data object StatusBarChipBgColor :
            IntSettings(key = "StatusBarChipBgColor", defaultValue = { DANMU_COLOR_AUTO })

        data object StatusBarChipTextColor :
            IntSettings(key = "StatusBarChipTextColor", defaultValue = { DANMU_COLOR_AUTO })

        data object StatusBarChipMaxLength :
            IntSettings(key = "StatusBarChipMaxLength", defaultValue = { 100 })

        data object OCREngine :
            IntSettings(
                key = "OCREngine",
                defaultValue = { tornaco.apps.shortx.core.proto.common.OCREngine.OCREngine_Paddle_VALUE })

        data object AreaSelectorTipsShown :
            IntSettings(key = "AreaSelectorTipsShown", defaultValue = { 0 })

        data class SIMSlotSubIdMapping(val slotId: Int) :
            IntSettings(key = "SIMSlotSubIdMapping${slotId}", defaultValue = { -1 })
    }

    sealed class FloatSettings(key: String, defaultValue: (Context) -> Float) :
        Settings<Float>(key, defaultValue) {
        data object DanmuAlpha :
            FloatSettings(key = "DanmuAlpha", defaultValue = { DANMU_DEFAULT_ALPHA })

        data object GestureAreaLengthScale :
            FloatSettings(key = "GestureAreaLengthScale", defaultValue = { 1f })
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
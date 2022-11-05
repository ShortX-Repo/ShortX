package tornaco.apps.shortx.data

import tornaco.apps.shortx.core.gesture.Area

sealed class Settings<T>(val key: String, val defaultValue: T) {

    sealed class BoolSettings(key: String, defaultValue: Boolean) :
        Settings<Boolean>(key, defaultValue) {

        object EdgeGestureEnabled : BoolSettings(key = "EdgeGestureEnabled", defaultValue = false)

        data class EdgeGestureOnAreaEnabled(val area: Area) :
            BoolSettings(key = "EdgeGestureOnAreaEnabled-$area", defaultValue = false)
    }
}

fun SettingsStore.write(boolSettings: Settings.BoolSettings, boolValue: Boolean) =
    putBool(boolSettings.key, boolValue)

fun SettingsStore.read(boolSettings: Settings.BoolSettings) =
    getBool(boolSettings.key, boolSettings.defaultValue)
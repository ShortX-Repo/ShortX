package tornaco.apps.shortx.services.data

import android.os.Bundle
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture

sealed class Settings<T>(val key: String, val defaultValue: T) {

    sealed class BoolSettings(key: String, defaultValue: Boolean) :
        Settings<Boolean>(key, defaultValue) {

        object GestureEnabled : BoolSettings(key = "GestureEnabled", defaultValue = false)

        data class GestureOnAreaEnabled(val area: Edge) :
            BoolSettings(key = "GestureOnAreaEnabled-$area", defaultValue = false)
    }

    sealed class MapSettings(key: String, defaultValue: Map<String, String> = emptyMap()) :
        Settings<Map<String, String>>(key, defaultValue) {

        data class GestureActionOnArea(val area: Edge, val gestureEvent: Gesture) :
            MapSettings(key = "GestureActionOnArea-$area-$gestureEvent")
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
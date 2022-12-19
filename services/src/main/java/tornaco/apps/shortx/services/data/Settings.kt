package tornaco.apps.shortx.services.data

import android.os.Bundle

sealed class Settings<T>(val key: String, val defaultValue: T) {

    sealed class BoolSettings(key: String, defaultValue: Boolean) :
        Settings<Boolean>(key, defaultValue) {
    }

    sealed class StringSettings(key: String, defaultValue: String) :
        Settings<String>(key, defaultValue) {
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
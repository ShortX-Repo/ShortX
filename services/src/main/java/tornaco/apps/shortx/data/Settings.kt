package tornaco.apps.shortx.data

sealed class Settings<T>(val key: String, val defaultValue: T) {

    sealed class BoolSettings(key: String, defaultValue: Boolean) :
        Settings<Boolean>(key, defaultValue) {

        object EdgeGestureEnabled : BoolSettings(key = "EdgeGestureEnabled", defaultValue = false)
    }


}
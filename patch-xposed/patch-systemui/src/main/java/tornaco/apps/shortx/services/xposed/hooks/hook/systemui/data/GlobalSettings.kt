package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.data

import android.content.Context
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.j.KVStore
import java.io.File

object GlobalSettings {
    private const val STORE_FILE_NAME = "global_settings_shortx.txt"
    private const val KEY_GESTURE_ENABLED = "gesture"

    private val logger = Logger("GlobalSettings")

    private val store: KVStore = KVStore

    // java.lang.UnsatisfiedLinkError: dlopen failed:
    // couldn't map "/data/app/tornaco.apps.shortx-6nJ5nMnybZooF0oXnBorZA==/base.apk!/lib/arm64-v8a/libservices.so" segment 0: Permission denied
    fun init(context: Context) {
        logger.w("Init.")
        store.init(
            File(
                context.cacheDir,
                STORE_FILE_NAME
            ).absolutePath.also { logger.d("Path: $it") })
        store.putString("test", "test-value")
        logger.logOnDebug("KVStore: ${store.getString("test")}")
        store.putBool("test-bool", true)
        logger.logOnDebug("KVStore: ${store.getBool("test-bool")}")
        store.putInt("test-int", 123)
        logger.logOnDebug("KVStore: ${store.getInt("test-int")}")
        logger.logOnDebug("KVStore: ${store.getInt("test-int-no-exists", 333)}")
    }

    val isEdgeGestureEnabled get() = store.getBool(KEY_GESTURE_ENABLED, true)

    fun setEdgeGestureEnabled(enabled: Boolean) {
        store.putBool(KEY_GESTURE_ENABLED, enabled)
        logger.logOnDebug { "setEdgeGestureEnabled: $enabled" }
    }
}
package tornaco.apps.shortx.services.data

import android.content.Context
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.j.KVStore

object GlobalSettings {
    private const val STORE_FILE_NAME = "global"
    private val logger = Logger("GlobalSettings")

    val store: KVStore = KVStore

    // java.lang.UnsatisfiedLinkError: dlopen failed:
    // couldn't map "/data/app/tornaco.apps.shortx-6nJ5nMnybZooF0oXnBorZA==/base.apk!/lib/arm64-v8a/libservices.so" segment 0: Permission denied
    fun init(context: Context) {
        logger.w("Init.")
        store.init(Dirs.dataStoreFile(STORE_FILE_NAME).absolutePath)
        store.putString("test", "test-value")
        logger.logOnDebug("KVStore: ${store.getString("test")}")
        store.putBool("test-bool", true)
        logger.logOnDebug("KVStore: ${store.getBool("test-bool")}")
        store.putInt("test-int", 123)
        logger.logOnDebug("KVStore: ${store.getInt("test-int")}")
        logger.logOnDebug("KVStore: ${store.getInt("test-int-no-exists", 333)}")
    }
}
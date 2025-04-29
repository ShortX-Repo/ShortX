package tornaco.apps.shortx.ui

import android.content.Context
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.j.KVStore
import java.io.File

fun devTest(context: Context) {
    if (BuildPropInternal.APP_IS_DEBUG) {
        KVStore.init(File(context.externalCacheDir, "kv.txt").absolutePath)
        KVStore.putString("test", "test-value")
        println("KVStore: ${KVStore.getString("test")}")
        KVStore.putBool("test-bool", true)
        println("KVStore: ${KVStore.getBool("test-bool")}")
        KVStore.putInt("test-int", 123)
        println("KVStore: ${KVStore.getInt("test-int")}")
        println("KVStore: ${KVStore.getInt("test-int-no-exists", 333)}")
    }
}
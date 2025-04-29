package tornaco.apps.shortx.j

import java.util.concurrent.atomic.AtomicBoolean

object KVStore {
    init {
        System.loadLibrary("services")
    }

    private val initialized = AtomicBoolean(false)

    fun init(savePath: String) {
        if (initialized.compareAndSet(false, true)) {
            nativeInit(savePath)
        }
    }

    fun putString(key: String, value: String) {
        checkInit()
        nativePutString(key, value)
    }

    fun putInt(key: String, value: Int) {
        checkInit()
        nativePutInt(key, value)
    }

    fun putBool(key: String, value: Boolean) {
        checkInit()
        nativePutBool(key, value)
    }

    fun getString(key: String, defaultValue: String = ""): String {
        checkInit()
        return nativeGetString(key, defaultValue)
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        checkInit()
        return nativeGetInt(key, defaultValue)
    }

    fun getBool(key: String, defaultValue: Boolean = false): Boolean {
        checkInit()
        return nativeGetBool(key, defaultValue)
    }

    private fun checkInit() {
        if (!initialized.get()) {
            throw IllegalStateException("KVS is not initialized. Call init(savePath) first.")
        }
    }

    // Native 方法
    private external fun nativeInit(path: String)
    private external fun nativePutString(key: String, value: String)
    private external fun nativePutInt(key: String, value: Int)
    private external fun nativePutBool(key: String, value: Boolean)
    private external fun nativeGetString(key: String, defaultValue: String): String
    private external fun nativeGetInt(key: String, defaultValue: Int): Int
    private external fun nativeGetBool(key: String, defaultValue: Boolean): Boolean
}

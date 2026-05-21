package tornaco.apps.shortx.services.xposed.hooks.hook

import android.util.Log

object XposedLog {
    fun log(message: String) {
        val result = runCatching<Unit> {
            Api101Runtime.xposed.log(Log.INFO, "ShortX", message)
        }
        result.onFailure {
            Log.i("ShortX.Xposed", message)
        }
    }

    fun log(message: String, throwable: Throwable) {
        val result = runCatching<Unit> {
            Api101Runtime.xposed.log(Log.ERROR, "ShortX", message, throwable)
        }
        result.onFailure {
            Log.e("ShortX.Xposed", message, throwable)
        }
    }
}

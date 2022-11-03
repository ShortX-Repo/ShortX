package tornaco.apps.shortx.core.util

import android.util.Log

class Logger(private val tag: String) {
    private val fullTag get() = "ShortX-$tag"

    fun d(any: Any?) {
        Log.d(fullTag, "$any")
    }

    fun i(any: Any?) {
        Log.i(fullTag, "$any")
    }

    fun w(any: Any?) {
        Log.w(fullTag, "$any")
    }

    fun e(any: Any?) {
        Log.e(fullTag, "$any")
    }

    fun e(throwable: Throwable, any: Any?) {
        Log.e(fullTag, "$any", throwable)
    }
}
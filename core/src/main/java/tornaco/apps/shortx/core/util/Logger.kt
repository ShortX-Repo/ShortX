package tornaco.apps.shortx.core.util

import android.util.Log
import tornaco.apps.shortx.core.BuildProp

const val LOG_LEVEL_P = 200

class Logger(private val tag: String = "-") {
    private val fullTag get() = tag

    fun p(any: Any?) {
        if (BuildProp.APP_IS_DEBUG) logAdapter(
            LOG_LEVEL_P,
            fullTag,
            "[${Thread.currentThread().name}] $any"
        )
    }

    fun d(any: Any?) {
        logAdapter(Log.DEBUG, fullTag, "[${Thread.currentThread().name}] $any")
    }

    fun i(any: Any?) {
        logAdapter(Log.INFO, fullTag, "[${Thread.currentThread().name}] $any")
    }

    fun w(any: Any?) {
        logAdapter(Log.WARN, fullTag, "[${Thread.currentThread().name}] $any")
    }

    fun e(any: Any?) {
        logAdapter(Log.ERROR, fullTag, "[${Thread.currentThread().name}] $any")
    }

    fun e(throwable: Throwable, any: Any?) {
        logAdapter(
            Log.ERROR,
            fullTag,
            "[${Thread.currentThread().name}] $any --- ${throwable.stackTraceToString()}"
        )
    }
}

var logAdapter: (level: Int, String, String) -> Unit = { level: Int, tag: String, msg: String ->
    Log.println(level, tag, msg)
}
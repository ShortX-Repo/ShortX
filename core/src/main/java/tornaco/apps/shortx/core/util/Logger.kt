package tornaco.apps.shortx.core.util

import android.util.Log
import tornaco.apps.shortx.core.BuildProp

class Logger(private val tag: String = "-") {
    private val fullTag get() = tag

    fun d(any: Any?) {
        if (BuildProp.APP_IS_DEBUG) {
            logAdapter(Log.DEBUG, fullTag, "$any")
        }
    }

    fun i(any: Any?) {
        if (BuildProp.APP_IS_DEBUG) {
            logAdapter(Log.INFO, fullTag, "$any")
        }
    }

    fun w(any: Any?) {
        logAdapter(Log.WARN, fullTag, "$any")
    }

    fun e(any: Any?) {
        logAdapter(Log.ERROR, fullTag, "$any")
    }

    fun e(throwable: Throwable, any: Any?) {
        logAdapter(Log.ERROR, fullTag, "$any --- ${Log.getStackTraceString(throwable)}")
    }
}

var logAdapter: (level: Int, String, String) -> Unit = { level: Int, tag: String, msg: String ->
    Log.println(level, tag, msg)
}
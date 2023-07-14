package tornaco.apps.shortx.services.xposed.hooks.hook

import android.util.Log
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.util.Logger
import util.Reflections

object RuntimeInitHook {
    private val logger = Logger("ShortXHook.RuntimeInitHook")

    fun hookUncaughtException() = kotlin.runCatching {
        beforeMethod(
            Reflections.findClass(
                "com.android.internal.os.RuntimeInit\$LoggingHandler",
                null
            ),
            "uncaughtException"
        ) { param ->
            logger.d("RuntimeInit uncaughtException: ${param.args.map { it }}")
            // uncaughtException(Thread t, Throwable e)
            ShortXManagerNative.shortX?.uncaughtException(Log.getStackTraceString(param.args[1] as Throwable?))
        }
    }.onFailure {
        logger.e(it, "hookUncaughtException")
    }
}


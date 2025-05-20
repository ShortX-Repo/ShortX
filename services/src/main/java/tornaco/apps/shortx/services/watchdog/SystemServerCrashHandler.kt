package tornaco.apps.shortx.services.watchdog

import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.data.Dirs
import java.io.File
import java.io.IOException

object SystemServerCrashHandler {
    fun install() {
        Logger.nameless.w("SystemServerCrashHandler, install.")
        val currentHandler = Thread.getDefaultUncaughtExceptionHandler()
        Logger.nameless.w("SystemServerCrashHandler, currentHandler: $currentHandler")
        val wrappedHandler = Thread.UncaughtExceptionHandler { t, e ->
            kotlin.runCatching { handleCrash(t, e) }
            kotlin.runCatching { currentHandler.uncaughtException(t, e) }
        }
        Thread.setDefaultUncaughtExceptionHandler(wrappedHandler)
    }

    private fun handleCrash(t: Thread?, e: Throwable?) {
        Logger.nameless.e("*** SystemServerCrashHandler FATAL EXCEPTION IN SYSTEM PROCESS: ")
        Logger.nameless.e("SystemServerCrashHandler, t: $t, e: ${e?.stackTraceToString()}")

        // Dump.
        val logFile = File(
            Dirs.baseServerLogDir(), ("log/crash/"
                    + "SYSTEM_SERVER_CRASH_" + DateUtils.formatForFileName(System.currentTimeMillis()) + ".log")
        )
        Logger.nameless.w("SystemServerCrashHandler Writing to log file: $logFile")
        try {
            logFile.parentFile?.mkdirs()
            logFile.writeText(e?.stackTraceToString() ?: "NO_ERROR_STACK-$e")
            Logger.nameless.w("SystemServerCrashHandler Write complete to log file $logFile")
        } catch (e: IOException) {
            Logger.nameless.e(e, "SystemServerCrashHandler Fail write log file")
        }
    }
}
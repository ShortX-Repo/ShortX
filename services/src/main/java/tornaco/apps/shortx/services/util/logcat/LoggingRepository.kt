package tornaco.apps.shortx.services.util.logcat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildProp.LOG_PREFIX
import tornaco.apps.shortx.services.ShortXContext

class LoggingRepository(
    private val context: ShortXContext,
    private val scope: CoroutineScope
) {

    companion object {
        val COMMAND = arrayOf("logcat", "-v", "uid", "-v", "epoch")

        val DUMP_FLAG = arrayOf("-d")
        val SHOW_LOGS_FROM_NOW_FLAGS = arrayOf("-T", "1")
    }

    val logsFlow = MutableStateFlow<LogLine?>(null)

    private var loggingJob: Job? = null

    private var idsCounter = -1L

    fun startLoggingIfNot() {
        if (loggingJob?.isActive == true) return

        loggingJob = scope.launch {
            while (isActive) {
                readLogs()
            }
        }
    }

    fun restartLogging() = scope.launch {
        stopLogging()
        startLoggingIfNot()
    }

    suspend fun stopLogging() = coroutineScope {
        loggingJob?.cancel()
        clearLogs()
    }

    fun clearLogs() = scope.launch {
        logsFlow.update {
            null
        }
    }

    private suspend fun readLogs() = coroutineScope {
        val command = COMMAND + when (true) {
            true -> SHOW_LOGS_FROM_NOW_FLAGS
            else -> emptyArray()
        }

        val process = DefaultTerminal().execute(*command)

        try {
            process.output.bufferedReader().useLines {
                var droppedFirst = false // !appPreferences.showLogsFromAppLaunch
                // avoiding getting the same line after logging restart because of
                // WARNING: -T 0 invalid, setting to 1
                for (line in it) {
                    if (!isActive) break
                    if (line.contains(LOG_PREFIX)) continue

                    val logLine = LogLine(idsCounter++, line, context) ?: continue
                    if (!droppedFirst) {
                        droppedFirst = true
                        continue
                    }

                    logsFlow.update { logLine }
                }
            }

            process.destroy()
        } catch (e: Exception) {
            // closed or dead
        }
    }
}
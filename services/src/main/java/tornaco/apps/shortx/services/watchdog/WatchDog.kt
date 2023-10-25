package tornaco.apps.shortx.services.watchdog

import android.os.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.common.SensorUsage
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.ShortXService.shortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Dirs
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

class WatchDogService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "WatchDogService") {
    private val fatalErrors = mutableListOf<Throwable>()
    private val sensorUsages = mutableMapOf<Int, List<String>>()

    override fun systemReady() {
        super.systemReady()
    }

    fun querySensorUsage(): List<SensorUsage> {
        return sensorUsages.filterValues { it.isNotEmpty() }.map {
            SensorUsage.newBuilder().setSensor(it.key).addAllSrcId(it.value).build()
        }
    }

    fun onSensorUsage(sensor: Int, srcId: List<String>) {
        sensorUsages[sensor] = srcId
        logger.i("onSensorUsage: $sensor $srcId")
    }

    fun removeSensorUsage(sensor: Int) {
        sensorUsages.remove(sensor)
    }

    fun onFatalError(error: Throwable, method: String = "") {
        fatalErrors.add(error)
        logger.e(error, "FATAL: $method got error in SYSTEM PROCESS")
        // Write to file
        val logFile = File(
            File(Dirs.baseServerLogDir(), "fatal"),
            "$method-${DateUtils.formatForFileName(System.currentTimeMillis())}.log"
        )
        logFile.parentFile.mkdirs()
        logFile.writeText(error.stackTraceToString())
    }

    fun hasFatalError() = fatalErrors.isNotEmpty()
}

fun SystemService.postSafety(delay: Long = 0L, runnable: () -> Unit) {
    if (delay == 0L) {
        handler.post {
            kotlin.runCatching { runnable() }.onFailure {
                this.shortXContext.watchDog.onFatalError(it)
            }
        }
    } else {
        handler.postDelayed({
            kotlin.runCatching { runnable() }.onFailure {
                this.shortXContext.watchDog.onFatalError(it)
            }
        }, delay)
    }
}

fun Handler.postSafety(delay: Long = 0L, runnable: () -> Unit) {
    if (delay == 0L) {
        post {
            kotlin.runCatching { runnable() }.onFailure {
                // TODO Ref Static
                shortXContext.watchDog.onFatalError(it)
            }
        }
    } else {
        postDelayed({
            kotlin.runCatching { runnable() }.onFailure {
                shortXContext.watchDog.onFatalError(it)
            }
        }, delay)
    }
}

fun CoroutineScope.launchSafety(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
): Job = launch(context = context, start = start, block = {
    kotlin.runCatching { block() }.onFailure {
        if (it is CancellationException) {
            // Ignore this error
        } else {
            shortXContext.watchDog.onFatalError(it)
        }
    }
})
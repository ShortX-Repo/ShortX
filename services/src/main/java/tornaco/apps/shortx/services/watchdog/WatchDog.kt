package tornaco.apps.shortx.services.watchdog

import android.os.Handler
import android.os.RemoteCallbackList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.ISensorUsageListener
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.proto.action.LaunchApp
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.SensorUsage
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.ShortXService.shortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.ui.i18NIns
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

class WatchDogService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "WatchDogService") {
    private val fatalErrors = mutableListOf<Throwable>()
    private val sensorUsages = mutableMapOf<Int, List<String>>()
    private val sensorUsageListeners = RemoteCallbackList<ISensorUsageListener>()

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
        systemServiceScope.launchSafety {
            sensorUsageListeners.broadcast {
                kotlin.runCatching { it.onUpdate() }
                    .onFailure { logger.e(it, "call onUpdate") }
            }
        }
    }

    fun removeSensorUsage(sensor: Int) {
        sensorUsages.remove(sensor)
        systemServiceScope.launchSafety {
            sensorUsageListeners.broadcast {
                kotlin.runCatching { it.onUpdate() }
                    .onFailure { logger.e(it, "call onUpdate") }
            }
        }
    }

    fun registerSensorUsageListener(listener: ISensorUsageListener) {
        sensorUsageListeners.register(listener)
    }

    fun unregisterSensorUsageListener(listener: ISensorUsageListener) {
        sensorUsageListeners.unregister(listener)
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

        // Post N
        shortXContext.notification.post(
            PostNotification.newBuilder()
                .setTitle(i18NIns["ui.settings.fatal.error.title"])
                .setMessage(i18NIns["ui.settings.fatal.error.message"])
                .setId("ShortX.WatchDog")
                .setIsImportant(true)
                .setSmallIcon(Remix.System.alert_fill)
                .addClickAction(
                    LaunchApp.newBuilder()
                        .setId("ShortX.WatchDog")
                        .setAppPkg(
                            AppPkg.newBuilder().setUserId(myUserId)
                                .setPkgName(BuildProp.APP_PACKAGE_NAME).build()
                        )
                        .build().pack_()
                )
                .build()
        )
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
package tornaco.apps.shortx.services

import android.os.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tornaco.apps.shortx.services.ShortXService.shortXContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class WatchDogService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val fatalErrors = mutableListOf<Throwable>()

    override fun systemReady() {
        super.systemReady()
    }

    fun onFatalError(error: Throwable, method: String = "") {
        logger.e(error, "FATAL: $method got error in SYSTEM PROCESS")
        fatalErrors.add(error)
    }
}

fun Handler.postSafety(delay: Long = 0L, runnable: () -> Unit) {
    if (delay == 0L) {
        post {
            kotlin.runCatching { runnable() }.onFailure {
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
        shortXContext.watchDog.onFatalError(it)
    }
})
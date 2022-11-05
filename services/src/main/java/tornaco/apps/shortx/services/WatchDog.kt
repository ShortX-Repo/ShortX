package tornaco.apps.shortx.services

import android.os.Handler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class WatchDogService : SystemService() {
    private val fatalErrors = mutableListOf<Throwable>()

    override fun systemReady() {
        super.systemReady()
    }

    fun onFatalError(error: Throwable) {
        logger.e(error, "FATAL: got error in SYSTEM PROCESS")
        fatalErrors.add(error)
    }
}

fun Handler.postSafety(runnable: () -> Unit) {
    post {
        kotlin.runCatching { runnable() }.onFailure {
            ServiceManager.watchDog.onFatalError(it)
        }
    }
}

fun CoroutineScope.launchSafety(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
): Job = launch(context = context, start = start, block = {
    kotlin.runCatching { block() }.onFailure {
        ServiceManager.watchDog.onFatalError(it)
    }
})
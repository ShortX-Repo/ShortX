package tornaco.apps.shortx.services.gesture.bpok.gesture

import android.os.Handler
import tornaco.apps.shortx.core.util.Logger

class GestureTimerController(
    private val handler: Handler
) {
    private val logger = Logger("GestureTimerController")

    private var longPressRunnable: Runnable? = null
    private var doubleTapRunnable: Runnable? = null

    fun startLongPressTimer(timeoutMs: Long, onExpire: () -> Unit) {
        cancelLongPressTimer()
        val runnable = Runnable {
            logger.d("Long press timer expired")
            onExpire()
        }
        longPressRunnable = runnable
        handler.postDelayed(runnable, timeoutMs)
    }

    fun startDoubleTapTimer(timeoutMs: Long, onExpire: () -> Unit) {
        cancelDoubleTapTimer()
        val runnable = Runnable {
            logger.d("Double tap timer expired")
            onExpire()
        }
        doubleTapRunnable = runnable
        handler.postDelayed(runnable, timeoutMs)
    }

    fun cancelLongPressTimer() {
        longPressRunnable?.let { handler.removeCallbacks(it) }
        longPressRunnable = null
    }

    fun cancelDoubleTapTimer() {
        doubleTapRunnable?.let { handler.removeCallbacks(it) }
        doubleTapRunnable = null
    }

    fun cancelAll() {
        cancelLongPressTimer()
        cancelDoubleTapTimer()
    }
}

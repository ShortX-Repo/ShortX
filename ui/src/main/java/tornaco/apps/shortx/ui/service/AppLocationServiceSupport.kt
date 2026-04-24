package tornaco.apps.shortx.ui.service

import android.os.Handler
import android.os.Looper
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.util.Logger
import java.util.concurrent.Executors

internal object AppLocationServiceSupport {
    private const val DEFAULT_TIMEOUT_MILLIS = 10000L

    private val logger = Logger("AppLocationService")
    private val executors = Executors.newCachedThreadPool()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun getCurrentLocationInfo(
        service: AppLocationService,
        requestId: String,
        receiver: SynchronousResultReceiver,
        requestData: ByteArrayWrapper,
    ) {
        executors.execute {
            runCatching {
                fail("not implemented")
            }.onFailure {
                logger.e(it, "getCurrentLocationInfo failed")
                receiver.propagateException(RuntimeException(it))
            }
        }
    }

    fun getCurrentLocationAddress(
        service: AppLocationService,
        requestId: String,
        receiver: SynchronousResultReceiver,
        requestData: ByteArrayWrapper,
    ) {
        executors.execute {
            runCatching {
                fail("not implemented")
            }.onFailure {
                logger.e(it, "getCurrentLocationAddress failed")
                receiver.propagateException(RuntimeException(it))
            }
        }
    }

    private fun fail(message: String, cause: Throwable? = null): Nothing {
        if (cause != null) {
            logger.e(cause, message)
            throw IllegalStateException(message, cause)
        }
        logger.e(message)
        throw IllegalStateException(message)
    }
}
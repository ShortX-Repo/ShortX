package tornaco.apps.shortx.services.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.util.Logger
import java.time.Duration
import java.util.Objects
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean


abstract class AppServiceProxy(
    private val mContext: Context,
    private val mIntent: Intent,
    private val scope: CoroutineScope
) {
    private val logger = Logger("AppServiceProxy")

    private val mConnection: ServiceConnection = ProxyConnection()
    private var mTask: ProxyTask? = null
    private var mName = " unnamed"

    private var mStartTime: Long = 0
    private var mTaskSet = false
    private var mTaskCompleted = AtomicBoolean(false)

    /**
     * This function is called after the proxy connects to the service but before it runs its task.
     * Subclasses must override this to store the binder correctly.
     *
     * @param binder The service IBinder.
     */
    abstract fun onConnected(binder: IBinder)

    @Throws(IllegalStateException::class)
    protected fun setTask(task: ProxyTask): Boolean {
        return setTask(task, "NO-NAME")
    }

    @Throws(IllegalStateException::class)
    protected fun setTask(task: ProxyTask, name: String): Boolean {
        if (mTaskSet) {
            throw IllegalStateException("Cannot call setTask twice on the same ServiceProxy.")
        }
        mTaskSet = true
        mName = name
        mTask = task
        mStartTime = System.currentTimeMillis()
        if (DEBUG_PROXY) {
            logger.d("Bind requested for task $mName")
        }
        return mContext.bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE)
    }

    @Throws(TimeoutException::class)
    suspend fun waitForCompletion(timeout: Duration) {
        return withContext(Dispatchers.Default) {
            Objects.requireNonNull(timeout, "Null timeout is not allowed")
            val startWaitNanoTime = SystemClock.elapsedRealtimeNanos()
            var remainingTime = timeout
            while (!remainingTime.isNegative && isActive) {
                if (mTaskCompleted.get()) {
                    return@withContext
                }
                delay(100)
                remainingTime = timeout.minus(
                    Duration.ofNanos(SystemClock.elapsedRealtimeNanos() - startWaitNanoTime)
                )
            }
            throw TimeoutException()
        }
    }

    interface ProxyTask {
        suspend fun run()
    }

    private inner class ProxyConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            if (DEBUG_PROXY) {
                logger.d(
                    ("Connected: " + name.shortClassName + " at " +
                            (System.currentTimeMillis() - mStartTime) + "ms")
                )
            }

            onConnected(binder)
            scope.launch {
                logger.d("Call proxy")
                call()
            }
        }

        private suspend fun call() {
            try {
                mTask?.run()
            } catch (e: RemoteException) {
                logger.d("RemoteException thrown running mTask!")
            } finally {
                // Make sure that we unbind the mConnection even on exceptions in the
                // task provided by the subclass.
                try {
                    // Each ServiceProxy handles just one task, so we unbind after we're
                    // done with our work.
                    mContext.unbindService(mConnection)
                } catch (e: RuntimeException) {
                    // The exceptions that are thrown here look like IllegalStateException,
                    // IllegalArgumentException and RuntimeException. Catching
                    // RuntimeException which get them all. Reasons for these exceptions
                    // include services that have already been stopped or unbound. This can
                    // happen if the user ended the activity that was using the service.
                    // This is harmless, but we've got to catch it.
                    logger.d("RuntimeException when trying to unbind delegate service")
                }
            }
            mTaskCompleted.set(true)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            if (DEBUG_PROXY) {
                logger.d(
                    ("Disconnected: " + name.shortClassName + " at " +
                            (System.currentTimeMillis() - mStartTime) + "ms")
                )
            }
        }
    }

    companion object {
        private const val DEBUG_PROXY = BuildProp.APP_IS_DEBUG
    }
}
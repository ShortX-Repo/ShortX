package tornaco.apps.shortx.core.os

import android.os.Parcel
import android.os.Parcelable
import android.os.RemoteException
import android.os.SystemClock
import android.util.Log
import com.android.internal.annotations.GuardedBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import java.time.Duration
import java.util.Objects
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class SynchronousResultReceiver : Parcelable {
    private var mLocal = false
    private var mIsCompleted = false

    @GuardedBy("sLock")
    private var mFuture: CompletableFuture<Result>? = CompletableFuture()

    companion object {
        const val TAG = "SynchronousResultReceiver"
        const val QUEUE_THRESHOLD = 4

        private val sLock = Any()

        @GuardedBy("sLock")
        private val sAvailableReceivers = ConcurrentLinkedQueue<SynchronousResultReceiver>()

        fun get(): SynchronousResultReceiver {
            synchronized(sLock) {
                if (sAvailableReceivers.isEmpty()) {
                    return SynchronousResultReceiver()
                }
                val receiver =
                    sAvailableReceivers.poll()
                receiver.resetLocked()
                return receiver
            }
        }

        @JvmField
        val CREATOR: Parcelable.Creator<SynchronousResultReceiver?> =
            object : Parcelable.Creator<SynchronousResultReceiver?> {
                override fun createFromParcel(`in`: Parcel): SynchronousResultReceiver {
                    return SynchronousResultReceiver(`in`)
                }

                override fun newArray(size: Int): Array<SynchronousResultReceiver?> {
                    return arrayOfNulls(size)
                }
            }
    }

    private constructor() {
        mLocal = true
        mIsCompleted = false
    }

    private constructor(p: Parcel) {
        mLocal = false
        mIsCompleted = false
        mReceiver = ISynchronousResultReceiver.Stub.asInterface(p.readStrongBinder())
    }

    @GuardedBy("sLock")
    private fun releaseLocked() {
        mFuture = null
        if (sAvailableReceivers.size < QUEUE_THRESHOLD) {
            sAvailableReceivers.add(this)
        }
    }

    @GuardedBy("sLock")
    private fun resetLocked() {
        mFuture = CompletableFuture()
        mIsCompleted = false
    }

    private fun getFuture(): CompletableFuture<Result>? {
        synchronized(sLock) { return mFuture }
    }


    class Result : Parcelable {
        private val mObject: ByteArrayWrapper?
        private val mErrorMessage: String?

        constructor(exception: String?) {
            mObject = null
            mErrorMessage = exception
        }

        constructor(`object`: ByteArrayWrapper?) {
            mObject = `object`
            mErrorMessage = null
        }

        /**
         * Return the stored value
         * May throw a [RuntimeException] thrown from the client
         */
        fun getValue(defaultValue: ByteArrayWrapper?): ByteArrayWrapper? {
            if (mErrorMessage != null) {
                throw RuntimeException(mErrorMessage)
            }
            return mObject ?: defaultValue
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            out.writeParcelable(mObject, 0)
            out.writeString(mErrorMessage)
        }

        private constructor(`in`: Parcel) {
            mObject =
                `in`.readParcelable<Parcelable>(ByteArrayWrapper::class.java.classLoader) as ByteArrayWrapper
            mErrorMessage = `in`.readString()
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Result> = object : Parcelable.Creator<Result> {
                override fun createFromParcel(`in`: Parcel): Result {
                    return Result(`in`)
                }

                override fun newArray(size: Int): Array<Result?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }


    private fun complete(result: Result) {
        check(!mIsCompleted) { "Receiver has already been completed" }
        mIsCompleted = true
        if (mLocal) {
            getFuture()!!.complete(result)
        } else {
            val rr: ISynchronousResultReceiver?
            synchronized(this) { rr = mReceiver }
            if (rr != null) {
                try {
                    rr.send(result)
                } catch (e: RemoteException) {
                    Log.w(TAG, "Failed to complete future")
                }
            }
        }
    }

    /**
     * Deliver a result to this receiver.
     *
     * @param resultData Additional data provided by you.
     */
    fun send(resultData: ByteArrayWrapper?) {
        complete(Result(resultData))
    }

    /**
     * Deliver an [Exception] to this receiver
     *
     * @param e exception to be sent
     */
    fun propagateException(e: RuntimeException) {
        Objects.requireNonNull(e, "RuntimeException cannot be null")
        complete(Result(Log.getStackTraceString(e)))
    }

    /**
     * Blocks waiting for the result from the remote client.
     *
     *
     * If it is interrupted before completion of the duration, wait again with remaining time until
     * the deadline.
     *
     * @param timeout The duration to wait before sending a [TimeoutException]
     * @return the Result
     * @throws TimeoutException if the timeout in milliseconds expired.
     */
    @Throws(TimeoutException::class)
    suspend fun awaitResultNoInterrupt(timeout: Duration): Result {
        return withContext(Dispatchers.Default) {
            Objects.requireNonNull(timeout, "Null timeout is not allowed")
            val startWaitNanoTime = SystemClock.elapsedRealtimeNanos()
            var remainingTime = timeout
            while (!remainingTime.isNegative && isActive) {
                try {
                    val result = getFuture()!![remainingTime.toMillis(), TimeUnit.MILLISECONDS]
                    synchronized(sLock) {
                        releaseLocked()
                        return@withContext result
                    }
                } catch (e: ExecutionException) {
                    // This will NEVER happen.
                    throw AssertionError("Error receiving response", e)
                } catch (e: InterruptedException) {
                    // The thread was interrupted, try and get the value again, this time
                    // with the remaining time until the deadline.
                    remainingTime = timeout.minus(
                        Duration.ofNanos(SystemClock.elapsedRealtimeNanos() - startWaitNanoTime)
                    )
                }
            }
            synchronized(sLock) { releaseLocked() }
            throw TimeoutException()
        }
    }

    var mReceiver: ISynchronousResultReceiver? = null

    private class MyResultReceiver(val receiver: SynchronousResultReceiver) :
        ISynchronousResultReceiver.Stub() {
        override fun send(result: Result) {
            receiver.getFuture()?.complete(result)
        }
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        synchronized(this) {
            if (mReceiver == null) {
                mReceiver = MyResultReceiver(this)
            }
            out.writeStrongBinder(mReceiver!!.asBinder())
        }
    }
}
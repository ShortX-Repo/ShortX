package tornaco.apps.shortx.services

import android.content.Context
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.rule.action.emptyByteArrayWrapper
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.RemoteCallbackListExt
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class ServiceLifecycle(private val shortXContext: ShortXContext) {
    private val logger = Logger("ServiceLifecycle")
    private val exe = Executors.newSingleThreadExecutor()

    private val lifecycleSystemReadyCallbacks = RemoteCallbackListExt<ICallback>()

    val isSystemReadyCalled: AtomicBoolean = AtomicBoolean(false)

    fun start(context: Context) {
        logger.d("start")
        exe.execute {
            shortXContext.allServices.forEach {
                val startTime = System.currentTimeMillis()
                it.start(context)
                val taken = System.currentTimeMillis() - startTime
                logger.i("start---> $it take $taken(ms)")
            }
        }
    }

    fun systemReady() {
        logger.d("systemReady")
        exe.execute {
            shortXContext.allServices.forEach {
                val startTime = System.currentTimeMillis()
                it.systemReady()
                val taken = System.currentTimeMillis() - startTime
                logger.i("systemReady---> $it take $taken(ms)")
            }
            isSystemReadyCalled.set(true)
            notifySystemReadyCallback()
        }
    }

    private fun notifySystemReadyCallback() {
        logger.logOnDebug("notifySystemReadyCallback.")
        synchronized(lifecycleSystemReadyCallbacks) {
            lifecycleSystemReadyCallbacks.broadcast {
                runCatching {
                    it.receive(
                        emptyByteArrayWrapper
                    )
                }.onFailure {
                    logger.e(it, "notifySystemReadyCallback")
                }
            }
        }
    }

    fun screenOn() {
        logger.d("screenOn")
        exe.execute {
            shortXContext.allServices.forEach {
                val startTime = System.currentTimeMillis()
                it.screenOn()
                val taken = System.currentTimeMillis() - startTime
                logger.i("screenOn---> $it take $taken(ms)")
            }
        }
    }

    fun screenOff() {
        logger.d("screenOff")
        exe.execute {
            shortXContext.allServices.forEach {
                val startTime = System.currentTimeMillis()
                it.screenOff()
                val taken = System.currentTimeMillis() - startTime
                logger.i("screenOff---> $it take $taken(ms)")
            }
        }
    }

    fun userPresent(isFirstTime: Boolean) {
        logger.d("userPresent: $isFirstTime")
        exe.execute {
            shortXContext.allServices.forEach {
                val startTime = System.currentTimeMillis()
                it.userPresent(isFirstTime)
                val taken = System.currentTimeMillis() - startTime
                logger.i("userPresent---> $it take $taken(ms)")
            }
        }
    }

    fun registerLifecycleSystemReadyCallback(callback: ICallback) {
        if (isSystemReadyCalled.get()) {
            kotlin.runCatching {
                callback.receive(emptyByteArrayWrapper)
            }.onFailure {
                logger.e(it, "notify LifecycleSystemReadyCallback")
            }
        } else {
            synchronized(lifecycleSystemReadyCallbacks) {
                lifecycleSystemReadyCallbacks.register(callback)
            }
        }
    }

    fun unregisterLifecycleSystemReadyCallback(callback: ICallback) {
        lifecycleSystemReadyCallbacks.unregister(callback)
    }
}
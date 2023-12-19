package tornaco.apps.shortx.services

import android.content.Context
import tornaco.apps.shortx.core.util.Logger
import java.util.concurrent.Executors

class ServiceLifecycle(private val shortXContext: ShortXContext) {
    private val logger = Logger("ServiceLifecycle")
    private val exe = Executors.newSingleThreadExecutor()

    var isSystemReadyCalled: Boolean = false
        private set

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
            isSystemReadyCalled = true
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
}
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
        exe.execute {
            shortXContext.allServices.forEach {
                val startTime = System.currentTimeMillis()
                it.start(context)
                val taken = System.currentTimeMillis() - startTime
                logger.i("start $it take $taken(ms)")
            }
        }
    }

    fun systemReady() {
       exe.execute {
           shortXContext.allServices.forEach {
               val startTime = System.currentTimeMillis()
               it.systemReady()
               val taken = System.currentTimeMillis() - startTime
               logger.i("systemReady $it take $taken(ms)")
           }
           isSystemReadyCalled = true
       }
    }
}
package tornaco.apps.shortx.services

import android.content.Context

class ServiceLifecycle(private val shortXContext: ShortXContext) {

    var isSystemReadyCalled: Boolean = false
        private set

    fun start(context: Context) {
        shortXContext.allServices.forEach { it.start(context) }
    }

    fun systemReady() {
        shortXContext.allServices.forEach { it.systemReady() }
        isSystemReadyCalled = true
    }
}
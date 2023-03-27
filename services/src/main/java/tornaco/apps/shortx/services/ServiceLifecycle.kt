package tornaco.apps.shortx.services

import android.content.Context

class ServiceLifecycle(private val shortXContext: ShortXContext) {
    fun start(context: Context) {
        shortXContext.allServices.forEach { it.start(context) }
    }

    fun systemReady() {
        shortXContext.allServices.forEach { it.systemReady() }
    }
}
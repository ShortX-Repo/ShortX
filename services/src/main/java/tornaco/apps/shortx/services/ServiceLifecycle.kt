package tornaco.apps.shortx.services

import android.content.Context

class ServiceLifecycle(shortXContext: ShortXContext) {
    private val allServices = listOf(
        shortXContext.appState,
        shortXContext.rule,
        shortXContext.gesture,
        shortXContext.watchDog,
    )

    fun start(context: Context) {
        allServices.forEach { it.start(context) }
    }

    fun systemReady() {
        allServices.forEach { it.systemReady() }
    }
}
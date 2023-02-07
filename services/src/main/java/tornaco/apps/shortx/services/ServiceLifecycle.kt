package tornaco.apps.shortx.services

import android.content.Context

class ServiceLifecycle(shortXContext: ShortXContext) {
    private val allServices = listOf(
        shortXContext.appState,
        shortXContext.pkgSet,
        shortXContext.rule,
        shortXContext.gesture,
        shortXContext.watchDog,
        shortXContext.overlay,
    )

    fun start(context: Context) {
        allServices.forEach { it.start(context) }
    }

    fun systemReady() {
        allServices.forEach { it.systemReady() }
    }
}
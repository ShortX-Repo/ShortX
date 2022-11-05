package tornaco.apps.shortx.services

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object ServiceManager {
    val edgeGestureService = EdgeGestureService()
    val watchDogService = WatchDogService()

    private val internalServices = listOf(edgeGestureService, watchDogService)

    fun start(context: Context) {
        internalServices.forEach { it.start(context) }
    }

    fun systemReady() {
        internalServices.forEach { it.systemReady() }
    }
}
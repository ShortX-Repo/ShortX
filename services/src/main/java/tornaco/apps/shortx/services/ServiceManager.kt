package tornaco.apps.shortx.services

import android.annotation.SuppressLint
import android.content.Context
import tornaco.apps.shortx.services.gesture.GestureService

@SuppressLint("StaticFieldLeak")
object ServiceManager {
    val gesture = GestureService()
    val watchDog = WatchDogService()

    private val internalServices = listOf(gesture, watchDog)

    fun start(context: Context) {
        internalServices.forEach { it.start(context) }
    }

    fun systemReady() {
        internalServices.forEach { it.systemReady() }
    }
}
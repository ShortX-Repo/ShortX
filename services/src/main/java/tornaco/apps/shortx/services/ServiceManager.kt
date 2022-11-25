package tornaco.apps.shortx.services

import android.annotation.SuppressLint
import android.content.Context
import tornaco.apps.shortx.services.gesture.GestureService
import tornaco.apps.shortx.services.rule.RuleService

@SuppressLint("StaticFieldLeak")
object ServiceManager {
    val rule = RuleService()
    val gesture = GestureService()
    val watchDog = WatchDogService()

    private val internalServices = listOf(rule, gesture, watchDog)

    fun start(context: Context) {
        internalServices.forEach { it.start(context) }
    }

    fun systemReady() {
        internalServices.forEach { it.systemReady() }
    }
}
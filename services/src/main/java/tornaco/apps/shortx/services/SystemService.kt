package tornaco.apps.shortx.services

import android.annotation.CallSuper
import android.content.Context

open class SystemService {
    val logger by lazy {
        Logger(this.javaClass.simpleName)
    }

    var isStarted: Boolean = false
        private set

    var isSystemReady: Boolean = false
        private set

    lateinit var context: Context
        private set

    @CallSuper
    open fun start(context: Context) {
        this.context = context
        isStarted = true
        logger.i("start: $this with context: $context")
    }

    @CallSuper
    open fun systemReady() {
        isSystemReady = true
        logger.i("systemReady: $this")
    }
}
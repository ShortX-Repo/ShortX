package tornaco.apps.shortx.services

import android.annotation.CallSuper
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.data.SettingsStore

open class SystemService {
    private val bagOfTags: MutableMap<String, Any> = mutableMapOf()

    val logger by lazy {
        Logger(this.javaClass.simpleName)
    }

    var isStarted: Boolean = false
        private set

    var isSystemReady: Boolean = false
        private set

    lateinit var context: Context
        private set

    val settings: SettingsStore by lazy {
        SettingsStore(this.javaClass.simpleName, systemServiceScope)
    }

    @CallSuper
    open fun start(context: Context) {
        this.context = context
        isStarted = true
        logger.i("start: $this with context: $context")
    }

    @CallSuper
    open fun systemReady() {
        settings.readBlocking()
        isSystemReady = true
        logger.i("systemReady: $this")
    }

    open fun shutdown() {
        systemServiceScope.cancel("System shutdown.")
    }

    internal fun <T> getTag(key: String): T? {
        synchronized(bagOfTags) {
            @Suppress("UNCHECKED_CAST") return bagOfTags[key] as T
        }
    }

    internal fun <T> addTagIfAbsent(key: String, value: T): T {
        synchronized(bagOfTags) {
            if (bagOfTags.containsKey(key).not()) {
                bagOfTags[key] = requireNotNull(value) { "value should not be null for key: $key" }
            }
        }
        return value
    }
}

val SystemService.systemServiceScope: CoroutineScope
    get() {
        val key = "systemServiceScope"
        val scope: CoroutineScope? = this.getTag(key)
        if (scope != null) {
            return scope
        }
        return addTagIfAbsent(key, CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate))
    }
